package org.mining.util.inputparser;

import org.junit.jupiter.api.Test;
import org.mining.util.gitmetrics.GitMetricEnum;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;

class ConfigParserTest {

    @Test
    void testParseConfigValidInput() throws Exception {
        // JSON string representing a valid CodeAnalysisConfig
        String validJson = """
            {
              "repositoryPath": "/path/to/repo",
              "git_metrics": {
                "CommitSize": {
                  "commitDepth": 10
                }
              }
            }
        """;

        InputStream inputStream = new ByteArrayInputStream(validJson.getBytes());

        // Parse the input stream
        CodeAnalysisConfig config = ConfigParser.parseConfig(inputStream);

        // Validate the parsed configuration
        assertNotNull(config);
        assertEquals("/path/to/repo", config.getRepositoryPath());
        assertNotNull(config.getGitMetrics());
        assertTrue(config.getGitMetrics().containsKey(GitMetricEnum.CommitSize));
        assertEquals(10, config.getGitMetrics().get(GitMetricEnum.CommitSize).getCommitDepth());
    }

    @Test
    void testParseConfigInvalidInput() {
        // Invalid JSON string
        String invalidJson = """
            {
              "repositoryPath": "",
              "git_metrics": {
                "COMMIT_COUNT": {
                  "commitDepth": "invalid_value"
                }
              }
            }
        """;

        InputStream inputStream = new ByteArrayInputStream(invalidJson.getBytes());

        // Attempt to parse the invalid JSON and expect an IOException
        assertThrows(IOException.class, () -> ConfigParser.parseConfig(inputStream));
    }

}