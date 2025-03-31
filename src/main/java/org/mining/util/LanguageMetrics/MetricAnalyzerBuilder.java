package org.mining.util.LanguageMetrics;

import org.mining.util.LanguageMetrics.LanguageAnalyzer.ILanguageMetricGenerator;
import org.mining.util.inputparser.CodeAnalysisConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MetricAnalyzerBuilder {
    private final List<ILanguageMetricGenerator> languageMetricGenerators = new ArrayList<>();

    public void addLanguageMetricGenerator(ILanguageMetricGenerator generator){
        languageMetricGenerators.add(generator);
    }
    /**
     * Analyzes the code by iterating through each registered language metric generator.
     * Executes the metric generation process in parallel.
     *
     * <p>This method uses an ExecutorService to execute each metric generation task in parallel.
     * Each language metric generator will run in its own thread concurrently, improving performance
     * when there are multiple language generators to process.</p>
     *
     * @param config The {@link CodeAnalysisConfig} containing the metrics settings to be used by each generator.
     *               This includes specific metrics configurations for each enabled metric.
     */
    public void analyze(CodeAnalysisConfig config){
        ExecutorService executorService = Executors.newFixedThreadPool(languageMetricGenerators.size());

        // Submit each metric generation task for parallel execution
        for (ILanguageMetricGenerator generator : languageMetricGenerators) {
            executorService.submit(() -> generator.generateAnalyzer(config.getMetrics()));
        }

        //shut down the executor after all tasks are complete
        executorService.shutdown();

        try {
            // Wait for all tasks to finish
            if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                executorService.shutdownNow(); // Force shutdown if tasks don't complete in time
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow(); // Interrupt if shutdown is interrupted
            Thread.currentThread().interrupt();
        }
    }
}
