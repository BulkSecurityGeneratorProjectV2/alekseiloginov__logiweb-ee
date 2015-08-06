package com.tsystems.javaschool.loginov.logiweb.controllers;

import com.tsystems.javaschool.loginov.logiweb.exceptions.DuplicateEntryException;
import com.tsystems.javaschool.loginov.logiweb.exceptions.PlateNumberIncorrectException;
import com.tsystems.javaschool.loginov.logiweb.models.Location;
import com.tsystems.javaschool.loginov.logiweb.models.Truck;
import com.tsystems.javaschool.loginov.logiweb.services.TruckService;
import com.tsystems.javaschool.loginov.logiweb.utils.GsonParser;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Spring MVC Controller to work with the truck data.
 */
@Controller
public class TruckController {
    private static Logger logger = Logger.getLogger(TruckController.class);

    @Autowired
    private TruckService truckService;

    @Autowired
    private GsonParser gsonParser;

    /**
     * Redirects user to the truck page.
     */
    @RequestMapping(value = "/trucks", method = RequestMethod.GET)
    public String getTruckPage() {
        return "secure/manager/trucks";
    }

    /**
     * Fetches a list of all trucks using the TruckService and puts it to the result map.
     */
    @RequestMapping(value = "/TruckList.do", method = RequestMethod.POST)
    public void getAllTrucks(HttpServletResponse resp) throws IOException {
        List truckList = truckService.listTrucks();
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("data", truckList);
        gsonParser.parse(resultMap, resp);
    }

    /**
     * Adds a truck to the database using the TruckService and puts the saved object back to the result map.
     */
    @RequestMapping(value = "/TruckSave.do", method = RequestMethod.POST)
    public void saveTruck(@RequestParam(value = "plate_number") String plate_number,
                          @RequestParam(value = "driver_number") int driver_number,
                          @RequestParam(value = "capacity") int capacity,
                          @RequestParam(value = "drivable") int drivable,
                          @RequestParam(value = "location") String city,
                          HttpServletResponse resp) throws IOException {

        Map<String, Object> resultMap = new HashMap<>();

        try {
            Truck savedTruck =
                    truckService.addTruck(new Truck(plate_number, driver_number, capacity, drivable, new Location(city)));
            resultMap.put("datum", savedTruck);

        } catch (PlateNumberIncorrectException e) {
            logger.error("Plate number incorrect: " + plate_number, e);
            resultMap.put("jTableError", "Plate number should contain 2 letters and 5 digits.");
        } catch (DuplicateEntryException e) {
            logger.error("Duplicate entry: " + plate_number, e);
            resultMap.put("jTableError", "Plate number is unique and this one is already present in the database.");
        }

        gsonParser.parse(resultMap, resp);
    }

    /**
     * Updates a truck in the database using the TruckService and puts the updated truck back to the result map.
     */
    @RequestMapping(value = "/TruckUpdate.do", method = RequestMethod.POST)
    public void updateTruck(@RequestParam(value = "id") int id,
                            @RequestParam(value = "plate_number") String plate_number,
                            @RequestParam(value = "driver_number") int driver_number,
                            @RequestParam(value = "capacity") int capacity,
                            @RequestParam(value = "drivable") int drivable,
                            @RequestParam(value = "location") String city,
                            HttpServletResponse resp) throws IOException {

        Map<String, Object> resultMap = new HashMap<>();

        try {
            Truck updatedTruck =
                    truckService.updateTruck(new Truck(id, plate_number, driver_number, capacity, drivable, new Location(city)));
            resultMap.put("datum", updatedTruck);

        } catch (PlateNumberIncorrectException e) {
            logger.error("Plate number incorrect: " + plate_number, e);
            resultMap.put("jTableError", "Plate number should contain 2 letters and 5 digits.");
        } catch (DuplicateEntryException e) {
            logger.error("Duplicate entry: " + plate_number, e);
            resultMap.put("jTableError", "Plate number is unique and this one is already present in the database.");
        }

        gsonParser.parse(resultMap, resp);
    }

    /**
     * Deletes a truck from the database using the TruckService and puts "OK" back to the result map.
     */
    @RequestMapping(value = "/TruckDelete.do", method = RequestMethod.POST)
    public void deleteTruck(@RequestParam(value = "id") int id, HttpServletResponse resp) throws IOException {
        Map<String, Object> resultMap = new HashMap<>();
        truckService.removeTruck(id);
        resultMap.put("OK", "OK");
        gsonParser.parse(resultMap, resp);
    }

    /**
     * Fetches a list of valid truck options using the TruckService and puts a returned JSON string to the result map.
     */
    @RequestMapping(value = "/TruckOptions.do", method = RequestMethod.POST)
    public void getAllTruckOptions(HttpServletResponse resp) throws IOException {
        Map<String, Object> resultMap = new HashMap<>();
        String truckOptionJSONList = truckService.getTruckOptions();
        resultMap.put("options", truckOptionJSONList);
        gsonParser.parse(resultMap, resp);
    }
}
