package io.pivotal.pal.wehaul.application.eventstore;

import io.pivotal.pal.wehaul.fleet.domain.FleetTruck;
import io.pivotal.pal.wehaul.fleet.domain.FleetTruckRepository;
import io.pivotal.pal.wehaul.fleet.domain.Vin;
import io.pivotal.pal.wehaul.fleet.domain.event.FleetTruckEvent;
import io.pivotal.pal.wehaul.fleet.domain.FleetTruckUpdated;
import org.springframework.context.ApplicationEventPublisher;

import java.util.List;

public class EventPublishingFleetTruckRepository implements FleetTruckRepository {

    private final FleetTruckRepository fleetTruckRepository;
    private final ApplicationEventPublisher applicationEventPublisher;

    public EventPublishingFleetTruckRepository(FleetTruckRepository fleetTruckRepository,
                                               ApplicationEventPublisher applicationEventPublisher) {
        this.fleetTruckRepository = fleetTruckRepository;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Override
    public FleetTruck save(FleetTruck fleetTruck) {

        List<FleetTruckEvent> dirtyEvents = fleetTruck.getDirtyEvents();

        FleetTruck savedFleetTruck = fleetTruckRepository.save(fleetTruck);

        dirtyEvents.stream()
                .forEach(event -> applicationEventPublisher.publishEvent(event));

        FleetTruckUpdated fleetTruckUpdated = new FleetTruckUpdated(fleetTruck);
        this.applicationEventPublisher.publishEvent(fleetTruckUpdated);

        return savedFleetTruck;
    }

    @Override
    public FleetTruck findOne(Vin vin) {
        return fleetTruckRepository.findOne(vin);
    }

}
