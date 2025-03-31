package org.mining.util.LanguageMetrics;

import org.junit.jupiter.api.Test;
import org.mining.util.inputparser.CodeAnalysisConfig;
import org.mining.util.inputparser.ConfigParser;
import org.mining.util.inputparser.SupportedLanguages;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class MetricAnalyzerTest {

    @Test
    void testRunValidMetrics() throws IOException {
        InputStream configInputStream = getClass().getClassLoader().getResourceAsStream("propertiesJavascript.json");
        if (configInputStream == null) {
            throw new IllegalArgumentException("Config file not found!");
        }
        CodeAnalysisConfig codeAnalysisConfig = ConfigParser.parseConfig(configInputStream);
        String currentDirectory = System.getProperty("user.dir");

        // Create the folder path
        File folder = new File(currentDirectory + File.separator + "src" + File.separator +
                "test" + File.separator + "resources" + File.separator + "TestJavascriptFiles");
        codeAnalysisConfig.setRepositoryPath(folder.getAbsolutePath());

        MetricAnalyzer metricAnalyzer = new MetricAnalyzer();

        Map<SupportedLanguages, CodeAnalysisConfig.LanguageConfig> map = new HashMap<>();
        CodeAnalysisConfig.LanguageConfig languageConfig = new CodeAnalysisConfig.LanguageConfig();
        languageConfig.setEnabled(false);
        map.put(SupportedLanguages.Java, languageConfig);
        map.put(SupportedLanguages.Javascript, languageConfig);
        map.put(SupportedLanguages.Python, languageConfig);
        assertDoesNotThrow(() -> metricAnalyzer.runMetrics(codeAnalysisConfig));
    }

}