package io.pivotal.pal.wehaul.rental.domain;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class TruckAllocationServiceTest {

    @Mock
    private RentalTruckRepository mockRentalTruckRepository;

    private TruckAllocationService service;

    @Before
    public void setUp() {
        service = new TruckAllocationService(mockRentalTruckRepository);
    }

    @Test
    public void allocateTruck() {
        RentalTruck expected = mock(RentalTruck.class);
        when(mockRentalTruckRepository.findAllByTruckSizeAndStatus(any(), any()))
                .thenReturn(Arrays.asList(expected, mock(RentalTruck.class), mock(RentalTruck.class)));


        RentalTruck rentalTruck = service.allocateTruck(TruckSize.LARGE);


        verify(mockRentalTruckRepository).findAllByTruckSizeAndStatus(TruckSize.LARGE, RentalTruckStatus.RENTABLE);

        assertThat(rentalTruck).isEqualTo(expected);
    }

    @Test
    public void allocateTruck_noAvailableTrucks() {
        when(mockRentalTruckRepository.findAllByTruckSizeAndStatus(any(), any()))
                .thenReturn(emptyList());


        assertThatExceptionOfType(IllegalStateException.class)
                .isThrownBy(() -> service.allocateTruck(TruckSize.LARGE))
                .withMessage("No trucks available to rent");
    }
}
