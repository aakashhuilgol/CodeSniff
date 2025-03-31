package org.mining.util.LanguageMetrics.LanguageAnalyzer.LanguageMetricGenerator.JavascriptMetrics;

import org.mining.util.inputparser.CodeAnalysisConfig;

public class JavascriptDepthOfInheritanceTree extends AbstractJavascriptMetric{

    @Override
    public void generateMetric(CodeAnalysisConfig.MetricConfig config, StringBuilder sbBuilder) {
        sbBuilder.append("      'custom-rules/depth-of-inheritance-tree': ['warn', " +
                config.getMaxInheritanceDepth() + "],");
    }
}
