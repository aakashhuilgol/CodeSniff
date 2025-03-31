package org.mining.util.inputparser;

import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Getter;
import lombok.Setter;
import org.mining.util.gitmetrics.GitMetricEnum;

@Getter
@Setter
public class CodeAnalysisConfig {
    private String repositoryPath;

    @JsonProperty("metrics")
    private Map<MetricEnum, MetricConfig> metrics;

    @JsonProperty("git_metrics")
    private Map<GitMetricEnum, MetricConfig> gitMetrics;

    @JsonProperty("languageSpecificSettings")
    private Map<SupportedLanguages, LanguageConfig> languageSpecificSettings;

    @Getter
    @Setter
    public static class MetricConfig {
        private boolean enabled = true;
        private Integer maxMethodLength;
        private Integer classSizeThreshold;
        private Integer maxParameters;
        private Integer maxInheritanceDepth;
        private Integer maxCoupling;
        private Integer maxCyclomaticComplexity;
        private Integer maxDependencies;
        @Setter //for testing
        private Integer commitDepth = -1;

        @JsonSetter("enabled")
        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
    }

    @Getter
    @Setter
    public static class LanguageConfig {
        private boolean enabled = true;

        @JsonSetter("enabled")
        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

    }
}
