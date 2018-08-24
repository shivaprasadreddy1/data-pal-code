package io.pivotal.pal.wehaul.application;

import io.pivotal.pal.wehaul.fleet.domain.FleetService;
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
    public FleetService fleetService(FleetTruckRepository fleetTruckRepository) {
        return new FleetService(
                fleetTruckRepository
        );
    }

    @Bean
    public RentalService rentalService(TruckAllocationService truckAllocationService,
                                       RentalTruckRepository rentalTruckRepository,
                                       TruckSizeChart truckSizeChart) {
        return new RentalService(truckAllocationService, rentalTruckRepository, truckSizeChart);
    }
}
