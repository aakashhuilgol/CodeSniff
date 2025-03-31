package org.mining.util.gitmetrics.metrics;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.treewalk.AbstractTreeIterator;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.mining.util.gitmetrics.GitMetricAnalyzer;
import org.mining.util.gitmetrics.JSONReflectUtil;
import org.mining.util.gitmetrics.metrics.churns.BaseChurn;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CodeChurn implements GitMetricAnalyzer<Map<String, Map<String, Integer>>> {

    private final Map<String, Map<String, Integer>> churnMap = new HashMap<>();
    private final Map<String, Map<String, Integer>> resultMap = new HashMap<>();
    private final int commitDepth;

    public CodeChurn(int commitDepth) {
        this.commitDepth = commitDepth;
    }

    @Override
    public void analyze(Repository repository) {
        try (Git git = new Git(repository)) {
            Iterable<RevCommit> commits = git.log().call();
            int count = 0;
            for (RevCommit commit : commits) {
                if (commitDepth > 0 && count >= commitDepth) {
                    break;
                }
                if (commit.getParentCount() > 0) {
                    RevCommit parent = commit.getParent(0);
                    analyzeCommitDiff(repository, parent, commit);
                }
                count++;
            }
            getResultMap();
        } catch (GitAPIException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void getResultMap() {
        for (Map.Entry<String, Map<String, Integer>> entry : churnMap.entrySet()) {
            if (!resultMap.containsKey(retrieveFileName(entry.getKey()))) {
                resultMap.put(retrieveFileName(entry.getKey()), entry.getValue());
            }
        }
    }

    private String retrieveFileName(String path) {
        String[] parts = path.split("/");
        return parts[parts.length - 1];
    }

    private void analyzeCommitDiff(Repository repository, RevCommit parent, RevCommit commit) throws IOException {
        try (DiffFormatter diffFormatter = new DiffFormatter(new ByteArrayOutputStream())) {
            diffFormatter.setRepository(repository);
            AbstractTreeIterator parentTreeParser = prepareTreeParser(repository, parent);
            AbstractTreeIterator commitTreeParser = prepareTreeParser(repository, commit);
            List<DiffEntry> diffs = diffFormatter.scan(parentTreeParser, commitTreeParser);
            for (DiffEntry entry : diffs) {
                String filePath = entry.getNewPath();
                BaseChurn.processFileChanges(repository, commit, filePath, churnMap, getFileExtension(filePath));
            }
        }
    }

    public String getFileExtension(String filePath) {
        int dotIndex = filePath.lastIndexOf('.');
        return (dotIndex != -1 && dotIndex != filePath.length() - 1)
                ? filePath.substring(dotIndex + 1)
                : "";
    }

    private AbstractTreeIterator prepareTreeParser(Repository repository, RevCommit commit) throws IOException {
        CanonicalTreeParser treeParser = new CanonicalTreeParser();
        treeParser.reset(repository.newObjectReader(), commit.getTree());
        return treeParser;
    }

    @Override
    public Map<String, Map<String, Integer>> returnResult() {
        return resultMap;
    }

    @Override
    public JSONObject returnJSONResult() throws JSONException {
        JSONObject result = new JSONObject();
        JSONArray filesArray = new JSONArray();
        for (String filePath : resultMap.keySet()) {
            JSONObject fileObject = new JSONObject();
            JSONReflectUtil.reflect(fileObject);
            fileObject.put("filePath", filePath).put("methods", new JSONArray());
            Map<String, Integer> methodChurnMap = resultMap.get(filePath);
            for (Map.Entry<String, Integer> entry : methodChurnMap.entrySet()) {
                String methodName = entry.getKey();
                int modificationCount = entry.getValue();
                fileObject.getJSONArray("methods").put(new JSONObject()
                        .put("methodName", methodName)
                        .put("modifications", modificationCount));
            }
            filesArray.put(fileObject);
        }
        result.put("files", filesArray);
        JSONObject ref = new JSONObject();
        JSONReflectUtil.reflect(ref);
        return ref.put("metricName", "Code Churn")
                .put("result", result);
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append("Code Churn Analysis:\n");
        for (String filePath : resultMap.keySet()) {
            result.append("File: ").append(filePath).append("\n");
            Map<String, Integer> methodChurnMap = resultMap.get(filePath);
            for (Map.Entry<String, Integer> entry : methodChurnMap.entrySet()) {
                String methodName = entry.getKey();
                int modificationCount = entry.getValue();
                result.append(String.format("  - %s: %d modifications\n", methodName, modificationCount));
            }
            result.append("---------------------------------------------------\n");
        }
        return result.toString();
    }
}