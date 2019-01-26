package com.myapp.videoserver.communication.controllers;

import com.myapp.videoserver.transforming.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/gui")
public class GuiController {

    @Autowired
    private ApplicationContext context;

    @PutMapping(value = "/threshold/bottom")
    public void updateThresholdBottomValue(@RequestBody String thresholdBottom){
        ImageService bean = context.getBean(ImageService.class);
        bean.setThresholdBottom(Integer.parseInt(thresholdBottom));
    }

    @PutMapping(value = "/threshold/top")
    public void updateThresholdTopValue(@RequestBody String thresholdTop){
        ImageService bean = context.getBean(ImageService.class);
        bean.setThresholdBottom(Integer.parseInt(thresholdTop));
    }
}