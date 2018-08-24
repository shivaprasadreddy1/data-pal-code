package io.pivotal.pal.wehaul.fleet.domain.event;

import java.time.LocalDateTime;
import java.util.Objects;

public abstract class FleetTruckEvent {

    protected final String vin;
    protected final String status;
    protected final LocalDateTime createdDate;


    protected FleetTruckEvent(String vin, String status) {
        this.vin = vin;
        this.status = status;
        this.createdDate = LocalDateTime.now();
    }

    public String getVin() {
        return vin;
    }

    public String getStatus() {
        return status;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FleetTruckEvent)) return false;
        FleetTruckEvent that = (FleetTruckEvent) o;
        return Objects.equals(vin, that.vin) &&
                Objects.equals(status, that.status) &&
                Objects.equals(createdDate, that.createdDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(vin, status, createdDate);
    }

    @Override
    public String toString() {
        return "FleetTruckEvent{" +
                "vin='" + vin + '\'' +
                ", status='" + status + '\'' +
                ", createdDate=" + createdDate +
                '}';
    }
}
