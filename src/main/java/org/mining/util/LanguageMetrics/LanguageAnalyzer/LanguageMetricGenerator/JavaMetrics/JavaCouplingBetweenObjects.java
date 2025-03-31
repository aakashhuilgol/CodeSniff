package org.mining.util.LanguageMetrics.LanguageAnalyzer.LanguageMetricGenerator.JavaMetrics;

import org.mining.util.inputparser.CodeAnalysisConfig;

import java.nio.file.Path;

public class JavaCouplingBetweenObjects extends AbstractJavaMetric{
    @Override
    public void generateMetric(CodeAnalysisConfig.MetricConfig config, StringBuilder xmlBuilder ) {
        xmlBuilder.append("    <rule ref=\"category/java/design.xml/CouplingBetweenObjects\">");
        xmlBuilder.append("        <properties>\n");
        xmlBuilder.append("            <property name=\"threshold\" value=\"")
                .append(config.getMaxDependencies()) // Retrieve max cyclomatic complexity from config
                .append("\"/>\n");
        xmlBuilder.append("        </properties>\n");
        xmlBuilder.append("    </rule>\n");
    }
}
