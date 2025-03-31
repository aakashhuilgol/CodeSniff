package org.mining.util.gitmetrics;

import org.mining.util.gitmetrics.metrics.*;

public class GitMetricFactory {
    public static GitMetricAnalyzer<?> getMetric(GitMetricEnum metric, int depth) {
        return switch (metric) {
            case CommitFrequency -> new CommitFrequency(depth);
            case CommitSize -> new CommitSize(depth);
            case CommitFixRevert -> new CommitFixRevert(depth);
            case CodeOwnershipByFile -> new CodeOwnershipByFile(depth);
            case BranchTime -> new BranchTime(depth);
            case CodeChurn -> new CodeChurn(depth);
            case BranchCountWithAuthors -> new BranchCountWithAuthors();
        };
    }
}
