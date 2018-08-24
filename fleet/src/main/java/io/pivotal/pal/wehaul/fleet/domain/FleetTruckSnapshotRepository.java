package io.pivotal.pal.wehaul.fleet.domain;

import org.springframework.data.repository.Repository;

import java.util.List;

public interface FleetTruckSnapshotRepository extends Repository<FleetTruckSnapshot, String> {
    List<FleetTruckSnapshot> findAll();

    FleetTruckSnapshot findOne(String vin);

    void save(FleetTruckSnapshot fleetTruckSnapshot);
}
