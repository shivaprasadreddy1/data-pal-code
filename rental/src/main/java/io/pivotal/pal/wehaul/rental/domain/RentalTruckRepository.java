package io.pivotal.pal.wehaul.rental.domain;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface RentalTruckRepository extends CrudRepository<RentalTruck, Vin> {

    List<RentalTruck> findAllByTruckSizeAndStatus(TruckSize truckSize, RentalTruckStatus truckStatus);
}
