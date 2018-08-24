package io.pivotal.pal.wehaul.fleet.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class FleetTruckSnapshot {

    @Id
    private String vin;

    @Column
    private String status;

    @Column
    private Integer odometerReading;

    @Column
    private Integer truckLength;

    @Column
    private Integer lastInspectionOdometerReading;

    FleetTruckSnapshot() {
        // default constructor for JPA
    }

    public FleetTruckSnapshot(String vin,
                              String status,
                              Integer odometerReading,
                              Integer truckLength,
                              Integer lastInspectionOdometerReading) {
        this.vin = vin;
        this.status = status;
        this.odometerReading = odometerReading;
        this.truckLength = truckLength;
        this.lastInspectionOdometerReading = lastInspectionOdometerReading;
    }

    public String getVin() {
        return vin;
    }

    public String getStatus() {
        return status;
    }

    public Integer getOdometerReading() {
        return odometerReading;
    }

    public Integer getTruckLength() {
        return truckLength;
    }

    public Integer getLastInspectionOdometerReading() {
        return lastInspectionOdometerReading;
    }

    @Override
    public String toString() {
        return "FleetTruckSnapshot{" +
                "vin='" + vin + '\'' +
                ", status='" + status + '\'' +
                ", odometerReading=" + odometerReading +
                ", truckLength=" + truckLength +
                ", lastInspectionOdometerReading=" + lastInspectionOdometerReading +
                '}';
    }
}
