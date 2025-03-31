package org.mining.util.LanguageMetrics.LanguageAnalyzer;

import org.mining.util.inputparser.CodeAnalysisConfig;
import org.mining.util.inputparser.MetricEnum;

import java.util.Map;

public interface ILanguageMetricGenerator {
    void generateAnalyzer(Map<MetricEnum, CodeAnalysisConfig.MetricConfig> config);
}
