package io.pivotal.pal.wehaul.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.pivotal.pal.wehaul.fleet.domain.FleetService;
import io.pivotal.pal.wehaul.rental.domain.*;
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

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(MockitoJUnitRunner.class)
public class RentalControllerTest {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private RentalService mockRentalService;
    @Mock
    private FleetService mockFleetService;

    private MockMvc mockMvc;

    @Before
    public void setUp() {
        RentalController rentalController = new RentalController(mockRentalService, mockFleetService);
        mockMvc = MockMvcBuilders.standaloneSetup(rentalController).build();
    }

    @Test
    public void createRental() throws Exception {
        RentalTruck mockRentalTruck = mock(RentalTruck.class);
        when(mockRentalTruck.getVin()).thenReturn(Vin.of("some-vin"));
        when(mockRentalService.create(any(), any())).thenReturn(mockRentalTruck);

        RentalController.CreateRentalDto requestDto = new RentalController.CreateRentalDto("some-customer-name", "SMALL");
        String requestBody = objectMapper.writeValueAsString(requestDto);


        mockMvc
                .perform(
                        post("/rentals")
                                .contentType(MediaType.APPLICATION_JSON_UTF8)
                                .content(requestBody)
                )
                .andExpect(status().isOk());


        InOrder inOrder = inOrder(mockRentalService, mockFleetService);
        inOrder.verify(mockRentalService).create("some-customer-name", TruckSize.SMALL);
        io.pivotal.pal.wehaul.fleet.domain.Vin fleetVin =
                io.pivotal.pal.wehaul.fleet.domain.Vin.of("some-vin");
        inOrder.verify(mockFleetService).removeFromYard(fleetVin);
    }

    @Test
    public void pickUpRental() throws Exception {
        mockMvc
                .perform(
                        post("/rentals/00000000-0000-0000-0000-000000000000/pick-up")
                )
                .andExpect(status().isOk());


        verify(mockRentalService).pickUp(ConfirmationNumber.of("00000000-0000-0000-0000-000000000000"));
    }

    @Test
    public void dropOffRental() throws Exception {
        RentalTruck mockRentalTruck = mock(RentalTruck.class);
        Vin vin = Vin.of("some-vin");
        when(mockRentalTruck.getVin()).thenReturn(vin);
        when(mockRentalService.dropOff(any(), anyInt())).thenReturn(mockRentalTruck);

        RentalController.DropOffRentalDto requestDto = new RentalController.DropOffRentalDto(500);
        String requestBody = objectMapper.writeValueAsString(requestDto);


        mockMvc
                .perform(
                        post("/rentals/00000000-0000-0000-0000-000000000000/drop-off")
                                .contentType(MediaType.APPLICATION_JSON_UTF8)
                                .content(requestBody)
                )
                .andExpect(status().isOk());


        InOrder inOrder = inOrder(mockRentalService, mockFleetService);
        inOrder.verify(mockRentalService).dropOff(ConfirmationNumber.of("00000000-0000-0000-0000-000000000000"), 500);
        io.pivotal.pal.wehaul.fleet.domain.Vin fleetVin = io.pivotal.pal.wehaul.fleet.domain.Vin.of("some-vin");
        inOrder.verify(mockFleetService).returnToYard(fleetVin, 500);
    }

    @Test
    public void getAllRentals() throws Exception {
        RentalTruck rentalTruck1 = new RentalTruck(Vin.of("test-0001"), TruckSize.SMALL);
        rentalTruck1.reserve("some-customer-name-1");
        RentalTruck rentalTruck2 = new RentalTruck(Vin.of("test-0002"), TruckSize.LARGE);
        rentalTruck2.reserve("some-customer-name-2");
        List<RentalTruck> rentals = Arrays.asList(rentalTruck1, rentalTruck2);
        when(mockRentalService.findAll()).thenReturn(rentals);

        RentalController.RentalDto rentalDto1 = new RentalController.RentalDto(
                rentalTruck1.getRental().getConfirmationNumber().getConfirmationNumber().toString(),
                "some-customer-name-1",
                "test-0001",
                null
        );
        RentalController.RentalDto rentalDto2 = new RentalController.RentalDto(
                rentalTruck2.getRental().getConfirmationNumber().getConfirmationNumber().toString(),
                "some-customer-name-2",
                "test-0002",
                null
        );
        List<RentalController.RentalDto> rentalDtos = Arrays.asList(rentalDto1, rentalDto2);
        String expectedResponseBody = objectMapper.writeValueAsString(rentalDtos);


        mockMvc
                .perform(
                        get("/rentals")
                )
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResponseBody));


        verify(mockRentalService).findAll();
    }
}
