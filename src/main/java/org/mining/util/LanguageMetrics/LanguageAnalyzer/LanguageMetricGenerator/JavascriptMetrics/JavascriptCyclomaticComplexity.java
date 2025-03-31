package org.mining.util.LanguageMetrics.LanguageAnalyzer.LanguageMetricGenerator.JavascriptMetrics;

import org.mining.util.inputparser.CodeAnalysisConfig;

public class JavascriptCyclomaticComplexity extends AbstractJavascriptMetric{

    @Override
    public void generateMetric(CodeAnalysisConfig.MetricConfig config, StringBuilder sbBuilder) {
        sbBuilder.append("      'complexity': ['warn', " + config.getMaxCyclomaticComplexity() + "],\n");
    }
}
