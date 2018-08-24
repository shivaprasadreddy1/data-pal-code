package io.pivotal.pal.wehaul.application;

import io.pivotal.pal.wehaul.domain.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    public FleetService fleetService(TruckRepository truckRepository,
                                     TruckInspectionRepository truckInspectionRepository) {
        return new FleetService(
                truckRepository,
                truckInspectionRepository
        );
    }

    @Bean
    public RentalService rentalService(RentalRepository rentalRepository,
                                       TruckRepository truckRepository) {
        return new RentalService(rentalRepository, truckRepository);
    }
}
