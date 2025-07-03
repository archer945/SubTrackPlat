package com.dashboard.service.impl;

import com.common.domain.dto.dashboard.defect.DefectStatsDTO;
import com.common.domain.dto.dashboard.defect.DefectTypeDTO;
import com.common.domain.dto.dashboard.defect.DefectValidityCountDTO;
import com.dashboard.mapper.DefectMapper;
import com.dashboard.service.DefectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;
import java.util.List;


@Service
@Component
public class DefectServiceImpl implements DefectService {

    @Autowired
    private DefectMapper defectMapper;
    @Override
    public List<DefectTypeDTO> getDefectType() {
        List<DefectTypeDTO> defectTypes = defectMapper.getDefectType();

        // 计算总数
        int total = defectTypes.stream()
                .mapToInt(DefectTypeDTO::getCount)
                .sum();

        // 计算各类型占比
        if (total > 0) {
            for (DefectTypeDTO dto : defectTypes) {
                double ratio = formatPercent(dto.getCount(), total);
                dto.setRatio(ratio);
            }
        }

        return defectTypes;
    }

    @Override
    public  DefectStatsDTO getDefectStats(){

                DefectStatsDTO stats = new DefectStatsDTO();



                // 1. 使用 DefectValidityCountDTO 接收
                DefectValidityCountDTO validity = defectMapper.countDefectValidity();
                stats.setTotalDefects(validity.getTotal());
                stats.setConfirmedValidDefects(validity.getValid());
                stats.setFalseReportDefects(validity.getFalseReport());

                // 2. 计算各种比率
                calculateRates(stats, validity.getTotal());

                // 3. 获取分布数据
                stats.setTypeDistribution(calculateRatios(
                        defectMapper.countByType(),
                        stats.getTotalDefects()
                ));

                stats.setSeverityDistribution(calculateRatios(
                        defectMapper.countBySeverity(),
                        stats.getTotalDefects()
                ));

                stats.setStatusDistribution(calculateRatios(
                        defectMapper.countByStatus(),
                        stats.getTotalDefects()
                ));

                // 4. 新增有效性分布
                stats.setValidityDistribution(calculateRatios(
                        defectMapper.countByValidity(),
                        stats.getTotalDefects()
                ));

                // 5. 趋势数据
                stats.setRecentTrend(defectMapper.countRecentTrend());

                return stats;
            }

            private void calculateRates(DefectStatsDTO stats, Integer total) {
                if (total > 0) {
                    stats.setConfirmedValidRate(formatPercent(stats.getConfirmedValidDefects(), total));
                    stats.setFalseReportRate(formatPercent(stats.getFalseReportDefects(), total));
                } else {
                    stats.setConfirmedValidRate(0.0);
                    stats.setFalseReportRate(0.0);
                }
            }

            private Double formatPercent(Integer part, Integer total) {
                return Double.parseDouble(String.format("%.2f", part * 100.0 / total));
            }

            private <T> List<T> calculateRatios(List<T> stats, Integer total) {
                if (stats != null && total > 0) {
                    stats.forEach(s -> {
                        try {
                            Method setRatio = s.getClass().getMethod("setRatio", Double.class);
                            Method getCount = s.getClass().getMethod("getCount");
                            Integer count = (Integer) getCount.invoke(s);
                            setRatio.invoke(s, formatPercent(count, total));
                        } catch (Exception e) {
                            throw new RuntimeException("统计DTO必须包含count和ratio字段", e);
                        }
                    });
                }
                return stats;
            }
}
