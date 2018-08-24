package io.pivotal.pal.wehaul.rental.domain;

import javax.transaction.Transactional;
import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class RentalService {

    private final TruckAllocationService truckAllocationService;
    private final RentalRepository rentalRepository;
    private final RentalTruckRepository rentalTruckRepository;
    private final TruckSizeChart truckSizeChart;

    @Deprecated
    public RentalService(TruckAllocationService truckAllocationService,
                         RentalRepository rentalRepository,
                         RentalTruckRepository rentalTruckRepository,
                         TruckSizeChart truckSizeChart) {
        this.truckAllocationService = truckAllocationService;
        this.rentalRepository = rentalRepository;
        this.rentalTruckRepository = rentalTruckRepository;
        this.truckSizeChart = truckSizeChart;
    }

    public RentalService(TruckAllocationService truckAllocationService,
                         RentalTruckRepository rentalTruckRepository,
                         TruckSizeChart truckSizeChart) {
        this.truckAllocationService = truckAllocationService;
        this.rentalRepository = null;
        this.rentalTruckRepository = rentalTruckRepository;
        this.truckSizeChart = truckSizeChart;
    }

    public void addTruck(Vin vin, int truckLength) {
        TruckSize truckSize = truckSizeChart.getSizeByTruckLength(truckLength);
        RentalTruck rentalTruck = new RentalTruck(vin, truckSize);
        rentalTruckRepository.save(rentalTruck);
    }

    @Transactional
    public RentalTruck create(String customerName, TruckSize truckSize) {

        RentalTruck rentalTruck = truckAllocationService.allocateTruck(truckSize);

        rentalTruck.reserve(null);
        rentalTruckRepository.save(rentalTruck);

        Rental rental = new Rental(customerName, rentalTruck.getVin());
        rentalRepository.save(rental);

        return null;
    }

    @Transactional
    public void pickUp(ConfirmationNumber confirmationNumber) {
        Rental rental = rentalRepository.findOne(confirmationNumber);
        if (rental == null) {
            throw new IllegalArgumentException(String.format("No rental found for id=%s", confirmationNumber));
        }

        rental.pickUp();
        rentalRepository.save(rental);

        RentalTruck rentalTruck = rentalTruckRepository.findOne(rental.getTruckVin());
        rentalTruck.pickUp();
        rentalTruckRepository.save(rentalTruck);
    }

    @Transactional
    public RentalTruck dropOff(ConfirmationNumber confirmationNumber, int distanceTraveled) {
        Rental rental = rentalRepository.findOne(confirmationNumber);
        if (rental == null) {
            throw new IllegalArgumentException(String.format("No rental found for id=%s", confirmationNumber));
        }

        rental.dropOff(distanceTraveled);
        rentalRepository.save(rental);

        Vin vin = rental.getTruckVin();
        RentalTruck rentalTruck = rentalTruckRepository.findOne(vin);
        rentalTruck.dropOff(-1);
        rentalTruckRepository.save(rentalTruck);

        return null;
    }

    public void preventRenting(Vin vin) {
        RentalTruck rentalTruck = rentalTruckRepository.findOne(vin);
        if (rentalTruck == null) {
            throw new IllegalArgumentException(String.format("No truck found with vin=%s", vin));
        }

        rentalTruck.preventRenting();

        rentalTruckRepository.save(rentalTruck);
    }

    public void allowRenting(Vin vin) {
        RentalTruck rentalTruck = rentalTruckRepository.findOne(vin);
        if (rentalTruck == null) {
            throw new IllegalArgumentException(String.format("No truck found with vin=%s", vin));
        }

        rentalTruck.allowRenting();

        rentalTruckRepository.save(rentalTruck);
    }

    public Collection<RentalTruck> findAll() {
        return StreamSupport
                .stream(rentalTruckRepository.findAll().spliterator(), false)
                .collect(Collectors.toList());
    }
}
