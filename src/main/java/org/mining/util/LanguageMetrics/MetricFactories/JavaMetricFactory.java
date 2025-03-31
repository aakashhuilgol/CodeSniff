package org.mining.util.LanguageMetrics.MetricFactories;

import org.mining.util.LanguageMetrics.LanguageAnalyzer.LanguageMetricGenerator.JavaMetrics.*;
import org.mining.util.inputparser.MetricEnum;

import java.util.HashMap;
import java.util.Map;

public class JavaMetricFactory {
    private static final Map<MetricEnum, AbstractJavaMetric> metricsMap = new HashMap<>();

    static {
        metricsMap.put(MetricEnum.CouplingBetweenObjects, new JavaCouplingBetweenObjects());
        metricsMap.put(MetricEnum.CyclomaticComplexity, new JavaCyclomaticComplexity());
        metricsMap.put(MetricEnum.NumberOfParametersPerMethod, new JavaExcessiveParameterList());
        metricsMap.put(MetricEnum.GodClassDetection, new JavaGodClass());
        metricsMap.put(MetricEnum.LinesOfCode, new JavaNcssCount());
        metricsMap.put(MetricEnum.MethodLength, new JavaCognitiveComplexity()); //Questionable???
    }

    public static AbstractJavaMetric getMetric(MetricEnum metricType) {
        return metricsMap.get(metricType);
    }
}
