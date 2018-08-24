package io.pivotal.pal.wehaul.fleet.domain.event;

import java.util.Objects;

public class FleetTruckPurchased extends FleetTruckEvent {

    protected final Integer odometerReading;
    protected final Integer truckLength;

    public FleetTruckPurchased(String vin, String status, Integer truckLength, Integer odometerReading) {
        super(vin, status);
        this.odometerReading = odometerReading;
        this.truckLength = truckLength;
    }

    private FleetTruckPurchased() {
        super();
        this.odometerReading = null;
        this.truckLength = null;
    }

    public Integer getOdometerReading() {
        return odometerReading;
    }

    public Integer getTruckLength() {
        return truckLength;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FleetTruckPurchased)) return false;
        if (!super.equals(o)) return false;
        FleetTruckPurchased that = (FleetTruckPurchased) o;
        return Objects.equals(odometerReading, that.odometerReading) &&
                Objects.equals(truckLength, that.truckLength);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), odometerReading, truckLength);
    }

    @Override
    public String toString() {
        return "FleetTruckPurchased{" +
                "odometerReading=" + odometerReading +
                ", truckLength=" + truckLength +
                ", vin='" + vin + '\'' +
                ", status='" + status + '\'' +
                ", createdDate=" + createdDate +
                '}';
    }
}
