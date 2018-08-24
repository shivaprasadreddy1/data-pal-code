package io.pivotal.pal.wehaul.application;

import io.pivotal.pal.wehaul.fleet.domain.FleetCommandService;
import io.pivotal.pal.wehaul.fleet.domain.Vin;
import io.pivotal.pal.wehaul.rental.domain.event.RentalTruckDroppedOff;
import io.pivotal.pal.wehaul.rental.domain.event.RentalTruckReserved;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@Async
public class RentalEventListener {

    private final FleetCommandService fleetCommandService;

    public RentalEventListener(FleetCommandService fleetCommandService) {
        this.fleetCommandService = fleetCommandService;
    }

    @EventListener
    public void onRentalTruckReserved(RentalTruckReserved event) {
        fleetCommandService.removeFromYard(Vin.of(event.getVin()));
    }

    @EventListener
    public void onRentalTruckDroppedOff(RentalTruckDroppedOff event) {
        fleetCommandService.returnToYard(Vin.of(event.getVin()), event.getDistanceTraveled());
    }
}
