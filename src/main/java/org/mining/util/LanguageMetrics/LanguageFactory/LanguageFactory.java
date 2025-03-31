package org.mining.util.LanguageMetrics.LanguageFactory;

import org.mining.util.LanguageMetrics.LanguageAnalyzer.JavaRulesetGenerator;
import org.mining.util.LanguageMetrics.LanguageAnalyzer.JavascriptRulesetGenerator;
import org.mining.util.LanguageMetrics.LanguageParsingStartegies.JavaParserStrategy;
import org.mining.util.LanguageMetrics.LanguageParsingStartegies.JavascriptParserStrategy;
import org.mining.util.inputparser.SupportedLanguages;

public class LanguageFactory {
    /**
     * Returns a {@link LanguageProcessingComponents} instance for the specified language.
     *
     * <p>This method initializes and returns a new {@link LanguageProcessingComponents} object,
     * which contains both a metric generator and a parsing strategy specific to the provided
     * {@link SupportedLanguages} type.</p>
     *
     * @param language The language for which to retrieve processing components.
     * @return A {@link LanguageProcessingComponents} instance containing the metric generator
     *         and parsing strategy for the specified language, or {@code null} if the language
     *         is not supported.
     */
    public static LanguageProcessingComponents getMetricGenerator(SupportedLanguages language){
        return switch (language) {
            case Java -> new LanguageProcessingComponents(new JavaRulesetGenerator(), new JavaParserStrategy());
            case Javascript -> new LanguageProcessingComponents(new JavascriptRulesetGenerator(),
                    new JavascriptParserStrategy());
            default -> null;
        };
    }
}

