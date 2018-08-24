package io.pivotal.pal.wehaul.fleet.domain;

import org.springframework.data.repository.CrudRepository;

public interface FleetTruckRepository extends CrudRepository<FleetTruck, Vin> {
}
