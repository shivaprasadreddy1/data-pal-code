package io.pivotal.pal.wehaul.domain;

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
import java.util.Collections;
import java.util.List;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class RentalServiceTest {

    @Mock
    private RentalRepository mockRentalRepository;
    @Mock
    private TruckRepository mockTruckRepository;
    @Captor
    private ArgumentCaptor<Rental> rentalCaptor;
    @Captor
    private ArgumentCaptor<Truck> truckCaptor;

    private RentalService rentalService;

    @Before
    public void setUp() {
        rentalService = new RentalService(mockRentalRepository, mockTruckRepository);
    }

    @Test
    public void create() {

        Truck mockTruck = mock(Truck.class);
        Truck mockTruck1 = mock(Truck.class);

        when(mockTruck.getVin()).thenReturn(Vin.of("some-vin"));
        when(mockTruck.getStatus()).thenReturn(TruckStatus.RENTABLE);

        when(mockTruck.getVin()).thenReturn(Vin.of("some-vin"));
        when(mockTruck.getStatus()).thenReturn(TruckStatus.RENTABLE);
        String customerName = "some-customer-name";

        List<Truck> trucks = Arrays.asList(mockTruck, mockTruck1);
        when(mockTruckRepository.findAllByTruckSizeAndStatus(any(), any())).thenReturn(trucks);

        rentalService.create(customerName, TruckSize.LARGE);

        InOrder inOrder = inOrder(mockTruck, mockTruckRepository, mockRentalRepository);
        inOrder.verify(mockTruckRepository).save(truckCaptor.capture());
        inOrder.verify(mockRentalRepository).save(rentalCaptor.capture());

        Truck savedTruck = truckCaptor.getValue();
        assertThat(savedTruck).isSameAs(mockTruck);

        Rental savedRental = rentalCaptor.getValue();
        assertThat(savedRental.getCustomerName()).isEqualTo(customerName);
        assertThat(savedRental.getTruckVin()).isEqualTo(Vin.of("some-vin"));
    }

    @Test
    public void pickUp() {
        Truck mockTruck = mock(Truck.class);
        when(mockTruck.getStatus()).thenReturn(TruckStatus.RESERVED);
        when(mockTruckRepository.findOne(any())).thenReturn(mockTruck);

        Rental mockRental = mock(Rental.class);
        when(mockRental.getTruckVin()).thenReturn(Vin.of("some-vin"));
        when(mockRental.getDistanceTraveled()).thenReturn(null);
        when(mockRentalRepository.findOne(any())).thenReturn(mockRental);

        ConfirmationNumber confirmationNumber = mockRental.getConfirmationNumber();

        rentalService.pickUp(confirmationNumber);

        InOrder inOrder = inOrder(mockRental, mockRentalRepository, mockTruck, mockTruckRepository);


        inOrder.verify(mockRentalRepository).findOne(confirmationNumber);
        inOrder.verify(mockRentalRepository).save(mockRental);

        inOrder.verify(mockTruckRepository).findOne(Vin.of("some-vin"));
        inOrder.verify(mockTruckRepository).save(mockTruck);

        verify(mockTruck).getStatus();
        verify(mockTruck).setStatus(TruckStatus.RENTED);

        verify(mockRental).getConfirmationNumber();
        verify(mockRental).getDistanceTraveled();
        verify(mockRental).setDistanceTraveled(0);
        verify(mockRental).getTruckVin();
        verifyNoMoreInteractions(mockRental, mockTruck);
    }

    @Test
    public void dropOff() {
        Rental mockRental = mock(Rental.class);
        when(mockRentalRepository.findOne(any())).thenReturn(mockRental);

        Truck mockTruck = mock(Truck.class);
        when(mockTruck.getStatus()).thenReturn(TruckStatus.RENTED);
        when(mockTruck.getOdometerReading()).thenReturn(10_000);
        when(mockTruckRepository.findOne(any())).thenReturn(mockTruck);

        ConfirmationNumber confirmationNumber = ConfirmationNumber.newId();
        int distanceTraveled = 500;


        rentalService.dropOff(confirmationNumber, distanceTraveled);


        InOrder inOrder = inOrder(mockRental, mockRentalRepository, mockTruck, mockTruckRepository);
        inOrder.verify(mockRentalRepository).findOne(confirmationNumber);

        verify(mockRental, times(2)).getDistanceTraveled();

        inOrder.verify(mockRentalRepository).save(rentalCaptor.capture());
        Rental savedRental = rentalCaptor.getValue();
        assertThat(savedRental).isSameAs(mockRental);

        inOrder.verify(mockTruckRepository).findOne(any());
        inOrder.verify(mockTruckRepository).save(truckCaptor.capture());
        Truck savedTruck = truckCaptor.getValue();
        assertThat(savedTruck).isSameAs(mockTruck);

        verify(mockRental).getTruckVin();
        verify(mockTruck, times(2)).getOdometerReading();
        verify(mockTruck).getStatus();

        verify(mockTruck).setStatus(TruckStatus.RENTABLE);
        verify(mockTruck).setOdometerReading(10_500);

        verify(mockRental).setDistanceTraveled(distanceTraveled);
        verifyNoMoreInteractions(mockRental, mockTruck);
    }

    @Test
    public void findAll() {
        Rental mockRental1 = mock(Rental.class);
        Rental mockRental2 = mock(Rental.class);
        List<Rental> toBeReturned = asList(mockRental1, mockRental2);
        when(mockRentalRepository.findAll()).thenReturn(toBeReturned);


        Collection<Rental> rentals = rentalService.findAll();


        verify(mockRentalRepository).findAll();

        assertThat(rentals).hasSameElementsAs(toBeReturned);
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
        verifyZeroInteractions(mockTruckRepository);
    }
}
