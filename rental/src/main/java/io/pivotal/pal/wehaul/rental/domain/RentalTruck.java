package io.pivotal.pal.wehaul.rental.domain;

import javax.persistence.*;

@Entity(name = "rentalTruck")
@Table(name = "rental_truck")
public class RentalTruck {

    @EmbeddedId
    private Vin vin;

    @Enumerated(EnumType.STRING)
    @Column
    private RentalTruckStatus status;

    @Enumerated(EnumType.STRING)
    @Column
    private TruckSize truckSize;

    RentalTruck() {
        // default constructor required by JPA
    }

    public RentalTruck(Vin vin, TruckSize truckSize) {
        // TODO implement me
    }

    public void reserve() {
        // TODO implement me
    }

    public void pickUp() {
        // TODO implement me
    }

    public void dropOff() {
        // TODO implement me
    }

    public void preventRenting() {
        if (status != RentalTruckStatus.RENTABLE) {
            throw new IllegalStateException("Truck cannot be prevented from renting");
        }
        status = RentalTruckStatus.NOT_RENTABLE;
    }

    public void allowRenting() {
        if (status != RentalTruckStatus.NOT_RENTABLE) {
            throw new IllegalStateException("Truck is not rentable");
        }

        status = RentalTruckStatus.RENTABLE;
    }

    public Vin getVin() {
        return vin;
    }

    public RentalTruckStatus getStatus() {
        return status;
    }

    public TruckSize getTruckSize() {
        return truckSize;
    }

    @Override
    public String toString() {
        return "Truck{" +
                "vin=" + vin +
                ", status=" + status +
                ", truckSize=" + truckSize +
                '}';
    }
}
