package io.pivotal.pal.wehaul.fleet.domain;

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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class FleetServiceTest {

    @Mock
    private FleetTruckRepository mockFleetTruckRepository;
    @Captor
    private ArgumentCaptor<FleetTruck> truckCaptor;

    private FleetService fleetService;

    @Before
    public void setUp() {
        fleetService = new FleetService(
                mockFleetTruckRepository
        );
    }

    @Test
    public void buyTruck() {
        fleetService.buyTruck(Vin.of("some-vin"), 1000, 25);


        verify(mockFleetTruckRepository).save(truckCaptor.capture());

        FleetTruck savedFleetTruck = truckCaptor.getValue();
        assertThat(savedFleetTruck.getVin()).isEqualTo(Vin.of("some-vin"));
        assertThat(savedFleetTruck.getOdometerReading()).isEqualTo(1000);
        assertThat(savedFleetTruck.getTruckLength()).isEqualTo(25);

        assertThat(truckCaptor.getValue().getVin()).isEqualTo(Vin.of("some-vin"));
        assertThat(truckCaptor.getValue().getOdometerReading()).isEqualTo(1000);
        assertThat(truckCaptor.getValue().getTruckLength()).isEqualTo(25);
    }

    @Test
    public void sendForInspection() {
        FleetTruck mockFleetTruck = mock(FleetTruck.class);
        when(mockFleetTruck.getStatus()).thenReturn(FleetTruckStatus.INSPECTABLE);
        when(mockFleetTruckRepository.findOne(any())).thenReturn(mockFleetTruck);
        Vin vin = Vin.of("some-vin");


        fleetService.sendForInspection(vin);


        InOrder inOrder = inOrder(mockFleetTruck, mockFleetTruckRepository);
        inOrder.verify(mockFleetTruckRepository).findOne(vin);
        inOrder.verify(mockFleetTruck).sendForInspection();
        inOrder.verify(mockFleetTruckRepository).save(mockFleetTruck);

        verifyNoMoreInteractions(mockFleetTruck);
    }

    @Test
    public void returnFromInspection() {
        FleetTruck mockFleetTruck = mock(FleetTruck.class);
        when(mockFleetTruckRepository.findOne(any())).thenReturn(mockFleetTruck);


        fleetService.returnFromInspection(Vin.of("some-vin"), "some-notes", 2);


        InOrder inOrder = inOrder(mockFleetTruck, mockFleetTruckRepository);
        inOrder.verify(mockFleetTruckRepository).findOne(Vin.of("some-vin"));
        inOrder.verify(mockFleetTruck).returnFromInspection("some-notes", 2);
        inOrder.verify(mockFleetTruckRepository).save(mockFleetTruck);
    }

    @Test
    public void removeFromYard() {
        FleetTruck mockTruck = mock(FleetTruck.class);
        when(mockFleetTruckRepository.findOne(any())).thenReturn(mockTruck);


        fleetService.removeFromYard(Vin.of("some-vin"));


        InOrder inOrder = inOrder(mockTruck, mockFleetTruckRepository);
        inOrder.verify(mockFleetTruckRepository).findOne(Vin.of("some-vin"));
        inOrder.verify(mockTruck).removeFromYard();
        inOrder.verify(mockFleetTruckRepository).save(mockTruck);
    }

    @Test
    public void returnToYard() {
        FleetTruck mockTruck = mock(FleetTruck.class);
        when(mockTruck.getVin()).thenReturn(Vin.of("some-vin"));
        when(mockFleetTruckRepository.findOne(any())).thenReturn(mockTruck);


        fleetService.returnToYard(mockTruck.getVin(), 200);


        InOrder inOrder = inOrder(mockTruck, mockFleetTruckRepository);
        inOrder.verify(mockFleetTruckRepository).findOne(Vin.of("some-vin"));
        inOrder.verify(mockTruck).returnToYard(200);
        inOrder.verify(mockFleetTruckRepository).save(mockTruck);
    }

    @Test
    public void findAll() {
        FleetTruck mockFleetTruck1 = mock(FleetTruck.class);
        FleetTruck mockFleetTruck2 = mock(FleetTruck.class);
        List<FleetTruck> toBeReturned = Arrays.asList(mockFleetTruck1, mockFleetTruck2);
        when(mockFleetTruckRepository.findAll()).thenReturn(toBeReturned);


        Collection<FleetTruck> fleetTrucks = fleetService.findAll();


        verify(mockFleetTruckRepository).findAll();

        Assertions.assertThat(fleetTrucks).hasSameElementsAs(toBeReturned);
    }

    @Test
    public void sendForInspection_whenNoTruckFound() {
        Vin vin = Vin.of("cant-find-me");


        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> fleetService.sendForInspection(vin))
                .withMessage(String.format("No truck found with VIN=%s", vin));


        verify(mockFleetTruckRepository).findOne(vin);
        verifyNoMoreInteractions(mockFleetTruckRepository);
    }

    @Test
    public void returnFromInspection_whenNoTruckFound() {
        Vin vin = Vin.of("cant-find-me");


        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> fleetService.returnFromInspection(vin, "some-notes", 5000))
                .withMessage(String.format("No truck found with VIN=%s", vin));


        verify(mockFleetTruckRepository).findOne(vin);
        verifyNoMoreInteractions(mockFleetTruckRepository);
    }

    @Test
    public void returnToYard_whenNoTruckFound() {
        Vin vin = Vin.of("cant-find-me");
        when(mockFleetTruckRepository.findOne(any())).thenReturn(null);


        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> fleetService.returnToYard(vin, 404))
                .withMessage(String.format("No truck found with VIN=%s", vin));


        verify(mockFleetTruckRepository, times(0)).save(any(FleetTruck.class));
    }
}
