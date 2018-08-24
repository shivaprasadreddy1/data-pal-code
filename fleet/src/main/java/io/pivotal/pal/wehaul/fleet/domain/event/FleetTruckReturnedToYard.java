package io.pivotal.pal.wehaul.fleet.domain.event;

import java.util.Objects;

public class FleetTruckReturnedToYard extends FleetTruckEvent {

    protected final Integer distanceTraveled;

    public FleetTruckReturnedToYard(String vin, String status, Integer distanceTraveled) {
        super(vin, status);
        this.distanceTraveled = distanceTraveled;
    }

    private FleetTruckReturnedToYard() {
        super();
        this.distanceTraveled = null;
    }

    public Integer getDistanceTraveled() {
        return distanceTraveled;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FleetTruckReturnedToYard)) return false;
        if (!super.equals(o)) return false;
        FleetTruckReturnedToYard that = (FleetTruckReturnedToYard) o;
        return Objects.equals(distanceTraveled, that.distanceTraveled);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), distanceTraveled);
    }

    @Override
    public String toString() {
        return "FleetTruckReturnedToYard{" +
                "distanceTraveled=" + distanceTraveled +
                ", vin='" + vin + '\'' +
                ", status='" + status + '\'' +
                ", createdDate=" + createdDate +
                '}';
    }
}
