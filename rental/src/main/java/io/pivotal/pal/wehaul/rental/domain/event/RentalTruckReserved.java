package io.pivotal.pal.wehaul.rental.domain.event;

import java.time.LocalDateTime;
import java.util.Objects;

public class RentalTruckReserved {

    private final String vin;
    private final LocalDateTime createdDate;


    public RentalTruckReserved(String vin) {
        this.vin = vin;
        this.createdDate = LocalDateTime.now();
    }

    public String getVin() {
        return vin;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RentalTruckReserved)) return false;
        RentalTruckReserved that = (RentalTruckReserved) o;
        return Objects.equals(vin, that.vin) &&
                Objects.equals(createdDate, that.createdDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(vin, createdDate);
    }

    @Override
    public String toString() {
        return "RentalTruckReserved{" +
                "vin='" + vin + '\'' +
                ", createdDate=" + createdDate +
                '}';
    }
}
