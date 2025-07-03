package com.dashboard.service;

import com.common.domain.dto.dashboard.inspect.InspectionStatsDTO;
import com.common.domain.dto.dashboard.inspect.InspectionTypeStats;

import java.util.List;

public interface InspectionStatsService {

     InspectionStatsDTO getInspectionStats();

     void calculateRates(InspectionStatsDTO stats);

     <T extends InspectionTypeStats> List<T> calculateRatios(List<T> stats, int total);
}
