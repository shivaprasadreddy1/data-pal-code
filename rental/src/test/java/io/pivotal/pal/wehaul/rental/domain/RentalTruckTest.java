package io.pivotal.pal.wehaul.rental.domain;

import io.pivotal.pal.wehaul.rental.domain.event.RentalTruckDroppedOff;
import io.pivotal.pal.wehaul.rental.domain.event.RentalTruckReserved;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@SuppressWarnings("deprecation")
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


        rentalTruck.reserve("some-customer-name");


        assertThat(rentalTruck.getStatus()).isEqualTo(RentalTruckStatus.RESERVED);
        assertThat(rentalTruck.getRental()).isEqualToComparingOnlyGivenFields(
                new Rental("some-customer-name", Vin.of("test-0001")),
                "customerName", "truckVin", "distanceTraveled");
        assertThat(rentalTruck.getDomainEvents()).hasSize(1);
        assertThat(rentalTruck.getDomainEvents().get(0))
                .isEqualToIgnoringGivenFields(new RentalTruckReserved("test-0001"), "createdDate");
    }

    @Test
    public void pickUp() {
        RentalTruck rentalTruck = new RentalTruck(Vin.of("test-0001"), null);
        rentalTruck.reserve("some-customer-name");


        rentalTruck.pickUp();


        assertThat(rentalTruck.getStatus()).isEqualTo(RentalTruckStatus.RENTED);
        assertThat(rentalTruck.getRental().getDistanceTraveled()).isEqualTo(0);
    }

    @Test
    public void dropOff() {
        RentalTruck rentalTruck = new RentalTruck(Vin.of("test-0001"), TruckSize.LARGE);
        rentalTruck.reserve("some-customer-name");
        rentalTruck.pickUp();


        rentalTruck.dropOff(10);


        assertThat(rentalTruck.getStatus()).isEqualTo(RentalTruckStatus.RENTABLE);
        assertThat(rentalTruck.getRental().getDistanceTraveled()).isEqualTo(10);
        assertThat(rentalTruck.getDomainEvents()).hasSize(2);
        assertThat(rentalTruck.getDomainEvents().get(1))
                .isEqualToIgnoringGivenFields(new RentalTruckDroppedOff("test-0001", 10), "createdDate");
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
        rentalTruck.reserve("some-customer-name");


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
                .isThrownBy(() -> rentalTruck.reserve("some-customer-name"))
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
                .isThrownBy(() -> rentalTruck.dropOff(0))
                .withMessage("Truck is not currently rented");
    }
}
