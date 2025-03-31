package org.mining.util.LanguageMetrics.LanguageParsingStartegies;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class LanguageStrategyRunner {
    private final List<ILanguageParserStrategy> parsingStrategies = new ArrayList<>();

    public void addParsingStrategy(ILanguageParserStrategy languageStrategy){
        parsingStrategies.add(languageStrategy);
    }

    /**
     * Executes all registered language parsing strategies for the specified source directory
     * in parallel.
     *
     * <p>This method uses an ExecutorService to execute each parsing strategy in its own thread
     * concurrently. The directory where source code is located is provided to each strategy for
     * processing.</p>
     *
     * @param sourceDir The directory path containing the source code files to be analyzed
     *                  by each parsing strategy.
     */
    public void execute(String sourceDir){
        // Create a thread pool with as many threads as there are parsing strategies
        ExecutorService executorService = Executors.newFixedThreadPool(parsingStrategies.size());

        // Submit each strategy for execution in parallel
        for (ILanguageParserStrategy strategy : parsingStrategies) {
            executorService.submit(() -> execute(strategy, sourceDir));
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
    /**
     * Executes a single language parsing strategy for the specified source directory.
     *
     * <p>This method delegates to the {@link ILanguageParserStrategy#execute(String)} method
     * of the given strategy, which performs parsing on the provided source directory.</p>
     *
     * @param languageStrategy The language parsing strategy to execute.
     * @param sourceDir        The directory path containing the source code files to be analyzed.
     */
    public void execute(ILanguageParserStrategy languageStrategy, String sourceDir){
        languageStrategy.execute(sourceDir);
    }
}
