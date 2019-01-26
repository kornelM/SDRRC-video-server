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

    private final ApplicationContext context;

    @Autowired
    public GuiController(ApplicationContext context) {
        this.context = context;
    }

    @PutMapping(value = "/threshold/bottom")
    public void updateThresholdBottomValue(@RequestBody String thresholdBottom) {
        ImageService bean = context.getBean(ImageService.class);
        bean.setThresholdBottom(Integer.parseInt(thresholdBottom));
    }

    @PutMapping(value = "/threshold/top")
    public void updateThresholdTopValue(@RequestBody String thresholdTop) {
        ImageService bean = context.getBean(ImageService.class);
        bean.setThresholdTop(Integer.parseInt(thresholdTop));
    }

    @PutMapping(value = "/threshold/houghLinesP")
    public void updateHoughLinesPThresholdValue(@RequestBody String houghLinesP) {
        ImageService bean = context.getBean(ImageService.class);
        bean.setHoughLinesPThreshold(Integer.parseInt(houghLinesP));
    }

    @PutMapping(value = "/minLineLength")
    public void updateMinLineLengthValue(@RequestBody String minLineLength) {
        ImageService bean = context.getBean(ImageService.class);
        bean.setMinLineLength(Double.parseDouble(minLineLength));
    }

    @PutMapping(value = "/maxLineGap")
    public void updateMaxLineGapValue(@RequestBody String maxLineGap) {
        ImageService bean = context.getBean(ImageService.class);
        bean.setMaxLineGap(Double.parseDouble(maxLineGap));
    }

    @PutMapping(value = "/rho")
    public void updateRhoValue(@RequestBody String rho) {
        ImageService bean = context.getBean(ImageService.class);
        bean.setRho(Double.parseDouble(rho));
    }
}