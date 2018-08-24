package io.pivotal.pal.wehaul.fleet.domain.event;

public class FleetTruckRemovedFromYard extends FleetTruckEvent {

    public FleetTruckRemovedFromYard(String vin, String status) {
        super(vin, status);
    }

    private FleetTruckRemovedFromYard() {
        super();
    }

    @Override
    public String toString() {
        return "FleetTruckRemovedFromYard{" +
                "vin='" + vin + '\'' +
                ", status='" + status + '\'' +
                ", createdDate=" + createdDate +
                '}';
    }
}
