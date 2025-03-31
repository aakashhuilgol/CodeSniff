package MetricAnalyzerTest;

import org.eclipse.jgit.merge.MergeConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mining.util.LanguageMetrics.MetricAnalyzer;
import org.mining.util.inputparser.CodeAnalysisConfig;
import org.mining.util.inputparser.ConfigParser;
import org.mining.util.inputparser.MetricEnum;
import org.mining.util.sarifparser.SarifMerger;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class MetricAnalyzerJavascriptTest {

    private MetricAnalyzer metricAnalyzer;
    private CodeAnalysisConfig codeAnalysisConfig;

    @BeforeEach
    public void setUp() throws IOException {
        InputStream configInputStream = getClass().getClassLoader().getResourceAsStream("propertiesJavascript.json");
        if (configInputStream == null) {
            throw new IllegalArgumentException("Config file not found!");
        }
        codeAnalysisConfig = ConfigParser.parseConfig(configInputStream);
        String currentDirectory = System.getProperty("user.dir");

        // Create the folder path
        File folder = new File(currentDirectory + File.separator + "src" + File.separator +
                "test" + File.separator + "resources" + File.separator + "TestJavascriptFiles");
        codeAnalysisConfig.setRepositoryPath(folder.getAbsolutePath());

        metricAnalyzer = new MetricAnalyzer();
    }

    @Test
    void testReportGeneration() throws IOException {
        metricAnalyzer.runMetrics(codeAnalysisConfig);
        File generatedFile = new File("src/main/resources/eslint_report.txt");
        assertTrue(generatedFile.exists(), "Generated ruleset file does not exist");
    }
    @Test
    void testSarifReportGeneration() throws IOException {
        metricAnalyzer.runMetrics(codeAnalysisConfig);
        File initgeneratedFile = new File("src/main/resources/eslint_sarif.sarif");
        assertTrue(initgeneratedFile.exists(), "Generated SARIF file does not exist");
        SarifMerger.mergeSarif();
        File generatedFile = new File("src/main/resources/Final.sarif");
        assertTrue(generatedFile.exists(), "Generated SARIF file does not exist");
    }
    @Test
    void testReportCorrect() throws IOException {
        metricAnalyzer.runMetrics(codeAnalysisConfig);
        File generatedFile = new File("src/main/resources/eslint_report.txt");
        String expectedLine = "\"ruleId\":\"custom-rules/depth-of-inheritance-tree\",\"severity\":1,"
                + "\"message\":\"Class \\\"Puppy\\\" in file \\\"/app/example.js\\\" has a Depth of Inheritance Tree (DIT) of 3, "
                + "which exceeds the threshold of 2.\"";


        assertTrue(fileHasLine(expectedLine, generatedFile), "The file does not contain the expected line.");
    }

    private boolean fileHasLine(String expectedLine, File generatedFile) throws IOException{
        // Variable to track if the line is found
        boolean lineFound = false;

        // Read the file line by line
        try (BufferedReader reader = new BufferedReader(new FileReader(generatedFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains(expectedLine)) {
                    lineFound = true;
                    break;
                }
            }
        }
        return lineFound;
    }

    @Test
    void testMaxParams() throws IOException {
        Map<MetricEnum, CodeAnalysisConfig.MetricConfig> map = new HashMap<>();
        CodeAnalysisConfig.MetricConfig maxParams = new CodeAnalysisConfig.MetricConfig();
        maxParams.setMaxParameters(1);
        map.put(MetricEnum.NumberOfParametersPerMethod, maxParams);
        codeAnalysisConfig.setMetrics(map);
        metricAnalyzer.runMetrics(codeAnalysisConfig);
        String expectedLine = "has too many parameters (4). Maximum allowed is 1.";
        File generatedFile = new File("src/main/resources/eslint_report.txt");
        assertTrue(fileHasLine(expectedLine, generatedFile));
    }

    @Test
    void testMaxLines() throws IOException {
        Map<MetricEnum, CodeAnalysisConfig.MetricConfig> map = new HashMap<>();
        CodeAnalysisConfig.MetricConfig maxLines = new CodeAnalysisConfig.MetricConfig();
        maxLines.setMaxMethodLength(2);
        map.put(MetricEnum.MethodLength, maxLines);
        codeAnalysisConfig.setMetrics(map);
        metricAnalyzer.runMetrics(codeAnalysisConfig);
        String expectedLine = "Method 'calculateMassIndex' has too many lines (3). Maximum allowed is 2.";
        File generatedFile = new File("src/main/resources/eslint_report.txt");
        assertTrue(fileHasLine(expectedLine, generatedFile));
    }

    @Test
    void testMaxLinesofCode() throws IOException {
        Map<MetricEnum, CodeAnalysisConfig.MetricConfig> map = new HashMap<>();
        CodeAnalysisConfig.MetricConfig maxLines = new CodeAnalysisConfig.MetricConfig();
        maxLines.setClassSizeThreshold(2);
        map.put(MetricEnum.LinesOfCode, maxLines);
        codeAnalysisConfig.setMetrics(map);
        metricAnalyzer.runMetrics(codeAnalysisConfig);
        String expectedLine = "File has too many lines (146). Maximum allowed is 2.";
        File generatedFile = new File("src/main/resources/eslint_report.txt");
        assertTrue(fileHasLine(expectedLine, generatedFile));
    }

    @Test
    void testMaxCoupling() throws IOException {
        Map<MetricEnum, CodeAnalysisConfig.MetricConfig> map = new HashMap<>();
        CodeAnalysisConfig.MetricConfig maxCoupling = new CodeAnalysisConfig.MetricConfig();
        maxCoupling.setMaxDependencies(2);
        map.put(MetricEnum.CouplingBetweenObjects, maxCoupling);
        codeAnalysisConfig.setMetrics(map);
        metricAnalyzer.runMetrics(codeAnalysisConfig);
        String expectedLine = "Maximum number of dependencies (2) exceeded.";
        File generatedFile = new File("src/main/resources/eslint_report.txt");
        assertTrue(fileHasLine(expectedLine, generatedFile));
    }

    @Test
    void testMaxCyclomatic() throws IOException {
        Map<MetricEnum, CodeAnalysisConfig.MetricConfig> map = new HashMap<>();
        CodeAnalysisConfig.MetricConfig maxCycle = new CodeAnalysisConfig.MetricConfig();
        maxCycle.setMaxCyclomaticComplexity(2);
        map.put(MetricEnum.CyclomaticComplexity, maxCycle);
        codeAnalysisConfig.setMetrics(map);
        metricAnalyzer.runMetrics(codeAnalysisConfig);
        String expectedLine = "Method 'excessivelyComplexMethod' has a complexity of 6. Maximum allowed is 2.";
        File generatedFile = new File("src/main/resources/eslint_report.txt");
        assertTrue(fileHasLine(expectedLine, generatedFile));
    }

    @Test
    void testMaxDOT() throws IOException {
        Map<MetricEnum, CodeAnalysisConfig.MetricConfig> map = new HashMap<>();
        CodeAnalysisConfig.MetricConfig maxDOT = new CodeAnalysisConfig.MetricConfig();
        maxDOT.setMaxInheritanceDepth(1);
        map.put(MetricEnum.DepthOfInheritanceTree, maxDOT);
        codeAnalysisConfig.setMetrics(map);
        metricAnalyzer.runMetrics(codeAnalysisConfig);
        String expectedLine = "has a Depth of Inheritance Tree (DIT) of 2, which exceeds the threshold of 1.";
        File generatedFile = new File("src/main/resources/eslint_report.txt");
        assertTrue(fileHasLine(expectedLine, generatedFile));
    }


}
