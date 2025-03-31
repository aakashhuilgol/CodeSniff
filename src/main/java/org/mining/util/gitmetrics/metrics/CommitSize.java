package org.mining.util.gitmetrics.metrics;

import org.antlr.v4.runtime.misc.Pair;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.diff.*;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.AbstractTreeIterator;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.eclipse.jgit.util.io.NullOutputStream;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.mining.util.gitmetrics.GitMetricAnalyzer;
import org.mining.util.gitmetrics.JSONReflectUtil;

import java.io.IOException;
import java.util.List;

public class CommitSize implements GitMetricAnalyzer<Pair<Integer, Integer>> {

    private int totalLinesAdded = 0;
    private int totalLinesDeleted = 0;
    private final int commitDepth;

    public CommitSize(int commitDepth) {
        this.commitDepth = commitDepth;
    }

    @Override
    public void analyze(Repository repository) {
        try (Git git = new Git(repository);
             RevWalk revWalk = new RevWalk(repository)) {
            Iterable<RevCommit> commits = git.log().call();
            int count = 0;
            for (RevCommit commit : commits) {
                if (commitDepth > 0 && count >= commitDepth) {
                    break;
                }
                // Skip merge commits
                if (commit.getParentCount() == 0) {
                    continue;
                }
                // Get the parent commit
                RevCommit parentCommit = revWalk.parseCommit(commit.getParent(0));
                //System.out.println("Commit: " + commit.getShortMessage());
                // Analyze the diff between the commit and its parent
                analyzeCommitDiff(repository, parentCommit, commit);
                count++;
            }
            //System.out.println("\nTotal Lines Added: " + totalLinesAdded);
            //System.out.println("Total Lines Deleted: " + totalLinesDeleted);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void analyzeCommitDiff(Repository repository, RevCommit parent, RevCommit commit) throws IOException {
        try (DiffFormatter diffFormatter = new DiffFormatter(NullOutputStream.INSTANCE)) {
            diffFormatter.setRepository(repository);
            diffFormatter.setDetectRenames(true);
            diffFormatter.setDiffAlgorithm(DiffAlgorithm.getAlgorithm(DiffAlgorithm.SupportedAlgorithm.HISTOGRAM));
            diffFormatter.setDiffComparator(RawTextComparator.WS_IGNORE_ALL);

            AbstractTreeIterator parentTreeParser = prepareTreeParser(repository, parent);
            AbstractTreeIterator commitTreeParser = prepareTreeParser(repository, commit);

            List<DiffEntry> diffs = diffFormatter.scan(parentTreeParser, commitTreeParser);

            for (DiffEntry entry : diffs) {
                // Print file path of the change
                //System.out.println("File: " + entry.getNewPath());

                // Get the list of edits (insertions, deletions, modifications)
                EditList edits = diffFormatter.toFileHeader(entry).toEditList();

                for (Edit edit : edits) {
                    switch (edit.getType()) {
                        case INSERT:
                            int linesAdded = edit.getEndB() - edit.getBeginB();
                            totalLinesAdded += linesAdded;
                            //System.out.println("  + Added lines: " + linesAdded);
                            break;
                        case DELETE:
                            int linesDeleted = edit.getEndA() - edit.getBeginA();
                            totalLinesDeleted += linesDeleted;
                            //System.out.println("  - Deleted lines: " + linesDeleted);
                            break;
                        case REPLACE:
                            int replaceAdded = edit.getEndB() - edit.getBeginB();
                            int replaceDeleted = edit.getEndA() - edit.getBeginA();
                            totalLinesAdded += replaceAdded;
                            totalLinesDeleted += replaceDeleted;
                            //System.out.println("  ~ Replaced lines: Added " + replaceAdded + ", Deleted " + replaceDeleted);
                            break;
                        default:
                            break;
                    }
                }
            }
        }
    }

    private AbstractTreeIterator prepareTreeParser(Repository repository, RevCommit commit) throws IOException {
        try (ObjectReader reader = repository.newObjectReader()) {
            CanonicalTreeParser treeParser = new CanonicalTreeParser();
            treeParser.reset(reader, commit.getTree());
            return treeParser;
        }
    }

    @Override
    public Pair<Integer, Integer> returnResult() {
        return new Pair<>(totalLinesAdded, totalLinesDeleted);
    }

    @Override
    public JSONObject returnJSONResult() throws JSONException {
        JSONObject ref = new JSONObject();
        JSONReflectUtil.reflect(ref);
        return ref.put("metricName", "Commit Size")
                .put("result", new JSONArray()
                        .put(new JSONObject().put("totalLinesAdded", totalLinesAdded))
                        .put(new JSONObject().put("totalLinesDeleted", totalLinesDeleted))
                );
    }

    @Override
    public String toString() {
        return "Commit Size Metric" + "\n" +
                "Total Lines Added: " + totalLinesAdded + "\n" +
                "Total Lines Deleted: " + totalLinesDeleted + "\n" +
                "---------------------------------------------\n";
    }
}
