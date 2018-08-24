package io.pivotal.pal.wehaul.fleet.domain.event;

public class FleetTruckSentForInspection extends FleetTruckEvent {

    public FleetTruckSentForInspection(String vin, String status) {
        super(vin, status);
    }

    @Override
    public String toString() {
        return "FleetTruckSentForInspection{" +
                "vin='" + vin + '\'' +
                ", status='" + status + '\'' +
                ", createdDate=" + createdDate +
                '}';
    }
}
