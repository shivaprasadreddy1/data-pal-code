package io.pivotal.pal.wehaul.application;

import io.pivotal.pal.wehaul.fleet.domain.FleetTruck;
import io.pivotal.pal.wehaul.fleet.domain.FleetTruckStatus;
import io.pivotal.pal.wehaul.fleet.domain.event.FleetTruckPurchased;
import io.pivotal.pal.wehaul.fleet.domain.event.FleetTruckReturnedFromInspection;
import io.pivotal.pal.wehaul.fleet.domain.event.FleetTruckSentForInspection;
import io.pivotal.pal.wehaul.rental.domain.RentalService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.context.junit4.SpringRunner;

import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class FleetEventListenerTest {

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @MockBean
    private RentalService mockRentalService;

    @Test
    public void onFleetTruckPurchased() {
        FleetTruck mockFleetTruck = mock(FleetTruck.class);
        String vin = "vin";
        when(mockFleetTruck.getVin()).thenReturn(io.pivotal.pal.wehaul.fleet.domain.Vin.of(vin));
        when(mockFleetTruck.getTruckLength()).thenReturn(10);


        applicationEventPublisher.publishEvent(new FleetTruckPurchased(vin, FleetTruckStatus.INSPECTABLE.toString(), 10, 200));


        verify(mockRentalService, timeout(100)).addTruck(io.pivotal.pal.wehaul.rental.domain.Vin.of(vin), 10);
    }

    @Test
    public void onFleetTruckSentForInspection() {
        FleetTruck mockFleetTruck = mock(FleetTruck.class);
        String vin = "vin";
        when(mockFleetTruck.getVin()).thenReturn(io.pivotal.pal.wehaul.fleet.domain.Vin.of(vin));


        applicationEventPublisher.publishEvent(new FleetTruckSentForInspection(vin, FleetTruckStatus.IN_INSPECTION.toString()));


        verify(mockRentalService, timeout(100)).preventRenting(io.pivotal.pal.wehaul.rental.domain.Vin.of(vin));
    }

    @Test
    public void onFleetTruckReturnedFromInspection() {
        FleetTruck mockFleetTruck = mock(FleetTruck.class);
        String vin = "vin";
        when(mockFleetTruck.getVin()).thenReturn(io.pivotal.pal.wehaul.fleet.domain.Vin.of(vin));


        applicationEventPublisher.publishEvent(new FleetTruckReturnedFromInspection(vin, FleetTruckStatus.INSPECTABLE.toString(), 200, "some-notes"));


        verify(mockRentalService, timeout(100)).allowRenting(io.pivotal.pal.wehaul.rental.domain.Vin.of(vin));
    }
}
