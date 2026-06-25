package com.smartstay.laboroptimizer.controller;

import com.smartstay.laboroptimizer.model.StaffingRequest;
import com.smartstay.laboroptimizer.model.StaffingResponse;
import com.smartstay.laboroptimizer.service.StaffingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/staffing")
@CrossOrigin(origins = "*")
public class StaffingController {

    @Autowired
    private StaffingService staffingService;

    @PostMapping("/recommend")
    public StaffingResponse recommend(@RequestBody StaffingRequest request) {
        return staffingService.recommend(request);
    }
}