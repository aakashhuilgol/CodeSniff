package org.mining.util.LanguageMetrics.LanguageAnalyzer.LanguageMetricGenerator.JavaMetrics;

import org.mining.util.inputparser.CodeAnalysisConfig;

import java.nio.file.Path;

public class JavaNcssCount extends AbstractJavaMetric{
    @Override
    public void generateMetric(CodeAnalysisConfig.MetricConfig config, StringBuilder xmlBuilder ) {
        Integer maxMethodLength = config.getMaxMethodLength();
        Integer classLengthThreshold = config.getClassSizeThreshold();

        xmlBuilder.append("    <rule ref=\"category/java/design.xml/NcssCount\">\n");
        if (maxMethodLength != null || classLengthThreshold != null) {
            xmlBuilder.append("        <properties>\n");

            if (maxMethodLength != null) {
                xmlBuilder.append("            <property name=\"methodReportLevel\" value=\"")
                        .append(maxMethodLength)
                        .append("\" />\n");
            }
            if (classLengthThreshold != null) {
                xmlBuilder.append("            <property name=\"classReportLevel\" value=\"")
                        .append(classLengthThreshold)
                        .append("\" />\n");
            }

            xmlBuilder.append("        </properties>\n");
        }
        xmlBuilder.append("    </rule>\n");
    }
}
