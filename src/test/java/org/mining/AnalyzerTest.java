package org.mining;

import org.junit.jupiter.api.Test;
import org.mining.util.LanguageMetrics.MetricAnalyzer;
import org.mining.util.inputparser.CodeAnalysisConfig;
import org.mining.util.inputparser.ConfigParser;
import org.mining.util.inputparser.SupportedLanguages;
import org.mining.util.sarifparser.SarifMerger;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class AnalyzerTest {
    @Test
    void testDeleteDirectory() {
        // Create a nested directory structure
        File dir = new File("testDir");
        File subDir = new File(dir, "subDir");
        subDir.mkdirs();

        File file = new File(subDir, "file.txt");
        try {
            assertTrue(file.createNewFile());
        } catch (IOException e) {
            fail("Failed to create test file: " + e.getMessage());
        }

        // Ensure directory and file exist
        assertTrue(file.exists());
        assertTrue(dir.exists());

        // Delete the directory
        Analyzer.deleteDirectory(dir);

        // Verify deletion
        assertFalse(dir.exists());
    }

    @Test
    void testGetConfig() throws IOException {
        // Ensure the properties.json file exists in the correct location
        String resourcePath = Analyzer.class.getClassLoader().getResource("properties.json").getPath();
        assertNotNull(resourcePath);

        // Call getConfig and verify the parsed config is not null
        Analyzer.getConfig(null);
        assertNotNull(Analyzer.codeAnalysisConfig);
    }

    @Test
    void testRunAllLanguages() throws IOException {
        InputStream configInputStream = getClass().getClassLoader().getResourceAsStream("propertiesJavascript.json");
        if (configInputStream == null) {
            throw new IllegalArgumentException("Config file not found!");
        }
        CodeAnalysisConfig codeAnalysisConfig = ConfigParser.parseConfig(configInputStream);
        String currentDirectory = System.getProperty("user.dir");

        // Create the folder path
        File folder = new File(currentDirectory);
        codeAnalysisConfig.setRepositoryPath(folder.getAbsolutePath());

        MetricAnalyzer metricAnalyzer = new MetricAnalyzer();

        Map<SupportedLanguages, CodeAnalysisConfig.LanguageConfig> map = new HashMap<>();
        CodeAnalysisConfig.LanguageConfig languageConfig = new CodeAnalysisConfig.LanguageConfig();
        map.put(SupportedLanguages.Java, languageConfig);
        map.put(SupportedLanguages.Javascript, languageConfig);
        codeAnalysisConfig.setLanguageSpecificSettings(map);
        metricAnalyzer.runMetrics(codeAnalysisConfig);
        File jsReport = new File(currentDirectory + File.separator + "src" + File.separator +
                "main" + File.separator + "resources" + File.separator +
                "eslint_report.txt");
        assertTrue(jsReport.exists());
        File javaReport = new File(currentDirectory + File.separator + "src" + File.separator +
                "main" + File.separator + "resources" + File.separator +
                "java_sarif.sarif");
        assertTrue(javaReport.exists());
        SarifMerger.mergeSarif();
        File sarifReport = new File(currentDirectory + File.separator + "src" + File.separator +
                "main" + File.separator + "resources" + File.separator +
                "Final.sarif");
        assertTrue(sarifReport.exists());


    }

}