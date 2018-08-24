package io.pivotal.pal.wehaul.rental.domain;

import java.util.List;

public class TruckAllocationService {

    private final RentalTruckRepository rentalTruckRepository;

    public TruckAllocationService(RentalTruckRepository rentalTruckRepository) {
        this.rentalTruckRepository = rentalTruckRepository;
    }

    public RentalTruck allocateTruck(TruckSize truckSize) {

        List<RentalTruck> availableRentalTrucks =
                rentalTruckRepository.findAllByTruckSizeAndStatus(truckSize, RentalTruckStatus.RENTABLE);

        if (availableRentalTrucks.size() < 1) {
            throw new IllegalStateException("No trucks available to rent");
        }

        return availableRentalTrucks.get(0);

    }
}
