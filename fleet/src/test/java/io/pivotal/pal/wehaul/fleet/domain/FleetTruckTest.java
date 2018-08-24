package io.pivotal.pal.wehaul.fleet.domain;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

public class FleetTruckTest {

    @Test
    public void buyTruck() {
        Vin vin = Vin.of("test-0001");
        int odometerReading = 0;
        int truckLength = 25;


        FleetTruck fleetTruck = new FleetTruck(vin, odometerReading, truckLength);


        assertThat(fleetTruck.getVin()).isEqualTo(Vin.of("test-0001"));
        assertThat(fleetTruck.getStatus()).isEqualTo(FleetTruckStatus.INSPECTABLE);
        assertThat(fleetTruck.getOdometerReading()).isEqualTo(odometerReading);
    }

    @Test
    public void sendForInspection() {
        FleetTruck fleetTruck = new FleetTruck(Vin.of("test-0001"), 0, 10);


        fleetTruck.sendForInspection();


        assertThat(fleetTruck.getStatus()).isEqualTo(FleetTruckStatus.IN_INSPECTION);
    }

    @Test
    public void returnFromInspection() {
        Vin vin = Vin.of("test-0001");
        FleetTruck fleetTruck = new FleetTruck(vin, 0, 10);
        fleetTruck.sendForInspection();
        int odometerReading = 1;
        String notes = "some-notes";
        TruckInspection truckInspection = new TruckInspection(vin, odometerReading, notes);


        fleetTruck.returnFromInspection(notes, odometerReading);


        assertThat(fleetTruck.getStatus()).isEqualTo(FleetTruckStatus.INSPECTABLE);
        assertThat(fleetTruck.getOdometerReading()).isEqualTo(odometerReading);
        assertThat(fleetTruck.getInspections().size()).isEqualTo(1);
        assertThat(fleetTruck.getInspections().get(0)).isEqualToComparingOnlyGivenFields(truckInspection, "truckVin", "odometerReading", "notes");
    }

    @Test
    public void removeFromYard() {
        FleetTruck truck = new FleetTruck(Vin.of("test-0001"), 0, 10);


        truck.removeFromYard();


        assertThat(truck.getStatus()).isEqualTo(FleetTruckStatus.NOT_INSPECTABLE);
    }

    @Test
    public void returnToYard() {
        FleetTruck fleetTruck = new FleetTruck(Vin.of("test-0001"), 0, 10);
        fleetTruck.removeFromYard();
        int distanceTraveled = 101;


        fleetTruck.returnToYard(distanceTraveled);


        assertThat(fleetTruck.getStatus()).isEqualTo(FleetTruckStatus.INSPECTABLE);
        assertThat(fleetTruck.getOdometerReading()).isEqualTo(distanceTraveled);
    }

    @Test
    public void buyTruck_negativeOdometer() {
        Vin vin = Vin.of("test-0001");
        int odometerReading = -10;
        int truckLength = 25;


        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> {
                    new FleetTruck(vin, odometerReading, truckLength);
                })
                .withMessage("Cannot buy a truck with negative odometer reading");
    }

    @Test
    public void sendForInspection_whenAnythingButInspectable() {
        FleetTruck fleetTruck = new FleetTruck(Vin.of("test-0001"), 0, 10);
        fleetTruck.sendForInspection();


        assertThatExceptionOfType(IllegalStateException.class)
                .isThrownBy(() -> fleetTruck.sendForInspection())
                .withMessage("Truck cannot be inspected");
    }

    @Test
    public void removeFromYard_whenInInspection() {
        FleetTruck truck = new FleetTruck(Vin.of("test-0001"), 0, 10);
        truck.sendForInspection();


        assertThatExceptionOfType(IllegalStateException.class)
                .isThrownBy(() -> truck.removeFromYard())
                .withMessage("Cannot remove truck, currently in inspection");
    }

    @Test
    public void returnFromInspection_whenNotInInspection() {
        FleetTruck fleetTruck = new FleetTruck(Vin.of("test-0001"), 0, 10);


        assertThatExceptionOfType(IllegalStateException.class)
                .isThrownBy(() -> fleetTruck.returnFromInspection("some-notes", 100))
                .withMessage("Truck is not currently in inspection");
    }

    @Test
    public void returnFromInspection_withLowerOdometerReading() {
        FleetTruck fleetTruck = new FleetTruck(Vin.of("test-0001"), 100, 10);
        fleetTruck.sendForInspection();


        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> fleetTruck.returnFromInspection("some-notes", 0))
                .withMessage("Odometer reading cannot be less than previous reading");
    }

    @Test
    public void returnToYard_whenInInspection() {
        FleetTruck fleetTruck = new FleetTruck(Vin.of("test-0001"), 100, 10);
        fleetTruck.sendForInspection();


        assertThatExceptionOfType(IllegalStateException.class)
                .isThrownBy(() -> fleetTruck.returnToYard(1))
                .withMessage("Truck is not currently out of yard");
    }

    @Test
    public void returnToYard_withNegativeDistanceTraveled() {
        FleetTruck fleetTruck = new FleetTruck(Vin.of("test-0001"), 100, 10);
        fleetTruck.removeFromYard();


        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> fleetTruck.returnToYard(-1))
                .withMessage("Distance traveled cannot be negative");
    }
}
