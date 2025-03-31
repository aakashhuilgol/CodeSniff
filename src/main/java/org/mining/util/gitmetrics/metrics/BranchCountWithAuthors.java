package org.mining.util.gitmetrics.metrics;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ListBranchCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.mining.util.gitmetrics.GitMetricAnalyzer;
import org.mining.util.gitmetrics.JSONReflectUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.StreamSupport;

public class BranchCountWithAuthors implements GitMetricAnalyzer<Map<String, List<BranchCountWithAuthors.BranchDetail>>> {

    private final Map<String, List<BranchDetail>> authorBranchDetails = new HashMap<>();
    private int totalBranchCount = 0;

    @Override
    public void analyze(Repository repository) {
        try (Git git = new Git(repository)) {
            List<Ref> branches = git.branchList().setListMode(ListBranchCommand.ListMode.ALL).call();
            totalBranchCount = branches.size();
            for (Ref branch : branches) {
                String author = getBranchAuthor(repository, branch, git);
                if (author == null) {
                    author = "Unknown";
                }
                authorBranchDetails.putIfAbsent(author, new ArrayList<>());
                authorBranchDetails.get(author).add(new BranchDetail(branch.getName()));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String getBranchAuthor(Repository repository, Ref branch, Git git) throws IOException, GitAPIException {
        ObjectId branchId = repository.resolve(branch.getName());
        if (branchId == null) {
            return null;
        }
        Iterable<RevCommit> commits = git.log().add(branchId).call();
        for (RevCommit commit : commits) {
            return commit.getAuthorIdent().getName();
        }
        return null;
    }

    @Override
    public JSONObject returnJSONResult() throws JSONException {
        JSONObject result = new JSONObject();
        result.put("totalBranchCount", totalBranchCount);
        JSONArray authorsArray = new JSONArray();
        for (Map.Entry<String, List<BranchDetail>> entry : authorBranchDetails.entrySet()) {
            JSONObject authorObject = new JSONObject()
                    .put("author", entry.getKey())
                    .put("branches", new JSONArray());
            for (BranchDetail detail : entry.getValue()) {
                authorObject.getJSONArray("branches").put(new JSONObject()
                        .put("branchName", detail.branchName()));
            }
            authorsArray.put(authorObject);
        }
        result.put("authors", authorsArray);
        JSONObject ref = new JSONObject();
        JSONReflectUtil.reflect(ref);
        return ref.put("metricName", "Branch Count with Authors")
                .put("result", result);
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append("Branch Count with Authors:\n");
        result.append("Total Branches: ").append(totalBranchCount).append("\n");
        for (Map.Entry<String, List<BranchDetail>> entry : authorBranchDetails.entrySet()) {
            result.append("Author: ").append(entry.getKey()).append("\n");
            for (BranchDetail detail : entry.getValue()) {
                result.append("  - Branch: ").append(detail.branchName()).append("\n");
            }
        }
        result.append("---------------------------------------------\n");
        return result.toString();
    }

    @Override
    public Map<String, List<BranchDetail>> returnResult() {
        return authorBranchDetails;
    }

    public record BranchDetail(String branchName) {}
}
