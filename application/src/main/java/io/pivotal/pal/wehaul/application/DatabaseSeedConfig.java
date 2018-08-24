package io.pivotal.pal.wehaul.application;

import io.pivotal.pal.wehaul.fleet.domain.FleetTruck;
import io.pivotal.pal.wehaul.fleet.domain.FleetTruckRepository;
import io.pivotal.pal.wehaul.fleet.domain.Vin;
import io.pivotal.pal.wehaul.rental.domain.RentalTruck;
import io.pivotal.pal.wehaul.rental.domain.RentalTruckRepository;
import io.pivotal.pal.wehaul.rental.domain.TruckSize;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class DatabaseSeedConfig {

    private final FleetTruckRepository fleetTruckRepository;
    private final RentalTruckRepository rentalTruckRepository;

    public DatabaseSeedConfig(
            FleetTruckRepository fleetTruckRepository,
            RentalTruckRepository rentalTruckRepository) {
        this.fleetTruckRepository = fleetTruckRepository;
        this.rentalTruckRepository = rentalTruckRepository;
    }

    @PostConstruct
    public void populateDatabase() {
        // Create one Truck in both Fleet + Rental perspectives that is unrentable/in inspection
        FleetTruck inInspectionFleetTruck = new FleetTruck(
                Vin.of("test-0001"),
                0,
                25
        );
        inInspectionFleetTruck.sendForInspection();
        fleetTruckRepository.save(inInspectionFleetTruck);

        RentalTruck unrentableRentalTruck = new RentalTruck(
                io.pivotal.pal.wehaul.rental.domain.Vin.of("test-0001"),
                TruckSize.LARGE
        );
        unrentableRentalTruck.preventRenting();
        rentalTruckRepository.save(unrentableRentalTruck);


        // Create another Truck in both Fleet + Rental perspectives that is rentable/not in inspection
        FleetTruck inspectableFleetTruck = new FleetTruck(
                Vin.of("test-0002"),
                0,
                15
        );
        fleetTruckRepository.save(inspectableFleetTruck);

        RentalTruck rentableRentalTruck = new RentalTruck(
                io.pivotal.pal.wehaul.rental.domain.Vin.of("test-0002"),
                TruckSize.SMALL
        );
        rentalTruckRepository.save(rentableRentalTruck);

    }
}
