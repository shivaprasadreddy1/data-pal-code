package io.pivotal.pal.wehaul.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.pivotal.pal.wehaul.fleet.domain.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(MockitoJUnitRunner.class)
public class FleetControllerTest {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private FleetCommandService mockFleetCommandService;
    @Mock
    private FleetQueryService mockFleetQueryService;

    private MockMvc mockMvc;

    @Before
    public void setUp() {
        FleetController fleetController = new FleetController(mockFleetCommandService, mockFleetQueryService);
        mockMvc = MockMvcBuilders.standaloneSetup(fleetController).build();
    }

    @Test
    public void buyTruck() throws Exception {
        FleetTruck mockFleetTruck = mock(FleetTruck.class);
        when(mockFleetTruck.getVin()).thenReturn(Vin.of("some-vin"));

        FleetController.BuyTruckDto requestDto =
                new FleetController.BuyTruckDto("some-vin", 1000, 25);
        String requestBody = objectMapper.writeValueAsString(requestDto);


        mockMvc
                .perform(
                        post("/fleet/trucks")
                                .contentType(MediaType.APPLICATION_JSON_UTF8)
                                .content(requestBody)
                )
                .andExpect(status().isOk());


        verify(mockFleetCommandService).buyTruck(Vin.of("some-vin"), 1000, 25);
    }

    @Test
    public void getAllTrucks() throws Exception {
        FleetTruckSnapshot fleetTruckSnapshot1 = new FleetTruckSnapshot("some-vin-1", FleetTruckStatus.INSPECTABLE.toString(), 1000, 25, 500);
        FleetTruckSnapshot fleetTruckSnapshot2 = new FleetTruckSnapshot("some-vin-2", FleetTruckStatus.INSPECTABLE.toString(), 2000, 15, null);
        List<FleetTruckSnapshot> fleetTruckSnapshots = Arrays.asList(fleetTruckSnapshot1, fleetTruckSnapshot2);
        when(mockFleetQueryService.findAll()).thenReturn(fleetTruckSnapshots);

        FleetController.TruckDto truckDto1 = new FleetController.TruckDto("some-vin-1", "INSPECTABLE", 1000, 25, 500);
        FleetController.TruckDto truckDto2 = new FleetController.TruckDto("some-vin-2", "INSPECTABLE", 2000, 15, null);
        List<FleetController.TruckDto> truckDtos = Arrays.asList(truckDto1, truckDto2);
        String expectedResponseBody = objectMapper.writeValueAsString(truckDtos);


        mockMvc
                .perform(
                        get("/fleet/trucks")
                )
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResponseBody));


        verify(mockFleetQueryService).findAll();
    }

    @Test
    public void sendForInspection() throws Exception {
        mockMvc
                .perform(
                        post("/fleet/trucks/some-vin/send-for-inspection")
                )
                .andExpect(status().isOk());


        InOrder inOrder = inOrder(mockFleetCommandService);
        inOrder.verify(mockFleetCommandService).sendForInspection(Vin.of("some-vin"));
    }

    @Test
    public void returnFromInspection() throws Exception {
        FleetController.ReturnFromInspectionDto requestDto =
                new FleetController.ReturnFromInspectionDto("some-notes", 2000);
        String requestBody = objectMapper.writeValueAsString(requestDto);


        mockMvc
                .perform(
                        post("/fleet/trucks/some-vin/return-from-inspection")
                                .contentType(MediaType.APPLICATION_JSON_UTF8)
                                .content(requestBody)
                )
                .andExpect(status().isOk());


        verify(mockFleetCommandService).returnFromInspection(Vin.of("some-vin"), "some-notes", 2000);
    }
}
