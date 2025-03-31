package org.mining.util.LanguageMetrics.LanguageAnalyzer.LanguageMetricGenerator.JavascriptMetrics;

import org.mining.util.inputparser.CodeAnalysisConfig;

public class JavascriptLinesOfCode extends AbstractJavascriptMetric{
    @Override
    public void generateMetric(CodeAnalysisConfig.MetricConfig config, StringBuilder sbBuilder) {
        sbBuilder.append("      'max-lines': ['warn', { 'max': " + config.getClassSizeThreshold() +
                ", 'skipComments': true }],\n");
    }
}
