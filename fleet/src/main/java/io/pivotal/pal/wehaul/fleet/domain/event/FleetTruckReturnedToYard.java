package io.pivotal.pal.wehaul.fleet.domain.event;

import java.util.Objects;

public class FleetTruckReturnedToYard extends FleetTruckEvent {

    protected final Integer odometerReading;

    public FleetTruckReturnedToYard(String vin, String status, Integer odometerReading) {
        super(vin, status);
        this.odometerReading = odometerReading;
    }

    public Integer getOdometerReading() {
        return odometerReading;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FleetTruckReturnedToYard)) return false;
        if (!super.equals(o)) return false;
        FleetTruckReturnedToYard that = (FleetTruckReturnedToYard) o;
        return Objects.equals(odometerReading, that.odometerReading);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), odometerReading);
    }

    @Override
    public String toString() {
        return "FleetTruckReturnedToYard{" +
                "odometerReading=" + odometerReading +
                ", vin='" + vin + '\'' +
                ", status='" + status + '\'' +
                ", createdDate=" + createdDate +
                '}';
    }
}
