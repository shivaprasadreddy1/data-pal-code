package io.pivotal.pal.wehaul.application;

import io.pivotal.pal.wehaul.fleet.domain.FleetService;
import io.pivotal.pal.wehaul.fleet.domain.TruckInspectionRepository;
import io.pivotal.pal.wehaul.fleet.domain.FleetTruckRepository;
import io.pivotal.pal.wehaul.rental.domain.*;
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
    public FleetService fleetService(FleetTruckRepository fleetTruckRepository,
                                     TruckInspectionRepository truckInspectionRepository) {
        return new FleetService(
                fleetTruckRepository,
                truckInspectionRepository
        );
    }

    @Bean
    public RentalService rentalService(TruckAllocationService truckAllocationService,
                                       RentalRepository rentalRepository,
                                       RentalTruckRepository rentalTruckRepository,
                                       TruckSizeChart truckSizeChart) {
        return new RentalService(truckAllocationService, rentalRepository, rentalTruckRepository, truckSizeChart);
    }
}
