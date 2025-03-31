package org.mining.util.gitmetrics;

import lombok.Getter;
import org.eclipse.jgit.lib.Repository;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.mining.util.sarifparser.JGitSarifParser;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Getter
public class GitMetricAnalyzerBuilder {
    private final List<GitMetricAnalyzer<?>> analyzers = new ArrayList<>();
    private final JSONObject result = new JSONObject();

    public void addMetric(GitMetricAnalyzer<?> analyzer) {
        analyzers.add(analyzer);
    }

    public void analyze(Repository repository) throws IOException, JSONException {
        JSONArray arr = new JSONArray();
        for (GitMetricAnalyzer<?> analyzer : analyzers) {
            analyzer.analyze(repository);
            arr.put(analyzer.returnJSONResult());
        }
        result.put("metrics", arr);
        writeResultsToFile(result);
    }

    private void writeResultsToFile(JSONObject res) throws IOException, JSONException {
        String resourcePath = Paths.get("src", "main", "resources", "analysis_results.json").toString();
        File file = new File(resourcePath);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(res.toString(4));
        }
        System.out.println("Analysis results written to: " + file.getAbsolutePath());
        JGitSarifParser.parseJGit();
    }
}

