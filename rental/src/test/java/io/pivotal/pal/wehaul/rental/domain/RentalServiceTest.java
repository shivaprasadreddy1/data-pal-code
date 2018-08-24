package io.pivotal.pal.wehaul.rental.domain;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class RentalServiceTest {

    @Mock
    private TruckAllocationService mockTruckAllocationService;
    @Mock
    private TruckSizeChart mockTruckSizeChart;
    @Mock
    private RentalTruckRepository mockRentalTruckRepository;
    @Captor
    private ArgumentCaptor<Rental> rentalCaptor;
    @Captor
    private ArgumentCaptor<RentalTruck> truckCaptor;

    private RentalService rentalService;

    @Before
    public void setUp() {
        rentalService = new RentalService(mockTruckAllocationService, mockRentalTruckRepository, mockTruckSizeChart);
    }

    @Test
    public void addTruck() {
        when(mockTruckSizeChart.getSizeByTruckLength(anyInt())).thenReturn(TruckSize.LARGE);


        rentalService.addTruck(Vin.of("some-vin"), 25);


        verify(mockTruckSizeChart).getSizeByTruckLength(25);
        verify(mockRentalTruckRepository).save(truckCaptor.capture());
        RentalTruck rentalTruck = truckCaptor.getValue();
        assertThat(rentalTruck.getVin()).isEqualTo(Vin.of("some-vin"));
        assertThat(rentalTruck.getTruckSize()).isEqualTo(TruckSize.LARGE);
    }

    @Test
    public void create() {
        RentalTruck mockRentalTruck = mock(RentalTruck.class);
        when(mockTruckAllocationService.allocateTruck(any(TruckSize.class))).thenReturn(mockRentalTruck);


        rentalService.create("some-customer-name", TruckSize.LARGE);


        InOrder inOrder = inOrder(mockTruckAllocationService, mockRentalTruck, mockRentalTruckRepository);
        inOrder.verify(mockTruckAllocationService).allocateTruck(TruckSize.LARGE);
        inOrder.verify(mockRentalTruck).reserve("some-customer-name");
        inOrder.verify(mockRentalTruckRepository).save(mockRentalTruck);
    }

    @Test
    public void pickUp() {
        RentalTruck mockRentalTruck = mock(RentalTruck.class);
        when(mockRentalTruck.getStatus()).thenReturn(RentalTruckStatus.RESERVED);
        when(mockRentalTruckRepository.findOneByRentalConfirmationNumber(any())).thenReturn(mockRentalTruck);
        ConfirmationNumber confirmationNumber = ConfirmationNumber.of("00000000-0000-0000-0000-000000000000");


        rentalService.pickUp(confirmationNumber);


        InOrder inOrder = inOrder(mockRentalTruckRepository, mockRentalTruck);
        inOrder.verify(mockRentalTruckRepository).findOneByRentalConfirmationNumber(UUID.fromString("00000000-0000-0000-0000-000000000000"));
        inOrder.verify(mockRentalTruck).pickUp();
        inOrder.verify(mockRentalTruckRepository).save(mockRentalTruck);
    }

    @Test
    public void dropOff() {
        RentalTruck mockRentalTruck = mock(RentalTruck.class);
        when(mockRentalTruck.getStatus()).thenReturn(RentalTruckStatus.RENTED);
        when(mockRentalTruckRepository.findOneByRentalConfirmationNumber(any())).thenReturn(mockRentalTruck);

        ConfirmationNumber confirmationNumber = ConfirmationNumber.of("00000000-0000-0000-0000-000000000000");
        int distanceTraveled = 500;


        rentalService.dropOff(confirmationNumber, distanceTraveled);


        InOrder inOrder = inOrder(mockRentalTruckRepository, mockRentalTruck);
        inOrder.verify(mockRentalTruckRepository).findOneByRentalConfirmationNumber(UUID.fromString("00000000-0000-0000-0000-000000000000"));
        inOrder.verify(mockRentalTruck).dropOff(distanceTraveled);
        inOrder.verify(mockRentalTruckRepository).save(mockRentalTruck);
    }

    @Test
    public void preventRenting() {
        RentalTruck mockTruck = mock(RentalTruck.class);
        when(mockRentalTruckRepository.findOne(any())).thenReturn(mockTruck);


        rentalService.preventRenting(Vin.of("some-vin"));


        verify(mockRentalTruckRepository).findOne(Vin.of("some-vin"));
        verify(mockTruck).preventRenting();
        verify(mockRentalTruckRepository).save(mockTruck);
    }

    @Test
    public void allowRenting() {
        RentalTruck mockTruck = mock(RentalTruck.class);
        when(mockRentalTruckRepository.findOne(any())).thenReturn(mockTruck);


        rentalService.allowRenting(Vin.of("best-vin"));


        verify(mockRentalTruckRepository).findOne(Vin.of("best-vin"));
        verify(mockTruck).allowRenting();
        verify(mockRentalTruckRepository).save(mockTruck);
    }

    @Test
    public void findAll() {
        RentalTruck mockRental1 = mock(RentalTruck.class);
        RentalTruck mockRental2 = mock(RentalTruck.class);
        List<RentalTruck> toBeReturned = asList(mockRental1, mockRental2);
        when(mockRentalTruckRepository.findAll()).thenReturn(toBeReturned);


        Collection<RentalTruck> rentals = rentalService.findAll();


        verify(mockRentalTruckRepository).findAll();

        Assertions.assertThat(rentals).hasSameElementsAs(toBeReturned);
    }

    @Test
    public void allowRenting_whenTruckNotFound() {
        when(mockRentalTruckRepository.findOne(any())).thenReturn(null);

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> rentalService.allowRenting(Vin.of("missing-vin")))
                .withMessage(String.format("No truck found with vin=%s", Vin.of("missing-vin")));
    }

    @Test
    public void preventRenting_whenTruckNotFound() {
        Vin missingVin = Vin.of("missing-vin");
        when(mockRentalTruckRepository.findOne(any())).thenReturn(null);


        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> rentalService.preventRenting(missingVin))
                .withMessage(String.format("No truck found with vin=%s", missingVin));
    }

    @Test
    public void pickUp_whenNoRentalFound() {
        when(mockRentalTruckRepository.findOne(any())).thenReturn(null);
        ConfirmationNumber confirmationNumber = ConfirmationNumber.of("00000000-0000-0000-0000-000000000000");


        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> rentalService.pickUp(confirmationNumber))
                .withMessage(String.format("No rental found for id=%s", confirmationNumber));


        verify(mockRentalTruckRepository).findOneByRentalConfirmationNumber(UUID.fromString("00000000-0000-0000-0000-000000000000"));
    }

    @Test
    public void dropOff_whenNoRentalFound() {
        when(mockRentalTruckRepository.findOneByRentalConfirmationNumber(any())).thenReturn(null);
        ConfirmationNumber confirmationNumber = ConfirmationNumber.of("00000000-0000-0000-0000-000000000000");
        int distanceTraveled = 0;


        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> {
                    rentalService.dropOff(confirmationNumber, distanceTraveled);
                })
                .withMessage(String.format("No rental found for id=%s", confirmationNumber));


        verify(mockRentalTruckRepository).findOneByRentalConfirmationNumber(UUID.fromString("00000000-0000-0000-0000-000000000000"));
    }
}
