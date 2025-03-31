package org.mining;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Repository;
import org.mining.util.LanguageMetrics.MetricAnalyzer;
import org.mining.util.gitmetrics.GitMetricAnalyzer;
import org.mining.util.gitmetrics.GitMetricAnalyzerBuilder;
import org.mining.util.gitmetrics.GitMetricEnum;
import org.mining.util.gitmetrics.GitMetricFactory;
import org.mining.util.inputparser.CodeAnalysisConfig;
import org.mining.util.inputparser.ConfigParser;
import org.mining.util.sarifparser.JGitSarifParser;
import org.mining.util.sarifparser.SarifMerger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public class Analyzer {

    static CodeAnalysisConfig codeAnalysisConfig;

    public static void main(String[] args) throws Exception {
        getConfig(args);
        Git git = Git.open(new File(codeAnalysisConfig.getRepositoryPath()));
        Repository repository = git.getRepository();

        // Build metric analysis chain
        GitMetricAnalyzerBuilder builder = new GitMetricAnalyzerBuilder();
        for (Map.Entry<GitMetricEnum, CodeAnalysisConfig.MetricConfig> entry : codeAnalysisConfig.getGitMetrics().entrySet()) {
            GitMetricAnalyzer<?> metric = GitMetricFactory.getMetric(entry.getKey(), entry.getValue().getCommitDepth());
            if (metric != null) {
                builder.addMetric(metric);
            }
        }

        MetricAnalyzer metricAnalyzer = new MetricAnalyzer();
        metricAnalyzer.runMetrics(codeAnalysisConfig);

        // Analyze metrics
        builder.analyze(repository);

        // Cleanup
        SarifMerger.mergeSarif();
        git.getRepository().close();
    }

    static void deleteDirectory(File directory) {
        if (directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    deleteDirectory(file);
                }
            }
        }
        boolean isDeleted = directory.delete();
        if (!isDeleted) {
            System.err.println("Failed to delete: " + directory.getAbsolutePath());
        }
    }

    static void getConfig(String[] args) throws IOException {
        InputStream input;
        if (args != null && args.length > 0 && args[0] != null && !args[0].isEmpty()) {
            File configFile = new File(args[0]);
            if (!configFile.exists() || !configFile.isFile()) {
                throw new IllegalArgumentException("Config file not found at: " + args[0]);
            }
            input = new FileInputStream(configFile);
        } else {
            input = Analyzer.class.getClassLoader().getResourceAsStream("properties.json");
            if (input == null) {
                throw new IllegalArgumentException("Default config file not found! properties.json");
            }
        }

        codeAnalysisConfig = ConfigParser.parseConfig(input);
        input.close();
    }
}
