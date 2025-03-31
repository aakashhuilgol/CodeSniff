package org.mining.util.LanguageMetrics.LanguageAnalyzer;

import org.mining.util.LanguageMetrics.LanguageAnalyzer.LanguageMetricGenerator.JavaMetrics.AbstractJavaMetric;
import org.mining.util.LanguageMetrics.LanguageAnalyzer.LanguageMetricGenerator.JavascriptMetrics.AbstractJavascriptMetric;
import org.mining.util.LanguageMetrics.MetricFactories.JavaMetricFactory;
import org.mining.util.LanguageMetrics.MetricFactories.JavascriptMetricFactory;
import org.mining.util.inputparser.CodeAnalysisConfig;
import org.mining.util.inputparser.MetricEnum;

import java.io.*;
import java.util.Map;
import java.util.Properties;

public class JavascriptRulesetGenerator implements ILanguageMetricGenerator{
    private static String configFilePath;

    static {
        try (InputStream input = new FileInputStream("src/main/config.properties")) {
            Properties prop = new Properties();
            prop.load(input);
            configFilePath = prop.getProperty("ESlint_ruleset.filepath");

            if (configFilePath == null || configFilePath.isEmpty()) {
                throw new IOException("ESlint_ruleset.filepath not found in config.properties.");
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void generateAnalyzer(Map<MetricEnum, CodeAnalysisConfig.MetricConfig> config) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("const importPlugin = require('eslint-plugin-import');\n");
        stringBuilder.append("const depthOfInheritanceTreeRule = require('./depth-of-inheritance-tree');\n");
        stringBuilder.append("const sonarjsPlugin = require('eslint-plugin-sonarjs');\n\n");
        stringBuilder.append("module.exports = [\n");
        stringBuilder.append("  {\n");
        stringBuilder.append("    files: ['**/*.js'],\n");
        stringBuilder.append("    plugins: {\n");
        stringBuilder.append("      import: importPlugin,\n");
        stringBuilder.append("      sonarjs: sonarjsPlugin,\n");
        stringBuilder.append("      'custom-rules': { rules: { 'depth-of-inheritance-tree': depthOfInheritanceTreeRule } },\n");
        stringBuilder.append("    },\n");
        stringBuilder.append("    rules: {\n");
        stringBuilder.append("      'no-unused-vars': 'warn',\n");
        stringBuilder.append("      'no-console': 'off',\n");

        iterateThroughMetrics(config, stringBuilder);

        stringBuilder.append("    },\n");
        stringBuilder.append("  },\n");
        stringBuilder.append("];\n");
        generateMetricFile(stringBuilder);
    }

    private void iterateThroughMetrics(Map<MetricEnum, CodeAnalysisConfig.MetricConfig> config, StringBuilder sbBuilder ){
        for (Map.Entry<MetricEnum, CodeAnalysisConfig.MetricConfig> entry : config.entrySet()) {
            MetricEnum metricType = entry.getKey();
            CodeAnalysisConfig.MetricConfig metricConfig = entry.getValue();

            if (metricConfig.isEnabled()) {
                AbstractJavascriptMetric metric = JavascriptMetricFactory.getMetric(metricType);
                if (metric != null) {
                    metric.generateMetric(metricConfig, sbBuilder);
                }
            }
        }
    }

    private void generateMetricFile(StringBuilder sbBuilder) {
        try {
            File file = new File(configFilePath);

            if (file.exists()) {
                file.delete();
            }

            try (FileWriter writer = new FileWriter(file)) {
                writer.write(sbBuilder.toString());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
