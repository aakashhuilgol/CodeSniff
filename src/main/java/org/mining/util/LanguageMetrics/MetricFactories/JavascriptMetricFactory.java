package org.mining.util.LanguageMetrics.MetricFactories;

import org.mining.util.LanguageMetrics.LanguageAnalyzer.LanguageMetricGenerator.JavaMetrics.*;
import org.mining.util.LanguageMetrics.LanguageAnalyzer.LanguageMetricGenerator.JavascriptMetrics.*;
import org.mining.util.inputparser.MetricEnum;

import java.util.HashMap;
import java.util.Map;

public class JavascriptMetricFactory {
    private static final Map<MetricEnum, AbstractJavascriptMetric> metricsMap = new HashMap<>();

    static {
        metricsMap.put(MetricEnum.CouplingBetweenObjects, new JavascriptCouplingBetweenObjects());
        metricsMap.put(MetricEnum.CyclomaticComplexity, new JavascriptCyclomaticComplexity());
        metricsMap.put(MetricEnum.NumberOfParametersPerMethod, new JavascriptExcessiveParameterList());
        //metricsMap.put(MetricEnum.GodClassDetection, new JavascriptGodClass());
        metricsMap.put(MetricEnum.LinesOfCode, new JavascriptLinesOfCode());
        metricsMap.put(MetricEnum.MethodLength, new JavascriptMethodLength());
        metricsMap.put(MetricEnum.DepthOfInheritanceTree, new JavascriptDepthOfInheritanceTree());
    }

    public static AbstractJavascriptMetric getMetric(MetricEnum metricType) {
        return metricsMap.get(metricType);
    }
}
