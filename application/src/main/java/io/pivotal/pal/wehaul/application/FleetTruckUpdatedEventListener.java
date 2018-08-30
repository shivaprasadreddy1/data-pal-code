package io.pivotal.pal.wehaul.application;

import io.pivotal.pal.wehaul.fleet.domain.FleetTruckSnapshot;
import io.pivotal.pal.wehaul.fleet.domain.FleetTruckSnapshotRepository;
import io.pivotal.pal.wehaul.fleet.domain.FleetTruckUpdated;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@Async
public class FleetTruckUpdatedEventListener {

    private final FleetTruckSnapshotRepository fleetTruckSnapshotRepository;

    public FleetTruckUpdatedEventListener(FleetTruckSnapshotRepository fleetTruckSnapshotRepository) {
        this.fleetTruckSnapshotRepository = fleetTruckSnapshotRepository;
    }

    @EventListener
    public void onFleetTruckUpdated(FleetTruckUpdated event) {
        FleetTruckSnapshot fleetTruckSnapshot = new FleetTruckSnapshot(event.getVin(), event.getStatus(), event.getOdometerReading(), event.getTruckLength(), event.getLastInspectionOdometerReading());
        fleetTruckSnapshotRepository.save(fleetTruckSnapshot);
    }
}
