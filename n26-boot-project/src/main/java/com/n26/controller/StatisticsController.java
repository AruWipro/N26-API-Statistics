package com.n26.controller;

import static org.springframework.http.HttpStatus.OK;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.n26.dto.StatisticsDTO;
import com.n26.service.StatisticsService;

// TODO: Auto-generated Javadoc
/**
 * The Class StatisticsController.
 * Returns the latest statistics for the past
 * one minute
 */
@RestController
@RequestMapping("statistics")
public class StatisticsController {

    /** The statistics service. */
    @Autowired
    private StatisticsService statisticsService;

    /**
     * Gets the transactions.
     *
     * @return the transactions
     */
    @GetMapping("/")
    public ResponseEntity<StatisticsDTO> getTransactions() {
        return new ResponseEntity<>(statisticsService.getLatestStats(), OK);
    }
}
