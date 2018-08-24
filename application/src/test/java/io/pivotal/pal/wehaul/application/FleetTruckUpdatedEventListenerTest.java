package io.pivotal.pal.wehaul.application;

import io.pivotal.pal.wehaul.fleet.domain.*;
import io.pivotal.pal.wehaul.fleet.domain.FleetTruckUpdated;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class FleetTruckUpdatedEventListenerTest {

    @MockBean
    private FleetTruckSnapshotRepository fleetTruckSnapshotRepository;

    @Captor
    private ArgumentCaptor<FleetTruckSnapshot> fleetTruckSnapshotCaptor;

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @Before
    public void setUp() {
        reset(fleetTruckSnapshotRepository);
    }

    @Test
    public void onFleetTruckUpdated() {
        FleetTruck fleetTruck = mock(FleetTruck.class);
        when(fleetTruck.getVin()).thenReturn(Vin.of("vin"));
        when(fleetTruck.getStatus()).thenReturn(FleetTruckStatus.INSPECTABLE);
        when(fleetTruck.getOdometerReading()).thenReturn(1000);
        when(fleetTruck.getTruckLength()).thenReturn(20);
        TruckInspection truckInspection = new TruckInspection(Vin.of("vin"), 500, "some-notes");
        when(fleetTruck.getInspections()).thenReturn(Collections.singletonList(truckInspection));


        FleetTruckUpdated event = new FleetTruckUpdated(fleetTruck);
        applicationEventPublisher.publishEvent(event);


        FleetTruckSnapshot expectedSnapshot = new FleetTruckSnapshot(
                "vin",
                "INSPECTABLE",
                1000,
                20,
                500
        );
        verify(fleetTruckSnapshotRepository, timeout(100)).save(fleetTruckSnapshotCaptor.capture());

        assertThat(fleetTruckSnapshotCaptor.getValue()).isEqualToComparingFieldByField(expectedSnapshot);
    }
}
