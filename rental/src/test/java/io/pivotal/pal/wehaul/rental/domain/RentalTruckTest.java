package io.pivotal.pal.wehaul.rental.domain;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

public class RentalTruckTest {

    @Test
    public void buyTruck() {
        Vin vin = Vin.of("test-0001");
        TruckSize truckSize = TruckSize.LARGE;


        RentalTruck rentalTruck = new RentalTruck(vin, truckSize);


        assertThat(rentalTruck.getVin()).isEqualTo(Vin.of("test-0001"));
        assertThat(rentalTruck.getStatus()).isEqualTo(RentalTruckStatus.RENTABLE);
        assertThat(rentalTruck.getTruckSize()).isEqualTo(truckSize);
    }

    @Test
    public void reserve() {
        RentalTruck rentalTruck = new RentalTruck(Vin.of("test-0001"), null);


        rentalTruck.reserve();


        assertThat(rentalTruck.getStatus()).isEqualTo(RentalTruckStatus.RESERVED);
    }

    @Test
    public void pickUp() {
        RentalTruck rentalTruck = new RentalTruck(Vin.of("test-0001"), null);
        rentalTruck.reserve();


        rentalTruck.pickUp();


        assertThat(rentalTruck.getStatus()).isEqualTo(RentalTruckStatus.RENTED);
    }

    @Test
    public void dropOff() {
        RentalTruck rentalTruck = new RentalTruck(Vin.of("test-0001"), TruckSize.LARGE);
        rentalTruck.reserve();
        rentalTruck.pickUp();


        rentalTruck.dropOff();


        assertThat(rentalTruck.getStatus()).isEqualTo(RentalTruckStatus.RENTABLE);
    }

    @Test
    public void preventRenting() {
        RentalTruck rentalTruck = new RentalTruck(Vin.of("test-0001"), TruckSize.LARGE);


        rentalTruck.preventRenting();


        assertThat(rentalTruck.getStatus()).isEqualTo(RentalTruckStatus.NOT_RENTABLE);
    }

    @Test
    public void allowRenting() {
        RentalTruck rentalTruck = new RentalTruck(Vin.of("some-vin"), TruckSize.LARGE);
        rentalTruck.preventRenting();


        rentalTruck.allowRenting();


        assertThat(rentalTruck.getStatus()).isEqualTo(RentalTruckStatus.RENTABLE);
    }

    @Test
    public void allowRenting_whenAlreadyRentable() {
        RentalTruck rentalTruck = new RentalTruck(Vin.of("some-vin"), TruckSize.LARGE);


        assertThatExceptionOfType(IllegalStateException.class)
                .isThrownBy(() -> rentalTruck.allowRenting())
                .withMessage("Truck is not rentable");
    }

    @Test
    public void preventRenting_whenAnythingButRentable() {
        RentalTruck rentalTruck = new RentalTruck(Vin.of("test-0001"), TruckSize.LARGE);
        rentalTruck.reserve();


        assertThatExceptionOfType(IllegalStateException.class)
                .isThrownBy(() -> rentalTruck.preventRenting())
                .withMessage("Truck cannot be prevented from renting");

        assertThat(rentalTruck.getStatus()).isEqualTo(RentalTruckStatus.RESERVED);
    }

    @Test
    public void reserveTruck_whenNotRentable() {
        RentalTruck rentalTruck = new RentalTruck(Vin.of("test-0001"), null);
        rentalTruck.preventRenting();


        assertThatExceptionOfType(IllegalStateException.class)
                .isThrownBy(() -> rentalTruck.reserve())
                .withMessage("Truck cannot be reserved");
    }

    @Test
    public void pickUp_whenNotReserved() {
        RentalTruck rentalTruck = new RentalTruck(Vin.of("test-0001"), null);


        assertThatExceptionOfType(IllegalStateException.class)
                .isThrownBy(() -> rentalTruck.pickUp())
                .withMessage("Only reserved trucks can be picked up");
    }

    @Test
    public void dropOff_whenNotRented() {
        RentalTruck rentalTruck = new RentalTruck(Vin.of("test-0001"), null);


        assertThatExceptionOfType(IllegalStateException.class)
                .isThrownBy(() -> rentalTruck.dropOff())
                .withMessage("Truck is not currently rented");
    }
}
