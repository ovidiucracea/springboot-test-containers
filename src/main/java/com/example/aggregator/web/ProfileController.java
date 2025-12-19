package com.example.aggregator.web;

import com.example.aggregator.model.AggregatedProfile;
import com.example.aggregator.service.AggregationService;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/profiles")
@Validated
public class ProfileController {

    private final AggregationService aggregationService;

    public ProfileController(AggregationService aggregationService) {
        this.aggregationService = aggregationService;
    }

    @GetMapping("/{userId}/summary")
    public ResponseEntity<AggregatedProfile> getProfileSummary(@PathVariable @NotBlank String userId) {
        return ResponseEntity.ok(aggregationService.aggregateProfile(userId));
    }
}
