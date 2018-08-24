package io.pivotal.pal.wehaul.fleet.domain;

import java.util.Objects;

public class FleetTruckUpdated {

    private final String vin;
    private final int version;
    private final String status;
    private final Integer odometerReading;
    private final Integer truckLength;
    private final Integer lastInspectionOdometerReading;

    public FleetTruckUpdated(FleetTruck fleetTruck) {
        this.vin = fleetTruck.getVin().getVin();
        this.version = fleetTruck.getVersion();
        this.status = fleetTruck.getStatus().toString();
        this.odometerReading = fleetTruck.getOdometerReading();
        this.truckLength = fleetTruck.getTruckLength();

        if (fleetTruck.getInspections().size() == 0) {
            this.lastInspectionOdometerReading = null;
        } else {
            this.lastInspectionOdometerReading = fleetTruck.getInspections()
                    .get(fleetTruck.getInspections().size() - 1)
                    .getOdometerReading();
        }
    }

    private FleetTruckUpdated() {
        this.vin = null;
        this.version = -1;
        this.status = null;
        this.odometerReading = null;
        this.truckLength = null;
        this.lastInspectionOdometerReading = null;
    }

    public String getVin() {
        return vin;
    }

    public int getVersion() {
        return version;
    }

    public String getStatus() {
        return status;
    }

    public Integer getOdometerReading() {
        return odometerReading;
    }

    public Integer getTruckLength() {
        return truckLength;
    }

    public Integer getLastInspectionOdometerReading() {
        return lastInspectionOdometerReading;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FleetTruckUpdated that = (FleetTruckUpdated) o;
        return version == that.version &&
                Objects.equals(vin, that.vin) &&
                Objects.equals(status, that.status) &&
                Objects.equals(odometerReading, that.odometerReading) &&
                Objects.equals(truckLength, that.truckLength) &&
                Objects.equals(lastInspectionOdometerReading, that.lastInspectionOdometerReading);
    }

    @Override
    public int hashCode() {
        return Objects.hash(vin, version, status, odometerReading, truckLength, lastInspectionOdometerReading);
    }

    @Override
    public String toString() {
        return "FleetTruckUpdated{" +
                "vin='" + vin + '\'' +
                ", version=" + version +
                ", status='" + status + '\'' +
                ", odometerReading=" + odometerReading +
                ", truckLength=" + truckLength +
                ", lastInspectionOdometerReading=" + lastInspectionOdometerReading +
                '}';
    }
}
