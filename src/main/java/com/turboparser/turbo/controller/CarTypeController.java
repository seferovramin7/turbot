package com.turboparser.turbo.controller;

import com.turboparser.turbo.entity.CarTypeEntity;
import com.turboparser.turbo.util.DBactions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Controller
public class CarTypeController {

    @Autowired
    DBactions dBactions;

    @PostMapping(value = "/insertVehicleType", consumes = "application/json", produces = "application/json")
    public String insertCarType(@RequestBody CarTypeEntity carType) {
        dBactions.insertVehicleType(carType);
        return "DONE";
    }

    @GetMapping(value = "/getVehicleTypes", consumes = "application/json", produces = "application/json")
    public List<CarTypeEntity> getAllCarTypes() {
        List<CarTypeEntity> allVehicleTypes = dBactions.getAllVehicleTypes();
        return allVehicleTypes;
    }

}
