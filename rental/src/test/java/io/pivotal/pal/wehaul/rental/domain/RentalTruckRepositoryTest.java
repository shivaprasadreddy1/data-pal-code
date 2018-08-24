package io.pivotal.pal.wehaul.rental.domain;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;

@DirtiesContext
@RunWith(SpringRunner.class)
@DataJpaTest
public class RentalTruckRepositoryTest {

    @Autowired
    private RentalTruckRepository truckRepository;

    @Test
    public void findAllByTruckSizeAndStatus() {
        RentalTruck rentalTruck1 = new RentalTruck(Vin.of("test-0001"), TruckSize.LARGE);
        rentalTruck1.preventRenting();
        RentalTruck rentalTruck2 = new RentalTruck(Vin.of("test-0002"), TruckSize.LARGE);
        rentalTruck2.preventRenting();
        RentalTruck rentalTruck3 = new RentalTruck(Vin.of("test-0003"), TruckSize.LARGE);
        RentalTruck rentalTruck4 = new RentalTruck(Vin.of("test-0004"), TruckSize.SMALL);
        truckRepository.save(Arrays.asList(rentalTruck1, rentalTruck2, rentalTruck3, rentalTruck4));


        List<RentalTruck> rentalTrucks = truckRepository.findAllByTruckSizeAndStatus(TruckSize.LARGE, RentalTruckStatus.RENTABLE);


        Assertions.assertThat(rentalTrucks)
                .usingFieldByFieldElementComparator()
                .containsExactly(rentalTruck3);
    }

    @Test
    public void findAllByTruckSizeAndStatus_noneFound() {
        RentalTruck rentalTruck1 = new RentalTruck(Vin.of("test-0001"), TruckSize.SMALL);
        rentalTruck1.preventRenting();
        truckRepository.save(rentalTruck1);


        List<RentalTruck> rentalTrucks = truckRepository.findAllByTruckSizeAndStatus(TruckSize.LARGE, RentalTruckStatus.RENTABLE);


        Assertions.assertThat(rentalTrucks).isEmpty();
    }

    @EnableAutoConfiguration
    @SpringBootConfiguration
    static class ContextConfig {
    }
}
