package io.pivotal.pal.wehaul.fleet.domain;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class FleetQueryServiceTest {

    @Mock
    private FleetTruckSnapshotRepository mockFleetTruckSnapshotRepository;

    private FleetQueryService fleetQueryService;

    @Before
    public void setUp() {
        fleetQueryService = new FleetQueryService(mockFleetTruckSnapshotRepository);
    }

    @Test
    public void findAll() {
        FleetTruckSnapshot mockFleetTruckSnapshot1 = mock(FleetTruckSnapshot.class);
        FleetTruckSnapshot mockFleetTruckSnapshot2 = mock(FleetTruckSnapshot.class);
        List<FleetTruckSnapshot> toBeReturned = Arrays.asList(mockFleetTruckSnapshot1, mockFleetTruckSnapshot2);
        when(mockFleetTruckSnapshotRepository.findAll()).thenReturn(toBeReturned);


        Collection<FleetTruckSnapshot> fleetTrucks = fleetQueryService.findAll();


        verify(mockFleetTruckSnapshotRepository).findAll();

        assertThat(fleetTrucks).hasSameElementsAs(toBeReturned);
    }
}
