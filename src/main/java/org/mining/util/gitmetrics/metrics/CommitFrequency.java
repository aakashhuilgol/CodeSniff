package org.mining.util.gitmetrics.metrics;

import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.mining.util.gitmetrics.GitMetricAnalyzer;
import org.mining.util.gitmetrics.JSONReflectUtil;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Map;
import java.util.TreeMap;

public class CommitFrequency implements GitMetricAnalyzer<Map<LocalDate, Integer>> {

    private final Map<LocalDate, Integer> commitFrequency = new TreeMap<>();
    private final int commitDepth;

    public CommitFrequency(int commitDepth) {
        this.commitDepth = commitDepth;
    }

    @Override
    public void analyze(Repository repository) {
        try (RevWalk revWalk = new RevWalk(repository)) {
            // Resolve the head (main/master branch) to the latest commit
            revWalk.markStart(revWalk.parseCommit(repository.resolve("refs/heads/main")));
            int count = 0;
            for (RevCommit commit : revWalk) {
                if (commitDepth > 0 && count >= commitDepth) {
                    break;
                }
                LocalDate commitDate = Instant.ofEpochSecond(commit.getCommitTime())
                        .atZone(ZoneId.systemDefault()).toLocalDate();
                commitFrequency.put(commitDate, commitFrequency.getOrDefault(commitDate, 0) + 1);
                count++;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public JSONObject returnJSONResult() throws JSONException {
        JSONArray arr = new JSONArray();
        for (Map.Entry<LocalDate, Integer> entry : commitFrequency.entrySet()) {
            arr.put(new JSONObject()
                    .put("date", entry.getKey().toString())
                    .put("commits", entry.getValue()));
        }
        JSONObject ref = new JSONObject();
        JSONReflectUtil.reflect(ref);
        return ref.put("metricName", "Commit Frequency")
                .put("result", arr);
    }

    @Override
    public String toString() {
        StringBuilder res = new StringBuilder();
        res.append("Commit Frequency Metric").append("\n");
        for (Map.Entry<LocalDate, Integer> entry : commitFrequency.entrySet()) {
            res.append(entry.getKey()).append(": ").append(entry.getValue()).append(" commits").append("\n");
        }
        res.append("---------------------------------------------\n");
        return res.toString();
    }

    @Override
    public Map<LocalDate, Integer> returnResult() {
        return commitFrequency;
    }
}

