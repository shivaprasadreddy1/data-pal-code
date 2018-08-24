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
    private RentalRepository mockRentalRepository;
    @Mock
    private RentalTruckRepository mockRentalTruckRepository;
    @Captor
    private ArgumentCaptor<Rental> rentalCaptor;
    @Captor
    private ArgumentCaptor<RentalTruck> truckCaptor;

    private RentalService rentalService;

    @Before
    public void setUp() {
        rentalService = new RentalService(mockTruckAllocationService, mockRentalRepository, mockRentalTruckRepository, mockTruckSizeChart);
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
        List<RentalTruck> rentalTrucks = Arrays.asList(mock(RentalTruck.class), mock(RentalTruck.class));
        when(mockRentalTruckRepository.findAllByTruckSizeAndStatus(any(), any())).thenReturn(rentalTrucks);

        RentalTruck mockRentalTruck = mock(RentalTruck.class);
        when(mockRentalTruck.getVin()).thenReturn(Vin.of("some-vin"));
        String customerName = "some-customer-name";
        when(mockTruckAllocationService.allocateTruck(any(TruckSize.class))).thenReturn(mockRentalTruck);


        rentalService.create(customerName, TruckSize.LARGE);


        InOrder inOrder = inOrder(mockRentalTruck, mockTruckAllocationService, mockRentalTruckRepository, mockRentalRepository);
        inOrder.verify(mockTruckAllocationService).allocateTruck(TruckSize.LARGE);
        inOrder.verify(mockRentalTruck).reserve();
        inOrder.verify(mockRentalTruckRepository).save(truckCaptor.capture());
        inOrder.verify(mockRentalRepository).save(rentalCaptor.capture());

        RentalTruck savedRentalTruck = truckCaptor.getValue();
        assertThat(savedRentalTruck).isSameAs(mockRentalTruck);

        Rental savedRental = rentalCaptor.getValue();
        assertThat(savedRental.getCustomerName()).isEqualTo(customerName);
        assertThat(savedRental.getTruckVin()).isEqualTo(Vin.of("some-vin"));
    }

    @Test
    public void pickUp() {
        RentalTruck mockRentalTruck = mock(RentalTruck.class);
        when(mockRentalTruck.getStatus()).thenReturn(RentalTruckStatus.RESERVED);
        when(mockRentalTruckRepository.findOne(any())).thenReturn(mockRentalTruck);

        Rental mockRental = mock(Rental.class);
        when(mockRental.getTruckVin()).thenReturn(Vin.of("some-vin"));
        when(mockRental.getDistanceTraveled()).thenReturn(null);
        when(mockRentalRepository.findOne(any())).thenReturn(mockRental);

        ConfirmationNumber confirmationNumber = mockRental.getConfirmationNumber();


        rentalService.pickUp(confirmationNumber);


        InOrder inOrder = inOrder(mockRental, mockRentalRepository, mockRentalTruck, mockRentalTruckRepository);
        inOrder.verify(mockRentalRepository).findOne(confirmationNumber);
        inOrder.verify(mockRental).pickUp();
        inOrder.verify(mockRentalRepository).save(mockRental);

        inOrder.verify(mockRentalTruckRepository).findOne(Vin.of("some-vin"));
        inOrder.verify(mockRentalTruck).pickUp();
        inOrder.verify(mockRentalTruckRepository).save(mockRentalTruck);

        verify(mockRental).getConfirmationNumber();
        verify(mockRental).getTruckVin();
        verifyNoMoreInteractions(mockRental, mockRentalTruck);
    }

    @Test
    public void dropOff() {
        Rental mockRental = mock(Rental.class);
        when(mockRentalRepository.findOne(any())).thenReturn(mockRental);

        RentalTruck mockRentalTruck = mock(RentalTruck.class);
        when(mockRentalTruck.getStatus()).thenReturn(RentalTruckStatus.RENTED);
        when(mockRentalTruckRepository.findOne(any())).thenReturn(mockRentalTruck);

        ConfirmationNumber confirmationNumber = ConfirmationNumber.newId();
        int distanceTraveled = 500;


        rentalService.dropOff(confirmationNumber, distanceTraveled);


        InOrder inOrder = inOrder(mockRental, mockRentalRepository, mockRentalTruck, mockRentalTruckRepository);
        inOrder.verify(mockRentalRepository).findOne(confirmationNumber);
        inOrder.verify(mockRental).dropOff(distanceTraveled);
        inOrder.verify(mockRentalRepository).save(rentalCaptor.capture());
        Rental savedRental = rentalCaptor.getValue();
        assertThat(savedRental).isSameAs(mockRental);

        inOrder.verify(mockRentalTruckRepository).findOne(any());
        inOrder.verify(mockRentalTruck).dropOff();
        inOrder.verify(mockRentalTruckRepository).save(truckCaptor.capture());
        RentalTruck savedRentalTruck = truckCaptor.getValue();
        assertThat(savedRentalTruck).isSameAs(mockRentalTruck);

        verify(mockRental).getTruckVin();
        verifyNoMoreInteractions(mockRental, mockRentalTruck);
    }

    @Test
    public void findAll() {
        Rental mockRental1 = mock(Rental.class);
        Rental mockRental2 = mock(Rental.class);
        List<Rental> toBeReturned = asList(mockRental1, mockRental2);
        when(mockRentalRepository.findAll()).thenReturn(toBeReturned);


        Collection<Rental> rentals = rentalService.findAll();


        verify(mockRentalRepository).findAll();

        Assertions.assertThat(rentals).hasSameElementsAs(toBeReturned);
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
        when(mockRentalRepository.findOne(any())).thenReturn(null);
        ConfirmationNumber confirmationNumber = ConfirmationNumber.newId();


        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> rentalService.pickUp(confirmationNumber))
                .withMessage(String.format("No rental found for id=%s", confirmationNumber));


        verify(mockRentalRepository).findOne(confirmationNumber);
        verifyNoMoreInteractions(mockRentalRepository);
    }

    @Test
    public void dropOff_whenNoRentalFound() {
        when(mockRentalRepository.findOne(any())).thenReturn(null);
        ConfirmationNumber confirmationNumber = ConfirmationNumber.newId();
        int distanceTraveled = 0;


        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> {
                    rentalService.dropOff(confirmationNumber, distanceTraveled);
                })
                .withMessage(String.format("No rental found for id=%s", confirmationNumber));


        verify(mockRentalRepository).findOne(confirmationNumber);
        verifyNoMoreInteractions(mockRentalRepository);
        verifyZeroInteractions(mockRentalTruckRepository);
    }
}
