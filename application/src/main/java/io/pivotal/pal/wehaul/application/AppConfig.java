package io.pivotal.pal.wehaul.application;

import io.pivotal.pal.wehaul.application.eventstore.EventPublishingFleetTruckRepository;
import io.pivotal.pal.wehaul.application.eventstore.FleetTruckEventSourcedRepository;
import io.pivotal.pal.wehaul.application.eventstore.FleetTruckEventStoreRepository;
import io.pivotal.pal.wehaul.fleet.domain.FleetCommandService;
import io.pivotal.pal.wehaul.fleet.domain.FleetQueryService;
import io.pivotal.pal.wehaul.fleet.domain.FleetTruckRepository;
import io.pivotal.pal.wehaul.fleet.domain.FleetTruckSnapshotRepository;
import io.pivotal.pal.wehaul.rental.domain.RentalService;
import io.pivotal.pal.wehaul.rental.domain.RentalTruckRepository;
import io.pivotal.pal.wehaul.rental.domain.TruckAllocationService;
import io.pivotal.pal.wehaul.rental.domain.TruckSizeChart;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    public TruckSizeChart truckSizeChart() {
        return new TruckSizeChart();
    }

    @Bean
    public TruckAllocationService truckAllocationService(RentalTruckRepository rentalTruckRepository) {
        return new TruckAllocationService(rentalTruckRepository);
    }

    @Bean
    public FleetCommandService fleetCommandService(FleetTruckRepository fleetTruckRepository) {
        return new FleetCommandService(fleetTruckRepository);
    }

    @Bean
    public FleetQueryService fleetQueryService(FleetTruckSnapshotRepository fleetTruckSnapshotRepository) {
        return new FleetQueryService(fleetTruckSnapshotRepository);
    }

    @Bean
    public RentalService rentalService(TruckAllocationService truckAllocationService,
                                       RentalTruckRepository rentalTruckRepository,
                                       TruckSizeChart truckSizeChart) {
        return new RentalService(truckAllocationService, rentalTruckRepository, truckSizeChart);
    }

    @Bean
    public FleetTruckRepository eventPublishingFleetTruckRepository(FleetTruckEventStoreRepository eventStoreRepository, ApplicationEventPublisher applicationEventPublisher) {

        FleetTruckEventSourcedRepository eventSourcedRepository =
                new FleetTruckEventSourcedRepository(eventStoreRepository);

        return new EventPublishingFleetTruckRepository(eventSourcedRepository, applicationEventPublisher);
    }
}
