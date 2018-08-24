package io.pivotal.pal.wehaul.fleet.domain;

import java.util.UUID;

public class TruckInspection {

    private UUID id;
    private Vin truckVin;
    private Integer odometerReading;
    private String notes;

    public TruckInspection(Vin truckVin, int odometerReading, String notes) {
        this.id = UUID.randomUUID();
        this.truckVin = truckVin;
        this.odometerReading = odometerReading;
        this.notes = notes;
    }

    public UUID getId() {
        return id;
    }

    public Vin getTruckVin() {
        return truckVin;
    }

    public Integer getOdometerReading() {
        return odometerReading;
    }

    public String getNotes() {
        return notes;
    }

    @Override
    public String toString() {
        return "TruckInspection{" +
                "id=" + id +
                ", truckVin=" + truckVin +
                ", odometerReading=" + odometerReading +
                ", notes='" + notes + '\'' +
                '}';
    }
}
