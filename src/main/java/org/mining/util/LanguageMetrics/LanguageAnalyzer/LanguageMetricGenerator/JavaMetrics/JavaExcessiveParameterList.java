package org.mining.util.LanguageMetrics.LanguageAnalyzer.LanguageMetricGenerator.JavaMetrics;

import org.mining.util.inputparser.CodeAnalysisConfig;

import java.nio.file.Path;

public class JavaExcessiveParameterList extends AbstractJavaMetric{
    @Override
    public void generateMetric(CodeAnalysisConfig.MetricConfig config, StringBuilder xmlBuilder ) {
        Integer maxParameters = config.getMaxParameters();

        xmlBuilder.append("    <rule ref=\"category/java/design.xml/ExcessiveParameterList\">\n");
        xmlBuilder.append("        <properties>\n");

        if (maxParameters != null) {
            xmlBuilder.append("            <property name=\"minimum\" value=\"")
                    .append(maxParameters)
                    .append("\" />\n");
        }

        xmlBuilder.append("        </properties>\n");
        xmlBuilder.append("    </rule>\n");
    }
}
