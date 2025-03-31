package org.mining.util.LanguageMetrics.LanguageAnalyzer.LanguageMetricGenerator.JavascriptMetrics;

import org.mining.util.inputparser.CodeAnalysisConfig;

public class JavascriptExcessiveParameterList extends AbstractJavascriptMetric{
    @Override
    public void generateMetric(CodeAnalysisConfig.MetricConfig config, StringBuilder sbBuilder) {
        sbBuilder.append("      'max-params': ['warn', "+ config.getMaxParameters() +"],\n");
    }
}
