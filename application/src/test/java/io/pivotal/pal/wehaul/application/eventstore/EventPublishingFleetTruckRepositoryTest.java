package io.pivotal.pal.wehaul.application.eventstore;

import io.pivotal.pal.wehaul.fleet.domain.FleetTruck;
import io.pivotal.pal.wehaul.fleet.domain.FleetTruckRepository;
import io.pivotal.pal.wehaul.fleet.domain.FleetTruckStatus;
import io.pivotal.pal.wehaul.fleet.domain.Vin;
import io.pivotal.pal.wehaul.fleet.domain.event.FleetTruckEvent;
import io.pivotal.pal.wehaul.fleet.domain.event.FleetTruckPurchased;
import io.pivotal.pal.wehaul.fleet.domain.event.FleetTruckSentForInspection;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class EventPublishingFleetTruckRepositoryTest {

    @Mock
    private FleetTruckRepository mockFleetTruckRepository;
    @Mock
    private ApplicationEventPublisher mockApplicationEventPublisher;

    private EventPublishingFleetTruckRepository repository;

    @Before
    public void setUp() {
        repository = new EventPublishingFleetTruckRepository(mockFleetTruckRepository, mockApplicationEventPublisher);
    }

    @Test
    public void save_delegatesSave() {
        FleetTruck mockReturnedFromDelegate = mock(FleetTruck.class);
        when(mockFleetTruckRepository.save(any())).thenReturn(mockReturnedFromDelegate);

        FleetTruckEvent fleetTruckEvent1 = new FleetTruckPurchased("vin", FleetTruckStatus.INSPECTABLE.toString(), 0, 0);
        FleetTruckEvent fleetTruckEvent2 = new FleetTruckSentForInspection("vin", FleetTruckStatus.IN_INSPECTION.toString());
        FleetTruck mockFleetTruck = mock(FleetTruck.class);
        when(mockFleetTruck.getVin()).thenReturn(Vin.of("vin"));
        when(mockFleetTruck.getDirtyEvents())
                .thenReturn(Arrays.asList(fleetTruckEvent1, fleetTruckEvent2));


        FleetTruck savedFleetTruck = repository.save(mockFleetTruck);


        verify(mockFleetTruckRepository).save(mockFleetTruck);

        assertThat(savedFleetTruck).isEqualTo(mockReturnedFromDelegate);
    }

    @Test
    public void save_publishEvents() {
        FleetTruckEvent fleetTruckEvent1 = new FleetTruckPurchased("some-vin", FleetTruckStatus.INSPECTABLE.toString(), 0, 0);
        FleetTruckEvent fleetTruckEvent2 = new FleetTruckSentForInspection("some-vin", FleetTruckStatus.IN_INSPECTION.toString());
        FleetTruck mockFleetTruck = mock(FleetTruck.class);
        when(mockFleetTruck.getVin()).thenReturn(Vin.of("some-vin"));
        when(mockFleetTruck.getDirtyEvents())
                .thenReturn(Arrays.asList(fleetTruckEvent1, fleetTruckEvent2));


        repository.save(mockFleetTruck);


        InOrder inOrder = inOrder(mockFleetTruckRepository, mockApplicationEventPublisher);

        inOrder.verify(mockFleetTruckRepository).save(mockFleetTruck);

        inOrder.verify(mockApplicationEventPublisher).publishEvent(fleetTruckEvent1);
        inOrder.verify(mockApplicationEventPublisher).publishEvent(fleetTruckEvent2);
    }

    @Test
    public void findOne_delegatesFindOne() {
        FleetTruck mockFleetTruckFromDelegate = mock(FleetTruck.class);
        when(mockFleetTruckRepository.findOne(any())).thenReturn(mockFleetTruckFromDelegate);


        FleetTruck fleetTruck = repository.findOne(Vin.of("some-vin"));


        verify(mockFleetTruckRepository).findOne(Vin.of("some-vin"));

        assertThat(fleetTruck).isEqualTo(mockFleetTruckFromDelegate);
    }

    @Test
    public void findAll_delegatesFindAll() {
        List<FleetTruck> fleetTrucksFromDelegate =
                Arrays.asList(mock(FleetTruck.class), mock(FleetTruck.class));

        when(mockFleetTruckRepository.findAll()).thenReturn(fleetTrucksFromDelegate);


        List<FleetTruck> fleetTrucks = repository.findAll();


        verify(mockFleetTruckRepository).findAll();

        assertThat(fleetTrucks).isEqualTo(fleetTrucksFromDelegate);
    }
}
