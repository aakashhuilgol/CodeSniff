package org.mining.util.LanguageMetrics.LanguageParsingStartegies;

import net.sourceforge.pmd.PMDConfiguration;
import net.sourceforge.pmd.PmdAnalysis;
import net.sourceforge.pmd.lang.java.JavaLanguageModule;
import net.sourceforge.pmd.lang.rule.RulePriority;
import net.sourceforge.pmd.renderers.Renderer;
import net.sourceforge.pmd.renderers.TextRenderer;

import javax.print.DocFlavor;
import java.io.*;
import java.nio.file.Path;
import java.util.Properties;

public class JavaParserStrategy implements ILanguageParserStrategy {
    private static String rulesetFilePath;
    private static final String reportPath = "src/main/resources/java_sarif.sarif";

    static {
        try (InputStream input = new FileInputStream("src/main/config.properties")) {
            Properties prop = new Properties();
            prop.load(input);
            rulesetFilePath = prop.getProperty("JavaPMD_ruleset.filepath");

            if (rulesetFilePath == null || rulesetFilePath.isEmpty()) {
                throw new IOException("JavaPMD_ruleset.filepath not found in config.properties.");
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    @Override
    public void execute(String sourceDir) {
        runPmdTest(sourceDir);
    }

    private void runPmdTest(String sourceDir){
        PMDConfiguration configuration = new PMDConfiguration();
        configuration.setMinimumPriority(RulePriority.MEDIUM);

        configuration.addInputPath(Path.of(sourceDir));
        configuration.setDefaultLanguageVersion(JavaLanguageModule.getInstance().getVersion("21"));
        configuration.prependAuxClasspath("target");


        configuration.setReportFormat("sarif");
        configuration.setReportFile(Path.of(reportPath));

//        Writer rendererOutput = new StringWriter();
//        Renderer renderer = createRenderer(rendererOutput);

        try (PmdAnalysis pmd = PmdAnalysis.create(configuration)) {
            pmd.addRuleSet(pmd.newRuleSetLoader().loadFromResource(rulesetFilePath));
            //pmd.addRenderer(renderer);
            pmd.performAnalysis();
        }
    }

    private static Renderer createRenderer(Writer writer) {
        TextRenderer text = new TextRenderer();
        text.setWriter(writer);
        return text;
    }


}
