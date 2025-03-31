package org.mining.util.LanguageMetrics.LanguageAnalyzer.LanguageMetricGenerator.JavascriptMetrics;

import org.mining.util.inputparser.CodeAnalysisConfig;

public class JavascriptCouplingBetweenObjects extends AbstractJavascriptMetric{
    @Override
    public void generateMetric(CodeAnalysisConfig.MetricConfig config, StringBuilder sbBuilder) {
        //Coupling could be implemented alternatively, this is just a mock
        sbBuilder.append("      'import/no-cycle': 'warn',\n");
        sbBuilder.append("      'import/max-dependencies': ['warn', { max: " +
                config.getMaxDependencies() + " }],\n");
    }
}
