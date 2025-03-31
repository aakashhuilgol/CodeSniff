package org.mining.util.LanguageMetrics.LanguageAnalyzer.LanguageMetricGenerator.JavaMetrics;

import org.mining.util.LanguageMetrics.LanguageAnalyzer.LanguageMetricGenerator.IMetric;
import org.mining.util.inputparser.CodeAnalysisConfig;

import java.nio.file.Path;

public abstract class AbstractJavaMetric implements IMetric {
    public abstract void generateMetric(CodeAnalysisConfig.MetricConfig config, StringBuilder xmlBuilder);
}
