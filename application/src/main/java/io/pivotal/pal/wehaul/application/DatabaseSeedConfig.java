package io.pivotal.pal.wehaul.application;

import io.pivotal.pal.wehaul.domain.*;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class DatabaseSeedConfig {

    private final TruckRepository truckRepository;

    public DatabaseSeedConfig(TruckRepository truckRepository) {
        this.truckRepository = truckRepository;
    }

    @PostConstruct
    public void populateDatabase() {
        Truck truck = new Truck(
                Vin.of("test-0001"),
                TruckStatus.RENTABLE,
                0,
                TruckSize.LARGE,
                25
        );

        if (truck.getStatus() != TruckStatus.RENTABLE) {
            throw new IllegalStateException("Truck cannot be inspected");
        }

        truck.setStatus(TruckStatus.IN_INSPECTION);

        truckRepository.save(truck);

        Truck truck2 = new Truck(
                Vin.of("test-0002"),
                TruckStatus.RENTABLE,
                0,
                TruckSize.SMALL,
                15
        );
        truckRepository.save(truck2);
    }
}
