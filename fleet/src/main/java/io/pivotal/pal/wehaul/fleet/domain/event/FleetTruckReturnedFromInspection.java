package io.pivotal.pal.wehaul.fleet.domain.event;

import java.util.Objects;

public class FleetTruckReturnedFromInspection extends FleetTruckEvent {

    protected final Integer odometerReading;
    protected final String notes;

    public FleetTruckReturnedFromInspection(String vin, String status, Integer odometerReading, String notes) {
        super(vin, status);
        this.odometerReading = odometerReading;
        this.notes = notes;
    }

    public Integer getOdometerReading() {
        return odometerReading;
    }

    public String getNotes() {
        return notes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FleetTruckReturnedFromInspection)) return false;
        if (!super.equals(o)) return false;
        FleetTruckReturnedFromInspection that = (FleetTruckReturnedFromInspection) o;
        return Objects.equals(odometerReading, that.odometerReading) &&
                Objects.equals(notes, that.notes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), odometerReading, notes);
    }

    @Override
    public String toString() {
        return "FleetTruckReturnedFromInspection{" +
                "odometerReading=" + odometerReading +
                ", notes='" + notes + '\'' +
                ", vin='" + vin + '\'' +
                ", status='" + status + '\'' +
                ", createdDate=" + createdDate +
                '}';
    }
}
