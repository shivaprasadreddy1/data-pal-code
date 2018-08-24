package io.pivotal.pal.wehaul.fleet.domain;

import java.io.Serializable;
import java.util.Objects;

public class Vin implements Serializable {

    private final String vin;

    private Vin(String vin) {
        this.vin = vin;
    }

    public static Vin of(String vin) {
        return new Vin(vin);
    }

    public String getVin() {
        return vin;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vin vin1 = (Vin) o;
        return Objects.equals(vin, vin1.vin);
    }

    @Override
    public int hashCode() {
        return Objects.hash(vin);
    }

    @Override
    public String toString() {
        return "Vin{" +
                "vin='" + vin + '\'' +
                '}';
    }
}
