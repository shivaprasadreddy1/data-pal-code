package io.pivotal.pal.wehaul.fleet.domain;

import javax.persistence.*;

@Entity(name = "fleetTruck")
@Table(name = "fleet_truck")
public class FleetTruck {

    @EmbeddedId
    private Vin vin;

    @Enumerated(EnumType.STRING)
    @Column
    private FleetTruckStatus status;

    @Column
    private Integer odometerReading;

    @Column
    private Integer truckLength;

    FleetTruck() {
        // default constructor required by JPA
    }

    public FleetTruck(Vin vin, Integer odometerReading, Integer truckLength) {
        // TODO implement me
    }

    public void returnFromInspection(int odometerReading) {
        // TODO implement me
    }

    public void sendForInspection() {
        // TODO implement me
    }

    public void removeFromYard() {
        // TODO implement me
    }

    public void returnToYard(int distanceTraveled) {
        if (status != FleetTruckStatus.NOT_INSPECTABLE) {
            throw new IllegalStateException("Truck is not currently out of yard");
        }
        if (distanceTraveled < 0) {
            throw new IllegalArgumentException("Distance traveled cannot be negative");
        }

        status = FleetTruckStatus.INSPECTABLE;
        this.odometerReading = distanceTraveled;
    }

    public Vin getVin() {
        return vin;
    }

    public FleetTruckStatus getStatus() {
        return status;
    }

    public Integer getOdometerReading() {
        return odometerReading;
    }

    public Integer getTruckLength() {
        return truckLength;
    }

    @Override
    public String toString() {
        return "Truck{" +
                "vin=" + vin +
                ", status=" + status +
                ", odometerReading=" + odometerReading +
                ", truckLength=" + truckLength +
                '}';
    }
}
