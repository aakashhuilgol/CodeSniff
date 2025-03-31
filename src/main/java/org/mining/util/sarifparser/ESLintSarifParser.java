package org.mining.util.sarifparser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.File;
import java.io.IOException;
public class ESLintSarifParser {
    public static void parseESLint() {
        String inputJsonFilePath = "src/main/resources/eslint_report.txt";
        String outputSarifFilePath = "src/main/resources/eslint_sarif.sarif";

        try {
            ObjectMapper objectMapper = new ObjectMapper();

            JsonNode inputJson =  objectMapper.readTree(new File(inputJsonFilePath));

            ObjectNode sarifNode = objectMapper.createObjectNode();
            sarifNode.put("version", "2.1.0");
            sarifNode.put("$schema", "https://json.schemastore.org/sarif-2.1.0");


            ArrayNode runsArray = objectMapper.createArrayNode();
            ObjectNode toolNode = objectMapper.createObjectNode();
            ObjectNode driverNode = objectMapper.createObjectNode();
            driverNode.put("name","ESLint");
            driverNode.put("version", "9.17.0");
            driverNode.put("informationUri", "https://eslint.org/");
            toolNode.set("driver", driverNode);

            ObjectNode runNode = objectMapper.createObjectNode();
            runNode.set("tool",toolNode);
            ArrayNode resultsArray = objectMapper.createArrayNode();

            for (JsonNode fileNode : inputJson) {
                String filePath = fileNode.path("filePath").asText();
                for (JsonNode messageNode : fileNode.path("messages")) {
                    ObjectNode sarifResultNode = objectMapper.createObjectNode();

                    sarifResultNode.put("ruleId", messageNode.path("ruleId").asText("unknown-rule"));
                    ObjectNode messageText = objectMapper.createObjectNode();
                    messageText.put("text", messageNode.path("message").asText("No message provided."));
                    sarifResultNode.set("message", messageText);

                    String severity = mapSeverity(messageNode.path("severity").asInt());
                    sarifResultNode.put("level", severity);

                    ObjectNode locationNode = objectMapper.createObjectNode();
                    ObjectNode physicalLocation = objectMapper.createObjectNode();
                    ObjectNode artifactLocation = objectMapper.createObjectNode();
                    artifactLocation.put("uri", filePath.startsWith("/") ? filePath.substring(1) : filePath);
                    physicalLocation.set("artifactLocation", artifactLocation);

                    ObjectNode regionNode = objectMapper.createObjectNode();
                    regionNode.put("startLine", messageNode.path("line").asInt());
                    regionNode.put("startColumn", messageNode.path("column").asInt());
                    regionNode.put("endLine", messageNode.path("endLine").asInt());
                    regionNode.put("endColumn", messageNode.path("endColumn").asInt());
                    physicalLocation.set("region", regionNode);
                    locationNode.set("physicalLocation", physicalLocation);

                    ArrayNode locationsArray = objectMapper.createArrayNode();
                    locationsArray.add(locationNode);
                    sarifResultNode.set("locations", locationsArray);

                    resultsArray.add(sarifResultNode);
                }
            }

            runNode.set("results", resultsArray);

            runsArray.add(runNode);

            sarifNode.set("runs", runsArray);

            objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(outputSarifFilePath), sarifNode);

            System.out.println("SARIF file created successfully!");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error during conversion: " + e.getMessage());
        }
    }

    // Map ESLint severity to SARIF severity levels
    private static String mapSeverity(int severity) {
        return switch (severity) {
            case 1 -> "warning";
            case 2 -> "error";
            default -> "note";
        };
    }

}
