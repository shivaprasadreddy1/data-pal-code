package io.pivotal.pal.wehaul.domain;

import javax.persistence.*;

@Entity
@Table
public class Truck {

    @EmbeddedId
    private Vin vin;

    @Enumerated(EnumType.STRING)
    @Column
    private TruckStatus status;

    @Column
    private Integer odometerReading;

    @Enumerated(EnumType.STRING)
    @Column
    private TruckSize truckSize;

    @Column
    private Integer truckLength;

    Truck() {
        // default constructor required by JPA
    }

    public Truck(Vin vin,
                 TruckStatus status,
                 Integer odometerReading,
                 TruckSize truckSize,
                 Integer truckLength) {
        if (odometerReading < 0) {
            throw new IllegalArgumentException("Cannot buy a truck with negative odometer reading");
        }

        this.vin = vin;
        this.status = status;
        this.odometerReading = odometerReading;
        this.truckSize = truckSize;
        this.truckLength = truckLength;
    }

    public Vin getVin() {
        return vin;
    }

    public void setVin(Vin vin) {
        this.vin = vin;
    }

    public TruckStatus getStatus() {
        return status;
    }

    public void setStatus(TruckStatus status) {
        this.status = status;
    }

    public Integer getOdometerReading() {
        return odometerReading;
    }

    public void setOdometerReading(Integer odometerReading) {
        this.odometerReading = odometerReading;
    }

    public TruckSize getTruckSize() {
        return truckSize;
    }

    public void setTruckSize(TruckSize truckSize) {
        this.truckSize = truckSize;
    }

    public Integer getTruckLength() {
        return truckLength;
    }

    public void setTruckLength(Integer truckLength) {
        this.truckLength = truckLength;
    }

    @Override
    public String toString() {
        return "Truck{" +
                "vin=" + vin +
                ", status=" + status +
                ", odometerReading=" + odometerReading +
                ", truckSize=" + truckSize +
                ", truckLength=" + truckLength +
                '}';
    }
}
