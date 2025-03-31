package org.mining.util.LanguageMetrics.LanguageAnalyzer.LanguageMetricGenerator.JavascriptMetrics;

import org.mining.util.inputparser.CodeAnalysisConfig;

public class JavascriptMethodLength extends AbstractJavascriptMetric{
    @Override
    public void generateMetric(CodeAnalysisConfig.MetricConfig config, StringBuilder sbBuilder) {
        sbBuilder.append("      'max-lines-per-function': ['warn', { 'max': "+ config.getMaxMethodLength()
                +", 'skipComments': true, 'skipBlankLines': true }],\n");
    }
}
