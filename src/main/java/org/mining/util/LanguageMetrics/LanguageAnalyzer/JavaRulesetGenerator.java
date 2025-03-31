package org.mining.util.LanguageMetrics.LanguageAnalyzer;

import org.mining.util.LanguageMetrics.LanguageAnalyzer.LanguageMetricGenerator.JavaMetrics.AbstractJavaMetric;
import org.mining.util.LanguageMetrics.MetricFactories.JavaMetricFactory;
import org.mining.util.inputparser.CodeAnalysisConfig;
import org.mining.util.inputparser.MetricEnum;

import java.io.*;
import java.util.Map;
import java.util.Properties;

public class JavaRulesetGenerator implements ILanguageMetricGenerator {
    private static String configFilePath;

    static {
        try (InputStream input = new FileInputStream("src/main/config.properties")) {
            Properties prop = new Properties();
            prop.load(input);
            configFilePath = prop.getProperty("JavaPMD_ruleset.filepath");

            if (configFilePath == null || configFilePath.isEmpty()) {
                throw new IOException("JavaPMD_ruleset.filepath not found in config.properties.");
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    /**
     * Generates a custom PMD ruleset XML based on the provided metric configuration.
     * This method loops through the given metrics configuration and for each enabled metric,
     * it appends the corresponding rule to the XML content.
     *
     * <p>The generated XML structure follows the PMD ruleset format, including namespaces
     * and schema locations for validation. It also contains a description of the ruleset
     * and the specific rules for each metric defined in the configuration.</p>
     *
     * <p>The XML is built dynamically by iterating through the provided {@code config} map
     * that contains metrics and their respective configuration settings.</p>
     *
     * @param config A map where keys are metrics and values are their corresponding configuration settings.
     * @throws IllegalArgumentException if any required metric is missing or improperly configured.
     */
    @Override
    public void generateAnalyzer(Map<MetricEnum, CodeAnalysisConfig.MetricConfig> config) {
        StringBuilder xmlBuilder = new StringBuilder();
        xmlBuilder.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        xmlBuilder.append("<ruleset name=\"Custom Rules\"\n");
        xmlBuilder.append("         xmlns=\"http://pmd.sourceforge.net/ruleset/2.0.0\"\n");
        xmlBuilder.append("         xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n");
        xmlBuilder.append("         xsi:schemaLocation=\"http://pmd.sourceforge.net/ruleset/2.0.0 https://pmd.sourceforge.io/ruleset_2_0_0.xsd\">\n");
        xmlBuilder.append("    <description>\n");
        xmlBuilder.append("        This is a custom ruleset for PMD.\n");
        xmlBuilder.append("    </description>\n");

        IterateThroughMetrics(config, xmlBuilder);

        xmlBuilder.append("</ruleset>");
        generateMetricFile(xmlBuilder);

    }

    private void IterateThroughMetrics(Map<MetricEnum, CodeAnalysisConfig.MetricConfig> config, StringBuilder xmlBuilder ){
        for (Map.Entry<MetricEnum, CodeAnalysisConfig.MetricConfig> entry : config.entrySet()) {
            MetricEnum metricType = entry.getKey();
            CodeAnalysisConfig.MetricConfig metricConfig = entry.getValue();

            if (metricConfig.isEnabled()) {
                AbstractJavaMetric metric = JavaMetricFactory.getMetric(metricType);
                if (metric != null) {
                    metric.generateMetric(metricConfig, xmlBuilder);
                }
            }
        }
    }

    private void generateMetricFile(StringBuilder xmlBuilder) {
        try {
            File file = new File(configFilePath);

            if (file.exists()) {
                file.delete();
            }

            try (FileWriter writer = new FileWriter(file)) {
                writer.write(xmlBuilder.toString());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
