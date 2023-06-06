package com.maximilianwiegmann.backend.statistics;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/statistics")
@CrossOrigin(origins = "*", maxAge = 3600)
@RequiredArgsConstructor
public class StatisticController {
    
    private final StatisticHandler statisticHandler;

    @GetMapping
    public ResponseEntity<?> getStatistics(@RequestParam long from, @RequestParam long to) {
        return ResponseEntity.ok(statisticHandler.get(from, to).toString());
    }

}
