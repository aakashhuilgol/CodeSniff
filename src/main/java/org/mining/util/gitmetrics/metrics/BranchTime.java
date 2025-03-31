package org.mining.util.gitmetrics.metrics;

import org.eclipse.jgit.api.LogCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.json.JSONException;
import org.json.JSONObject;
import org.mining.util.gitmetrics.GitMetricAnalyzer;
import org.eclipse.jgit.api.ListBranchCommand;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import java.time.Duration;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.mining.util.gitmetrics.JSONReflectUtil;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class BranchTime implements GitMetricAnalyzer<List<Long>> {

    private final List<Long> branchLifetimes = new ArrayList<>();
    private final int commitDepth;

    public BranchTime(int commitDepth) {
        this.commitDepth = commitDepth;
    }

    @Override
    public void analyze(Repository repository) {
        try (Git git = new Git(repository)) {
            List<Ref> branches = git.branchList()
                    .setListMode(ListBranchCommand.ListMode.ALL)
                    .call();
            for (Ref branch : branches) {
                if (branch.getName().endsWith("main") || branch.getName().endsWith("master")) {
                    continue;
                }
                long branchLifetime = calculateBranchLifetime(repository, branch);
                if (branchLifetime > 0) {
                    branchLifetimes.add(branchLifetime);
                }
            }
        } catch (GitAPIException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Long> returnResult() {
        return branchLifetimes;
    }

    private long calculateBranchLifetime(Repository repository, Ref branch) throws IOException, GitAPIException {
        RevCommit branchCreationCommit = getFirstUniqueCommit(repository, branch);
        if (branchCreationCommit == null) {
            return -1;
        }

        RevCommit mergeCommit = getMergeCommit(repository, branch);
        if (mergeCommit == null) {
            return -1;
        }
        long branchCreationTime = branchCreationCommit.getCommitTime();
        long branchMergeTime = mergeCommit.getCommitTime();
        return branchMergeTime - branchCreationTime;
    }

    private RevCommit getFirstUniqueCommit(Repository repository, Ref branch) throws IOException, GitAPIException {
        try (Git git = new Git(repository)) {
            ObjectId branchId = repository.resolve(branch.getName());
            if (branchId == null) {
                return null;
            }

            LogCommand logCommand = git.log().add(branchId);
            if (commitDepth > 0) {
                logCommand.setMaxCount(commitDepth);
            }

            Iterable<RevCommit> commits = logCommand.call();
            for (RevCommit commit : commits) {
                return commit;
            }
        }
        return null;
    }

    private RevCommit getMergeCommit(Repository repository, Ref branch) throws IOException, GitAPIException {
        try (Git git = new Git(repository)) {
            ObjectId mainBranchId = repository.resolve("refs/heads/main");
            if (mainBranchId == null) {
                mainBranchId = repository.resolve("refs/heads/master");
            }
            if (mainBranchId == null) {
                return null;
            }

            LogCommand logCommand = git.log().add(mainBranchId);
            if (commitDepth > 0) {
                logCommand.setMaxCount(commitDepth);
            }

            Iterable<RevCommit> mainCommits = logCommand.call();
            for (RevCommit commit : mainCommits) {
                if (commit.getParentCount() > 1) {
                    String message = commit.getFullMessage();
                    if (message.contains(branch.getName()) ||
                            message.contains(branch.getName().substring(branch.getName().lastIndexOf("/") + 1))) {
                        return commit;
                    }
                }
            }
        }
        return null;
    }

    @Override
    public JSONObject returnJSONResult() throws JSONException {
        JSONObject ref = new JSONObject();
        JSONReflectUtil.reflect(ref);
        return ref.put("metricName", "Branch Time")
                .put("result", toString());
    }

    @Override
    public String toString() {
        if (branchLifetimes.isEmpty()) {
            return "No merged branches to calculate average branch time.";
        }
        long totalLifetime = branchLifetimes.stream().mapToLong(Long::longValue).sum();
        long averageLifetime = totalLifetime / branchLifetimes.size();
        Duration duration = Duration.ofSeconds(averageLifetime);
        long days = duration.toDays();
        long hours = duration.toHours() % 24;
        long minutes = duration.toMinutes() % 60;
        return String.format("Average Branch Lifetime: %d days, %d hours, %d minutes", days, hours, minutes);
    }
}

