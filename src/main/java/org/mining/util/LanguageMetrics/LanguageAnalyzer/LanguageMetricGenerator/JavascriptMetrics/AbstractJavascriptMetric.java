package org.mining.util.LanguageMetrics.LanguageAnalyzer.LanguageMetricGenerator.JavascriptMetrics;

import org.mining.util.LanguageMetrics.LanguageAnalyzer.LanguageMetricGenerator.IMetric;
import org.mining.util.inputparser.CodeAnalysisConfig;

public abstract class AbstractJavascriptMetric implements IMetric {
    public abstract void generateMetric(CodeAnalysisConfig.MetricConfig config, StringBuilder sbBuilder);
}
