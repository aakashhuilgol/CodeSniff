package org.mining.util.sarifparser;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.util.*;

public class JGitSarifParser {
    private static final String inputPath = "src/main/resources/analysis_results.json";
    private static final String outputPath = "src/main/resources/jgit_sarif.sarif";

    public static class AnalysisResults {
        public List<Metric> metrics;

        public static class Metric {
            public String metricName;
            public Object result;
        }
    }

    // SARIF Model
    public static class SarifOutput {
        public String $schema;
        public String version;
        public List<Run> runs;

        public static class Run {
            public Tool tool;
            public List<Result> results;
            public List<Artifact> artifacts; // Include only when necessary
            public Map<String, Object> properties;

            public static class Tool {
                public Driver driver;
            }

            public static class Driver {
                public String name;
                public String informationUri;
                public String version;
                public String semanticVersion;
                public String organization;
            }

            public static class Artifact {
                public ArtifactLocation location;

                public static class ArtifactLocation {
                    public String uri;
                    public String uriBaseId;
                }
            }
        }

        public static class Result {
            public String ruleId;
            public Message message;
            public Map<String, Object> properties;
            public List<Location> locations;

            public static class Message {
                public String text;
            }

            public static class Location {
                public PhysicalLocation physicalLocation;

                public static class PhysicalLocation {
                    public ArtifactLocation artifactLocation;
                    public Region region;

                    public static class ArtifactLocation {
                        public String uri;
                        public String uriBaseId;
                        public int index; // Index must refer to the artifacts array
                    }

                    public static class Region {
                        public int startLine;
                        public int startColumn;
                    }
                }
            }
        }
    }

    public static class SarifConverter {
        /**
         * Converts analysis results to SARIF format.
         *
         * @param analysisResults the analysis results to convert
         * @param retainArtifacts flag to decide if artifacts should always be retained
         * @return a SarifOutput object
         */
        public static SarifOutput convertToSarif(AnalysisResults analysisResults, boolean retainArtifacts) {
            SarifOutput sarif = new SarifOutput();
            sarif.$schema = "https://json.schemastore.org/sarif-2.1.0.json";
            sarif.version = "2.1.0";
            SarifOutput.Run run = new SarifOutput.Run();
            run.tool = new SarifOutput.Run.Tool();
            run.tool.driver = new SarifOutput.Run.Driver();
            run.tool.driver.name = "JGit";
            run.tool.driver.informationUri = "https://www.eclipse.org/jgit/";
            run.tool.driver.version = "7.0.0";
            run.tool.driver.semanticVersion = "7.0.0";
            run.tool.driver.organization = "Eclipse Foundation";
            run.results = new ArrayList<>();
            run.properties = new HashMap<>();

            // Track unique artifacts
            Map<String, Integer> artifactIndices = new HashMap<>();
            List<SarifOutput.Run.Artifact> artifacts = new ArrayList<>();
            int artifactIndex = 0;

            for (AnalysisResults.Metric metric : analysisResults.metrics) {
                SarifOutput.Result result = new SarifOutput.Result();
                result.ruleId = metric.metricName;
                result.message = new SarifOutput.Result.Message();
                result.message.text = "See properties for detailed information.";
                result.properties = extractProperties(metric.result);

                String uri = "dummy/file/path"; // Example URI
                if (!artifactIndices.containsKey(uri)) {
                    SarifOutput.Run.Artifact artifact = new SarifOutput.Run.Artifact();
                    artifact.location = new SarifOutput.Run.Artifact.ArtifactLocation();
                    artifact.location.uri = uri;
                    artifact.location.uriBaseId = "SRCROOT";
                    artifacts.add(artifact);
                    artifactIndices.put(uri, artifactIndex++);
                }

                SarifOutput.Result.Location location = new SarifOutput.Result.Location();
                location.physicalLocation = new SarifOutput.Result.Location.PhysicalLocation();
                location.physicalLocation.artifactLocation = new SarifOutput.Result.Location.PhysicalLocation.ArtifactLocation();
                location.physicalLocation.artifactLocation.uri = uri;
                location.physicalLocation.artifactLocation.uriBaseId = "SRCROOT";
                location.physicalLocation.artifactLocation.index = artifactIndices.get(uri);
                location.physicalLocation.region = new SarifOutput.Result.Location.PhysicalLocation.Region();
                location.physicalLocation.region.startLine = 1;
                location.physicalLocation.region.startColumn = 1;
                result.locations = List.of(location);

                run.results.add(result);
            }

            // Include artifacts array only if retainArtifacts is true or artifacts have meaningful information
            if (retainArtifacts || artifacts.stream().anyMatch(a -> hasAdditionalArtifactInfo(a))) {
                run.artifacts = artifacts;
            }

            sarif.runs = List.of(run);
            return sarif;
        }

        private static boolean hasAdditionalArtifactInfo(SarifOutput.Run.Artifact artifact) {
            // Check if artifact contains more information than just location
            return artifact.location != null && artifact.location.uri != null && !artifact.location.uri.isEmpty();
        }

        private static Map<String, Object> extractProperties(Object result) {
            Map<String, Object> properties = new HashMap<>();
            if (result instanceof List) {
                List<?> list = (List<?>) result;
                for (int i = 0; i < list.size(); i++) {
                    properties.put(String.valueOf(i), list.get(i));
                }
            } else if (result instanceof Map) {
                Map<?, ?> map = (Map<?, ?>) result;
                for (Map.Entry<?, ?> entry : map.entrySet()) {
                    properties.put(entry.getKey().toString(), entry.getValue());
                }
            } else if (result instanceof String) {
                properties.put("details", result);
            }
            return properties;
        }
    }

    public static void parseJGit() {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            AnalysisResults analysisResults = objectMapper.readValue(new File(inputPath), AnalysisResults.class);

            // Set retainArtifacts to omit artifacts if they contain only locations
            SarifOutput sarifOutput = SarifConverter.convertToSarif(analysisResults, false);

            objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(outputPath), sarifOutput);
            System.out.println("JGit SARIF file created successfully!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
