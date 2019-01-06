package com.myapp.videoserver.communication.controllers;

import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/gui")
public class GuiController {

    @PutMapping(value = "/threshold")
    public void updateThresholdValue(@RequestBody String threshold){
        System.out.println(threshold);
    }
}