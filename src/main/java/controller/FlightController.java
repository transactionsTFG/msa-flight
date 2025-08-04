package controller;

import java.util.List;

import javax.ejb.EJB;
import javax.ws.rs.BeanParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import business.dto.FlightParamsDTO;
import business.flight.FlightDTO;
import business.flightinstance.FlightInstanceDTO;
import business.services.FlightInstanceService;

@Path("/flights")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class FlightController {
    private static final Logger LOGGER = LogManager.getLogger(FlightController.class);
    private FlightInstanceService flightInstanceService;

    @GET
    @Path("/{idFlight}")
    public FlightDTO getFlight(@PathParam("idFlight") long idFlight) {
        LOGGER.info("Fetching flight with ID: {}", idFlight);
        return this.flightInstanceService.getFlightById(idFlight);
    }

    @GET
    @Path("/flight-info")
    public List<FlightDTO> getFlightInfo(@BeanParam FlightParamsDTO flightParams) {
        LOGGER.info("Fetching flight information");
        return this.flightInstanceService.getFlightsByParams(flightParams);
    }

    @GET
    @Path("/instance/{intanceId}")
    public FlightInstanceDTO getFlightInstance(@PathParam("instanceId") long instanceId) {
        LOGGER.info("Fetching flight instance with ID: {}", instanceId);
        return this.flightInstanceService.getFlightInstanceById(instanceId);
    }
    

    @EJB
    public void setFlightInstanceService(FlightInstanceService flightInstanceService) {
        this.flightInstanceService = flightInstanceService;
    }
}
