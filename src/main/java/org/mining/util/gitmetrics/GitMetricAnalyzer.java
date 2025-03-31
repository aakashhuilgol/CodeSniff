package org.mining.util.gitmetrics;

import org.eclipse.jgit.lib.Repository;
import org.json.JSONException;
import org.json.JSONObject;

public interface GitMetricAnalyzer<T> {
    void analyze(Repository repository);

    T returnResult();

    JSONObject returnJSONResult() throws JSONException;
}
