package MetricAnalyzerTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mining.util.LanguageMetrics.MetricAnalyzer;
import org.mining.util.inputparser.CodeAnalysisConfig;
import org.mining.util.inputparser.ConfigParser;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;
public class MetricAnalyzerTest {
    private static final String EXPECTED_RULESET_FILE_PATH = "src/test/resources/javaPMDRuleset_pattern.xml";
    private static final String GENERATED_RULESET_FILE_PATH = "src/main/resources/javaPMDRuleset.xml";

    private MetricAnalyzer metricAnalyzer;
    private CodeAnalysisConfig codeAnalysisConfig;

    @BeforeEach
    public void setUp() throws IOException {
        InputStream configInputStream = getClass().getClassLoader().getResourceAsStream("pmdTestProperties.json");
        if (configInputStream == null) {
            throw new IllegalArgumentException("Config file not found!");
        }
        codeAnalysisConfig = ConfigParser.parseConfig(configInputStream);

        metricAnalyzer = new MetricAnalyzer();
    }

    @Test
    void testGeneratedRulesetFile() throws IOException {
        metricAnalyzer.runMetrics(codeAnalysisConfig);

        File generatedFile = new File(GENERATED_RULESET_FILE_PATH);
        assertTrue(generatedFile.exists(), "Generated ruleset file does not exist");
        String generatedContent = new String(Files.readAllBytes(Paths.get(GENERATED_RULESET_FILE_PATH)));

        File expectedFile = new File(EXPECTED_RULESET_FILE_PATH);
        assertTrue(expectedFile.exists(), "Expected ruleset file does not exist");
        String expectedContent = new String(Files.readAllBytes(Paths.get(EXPECTED_RULESET_FILE_PATH)));

        generatedContent = normalizeContent(generatedContent);
        expectedContent = normalizeContent(expectedContent);

        assertEquals(expectedContent, generatedContent, "Generated ruleset does not match expected.");
    }

    @Test
    void testPMDReportGeneration() throws IOException {

        metricAnalyzer.runMetrics(codeAnalysisConfig);
        File generatedFile = new File("src/main/resources/java_sarif.sarif");
        assertTrue(generatedFile.exists(), "Generated ruleset file does not exist");
    }
    private String normalizeContent(String content) {
        // Убираем все символы новой строки, пробелы и табуляции
        content = content.replaceAll("\\s+", "");
        return content;
    }

    private static CodeAnalysisConfig loadConfig() throws IOException {
        InputStream input = MetricAnalyzerTest.class.getClassLoader().getResourceAsStream("properties.json");
        if (input == null) {
            throw new IllegalArgumentException("File not found! properties.json");
        }
        return ConfigParser.parseConfig(input);
    }
}
