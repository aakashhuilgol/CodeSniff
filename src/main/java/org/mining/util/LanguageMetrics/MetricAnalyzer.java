package org.mining.util.LanguageMetrics;

import org.mining.util.LanguageMetrics.LanguageAnalyzer.ILanguageMetricGenerator;
import org.mining.util.LanguageMetrics.LanguageFactory.LanguageFactory;
import org.mining.util.LanguageMetrics.LanguageFactory.LanguageProcessingComponents;
import org.mining.util.LanguageMetrics.LanguageParsingStartegies.LanguageStrategyRunner;
import org.mining.util.inputparser.CodeAnalysisConfig;
import org.mining.util.inputparser.SupportedLanguages;

import java.util.List;
import java.util.Map;

public class MetricAnalyzer {
    private final MetricAnalyzerBuilder metricBuilderAnalyzer;
    private final LanguageStrategyRunner languageStrategyRunner;

    public MetricAnalyzer() {
        this.metricBuilderAnalyzer = new MetricAnalyzerBuilder();
        this.languageStrategyRunner = new LanguageStrategyRunner();
    }
    /**
     * Runs the metrics analysis process for the specified configuration.
     *
     * <p>This method performs the following steps:</p>
     * <ul>
     *     <li>Detects enabled languages based on the {@link CodeAnalysisConfig}.</li>
     *     <li>Retrieves corresponding {@link LanguageProcessingComponents} for each enabled language from {@link LanguageFactory}.</li>
     *     <li>Adds each language's {@code ILanguageMetricGenerator} to the {@code metricBuilderAnalyzer} to prepare for metrics generation.</li>
     *     <li>Adds each language's {@code ILanguageParserStrategy} to the {@code languageStrategyRunner} for syntax parsing.</li>
     *     <li>Triggers metrics generation by calling {@code metricBuilderAnalyzer.analyze()}.</li>
     *     <li>Executes language parsing strategies through {@code languageStrategyRunner.execute()}.</li>
     * </ul>
     *
     * <p>Supported languages include Java, Python, and JavaScript, each identified in the
     * {@link SupportedLanguages} object.</p>
     *
     * @param config The {@link CodeAnalysisConfig} containing language settings and other
     *               configurations for metrics generation and analysis.
     */
    public void runMetrics(CodeAnalysisConfig config){
        Map<SupportedLanguages, CodeAnalysisConfig.LanguageConfig> languageSettings = config.getLanguageSpecificSettings();

        for (var lang : languageSettings.keySet()){
            if (languageSettings.get(lang).isEnabled()){
                LanguageProcessingComponents processingComponents = LanguageFactory
                        .getMetricGenerator(lang);
                if (processingComponents != null) {
                    metricBuilderAnalyzer.addLanguageMetricGenerator(processingComponents.metricGenerator());
                    languageStrategyRunner.addParsingStrategy(processingComponents.parserStrategy());

                }
            }
        }
        metricBuilderAnalyzer.analyze(config);
        languageStrategyRunner.execute(config.getRepositoryPath());

    }
}
