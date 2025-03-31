package org.mining.util.LanguageMetrics.LanguageAnalyzer.LanguageMetricGenerator.JavaMetrics;

import org.mining.util.inputparser.CodeAnalysisConfig;

public class JavaCyclomaticComplexity extends AbstractJavaMetric {

    @Override
    public void generateMetric(CodeAnalysisConfig.MetricConfig config, StringBuilder xmlBuilder ) {
        xmlBuilder.append("    <rule ref=\"category/java/design.xml/CyclomaticComplexity\">");
        xmlBuilder.append("        <properties>\n");
        xmlBuilder.append("            <property name=\"methodReportLevel\" value=\"")
                .append(config.getMaxCyclomaticComplexity()) // Retrieve max cyclomatic complexity from config
                .append("\"/>\n");
        xmlBuilder.append("        </properties>\n");
        xmlBuilder.append("    </rule>\n");
    }
}
