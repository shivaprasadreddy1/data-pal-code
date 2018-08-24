package io.pivotal.pal.wehaul.fleet.domain;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

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

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "truckVin", referencedColumnName = "vin")
    private List<TruckInspection> inspections = new ArrayList<>();

    FleetTruck() {
        // default constructor required by JPA
    }

    public FleetTruck(Vin vin, Integer odometerReading, Integer truckLength) {
        if (odometerReading < 0) {
            throw new IllegalArgumentException("Cannot buy a truck with negative odometer reading");
        }

        this.vin = vin;
        this.status = FleetTruckStatus.INSPECTABLE;
        this.odometerReading = odometerReading;
        this.truckLength = truckLength;
    }

    public void returnFromInspection(String notes, int odometerReading) {
        if (status != FleetTruckStatus.IN_INSPECTION) {
            throw new IllegalStateException("Truck is not currently in inspection");
        }
        if (this.odometerReading > odometerReading) {
            throw new IllegalArgumentException("Odometer reading cannot be less than previous reading");
        }

        this.status = FleetTruckStatus.INSPECTABLE;
        this.odometerReading = odometerReading;

        TruckInspection truckInspection = new TruckInspection(this.vin, odometerReading, notes);
        this.inspections.add(truckInspection);
    }

    public void sendForInspection() {
        if (status != FleetTruckStatus.INSPECTABLE) {
            throw new IllegalStateException("Truck cannot be inspected");
        }

        this.status = FleetTruckStatus.IN_INSPECTION;
    }

    public void removeFromYard() {
        if (status != FleetTruckStatus.INSPECTABLE) {
            throw new IllegalStateException("Cannot remove truck, currently in inspection");
        }

        status = FleetTruckStatus.NOT_INSPECTABLE;
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

    public List<TruckInspection> getInspections() {
        return inspections;
    }

    @Override
    public String toString() {
        return "FleetTruck{" +
                "vin=" + vin +
                ", status=" + status +
                ", odometerReading=" + odometerReading +
                ", truckLength=" + truckLength +
                ", inspections=" + inspections +
                '}';
    }
}
