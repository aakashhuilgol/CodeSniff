package org.mining.util.gitmetrics.metrics.churns;

import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectLoader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.treewalk.filter.PathFilter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BaseChurn {

    public static void processFileChanges(Repository repository, RevCommit commit, String filePath, Map<String, Map<String, Integer>> churnMap, String lang) throws IOException {
        String fileContent = BaseChurn.readFileFromCommit(repository, commit, filePath);
        if (fileContent == null) return;
        List<MethodRange> methods;

        switch (lang) {
            case "java" -> methods = JavaChurn.parseMethods(fileContent);
            case "py" -> methods = PythonChurn.parseMethods(fileContent);
            case "js" -> methods = JavaScriptChurn.parseMethods(fileContent);
            default -> methods = null;
        }

        if (methods != null) {
            Map<String, Integer> methodChanges = churnMap.computeIfAbsent(filePath, k -> new HashMap<>());
            for (MethodRange method : methods) {
                methodChanges.put(method.name, methodChanges.getOrDefault(method.name, 0) + 1);
            }
        }
    }

    public static String readFileFromCommit(Repository repository, RevCommit commit, String filePath) throws IOException {
        try (TreeWalk treeWalk = new TreeWalk(repository)) {
            treeWalk.addTree(commit.getTree());
            treeWalk.setRecursive(true);
            treeWalk.setFilter(PathFilter.create(filePath));
            if (!treeWalk.next()) {
                return null;
            }
            ObjectId objectId = treeWalk.getObjectId(0);
            ObjectLoader loader = repository.open(objectId);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            loader.copyTo(outputStream);
            return outputStream.toString();
        }
    }

    public record MethodRange(String name, int lineNumber, String methodBody) {}
}
