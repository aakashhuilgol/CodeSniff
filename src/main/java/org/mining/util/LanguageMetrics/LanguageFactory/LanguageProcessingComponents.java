package org.mining.util.LanguageMetrics.LanguageFactory;

import org.mining.util.LanguageMetrics.LanguageAnalyzer.ILanguageMetricGenerator;
import org.mining.util.LanguageMetrics.LanguageParsingStartegies.ILanguageParserStrategy;

public record LanguageProcessingComponents(ILanguageMetricGenerator metricGenerator,
                                           ILanguageParserStrategy parserStrategy) {
}
