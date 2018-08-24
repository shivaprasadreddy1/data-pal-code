package io.pivotal.pal.wehaul.application;

import io.pivotal.pal.wehaul.fleet.domain.FleetService;
import io.pivotal.pal.wehaul.rental.domain.RentalTruck;
import io.pivotal.pal.wehaul.rental.domain.event.RentalTruckDroppedOff;
import io.pivotal.pal.wehaul.rental.domain.event.RentalTruckReserved;
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
public class RentalEventListenerTest {

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @MockBean
    private FleetService mockFleetService;

    @Test
    public void onRentalTruckReserved() {
        RentalTruck mockRentalTruck = mock(RentalTruck.class);
        String vin = "vin";
        when(mockRentalTruck.getVin()).thenReturn(io.pivotal.pal.wehaul.rental.domain.Vin.of(vin));


        applicationEventPublisher.publishEvent(new RentalTruckReserved(vin));


        verify(mockFleetService, timeout(100)).removeFromYard(io.pivotal.pal.wehaul.fleet.domain.Vin.of(vin));
    }

    @Test
    public void onRentalTruckDroppedOff() {
        RentalTruck mockRentalTruck = mock(RentalTruck.class);
        String vin = "vin";
        when(mockRentalTruck.getVin()).thenReturn(io.pivotal.pal.wehaul.rental.domain.Vin.of(vin));


        applicationEventPublisher.publishEvent(new RentalTruckDroppedOff(vin, 100));


        verify(mockFleetService, timeout(100)).returnToYard(io.pivotal.pal.wehaul.fleet.domain.Vin.of(vin), 100);
    }
}
