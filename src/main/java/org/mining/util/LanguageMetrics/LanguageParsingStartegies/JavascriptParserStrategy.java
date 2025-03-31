package org.mining.util.LanguageMetrics.LanguageParsingStartegies;

import org.mining.util.sarifparser.ESLintSarifParser;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public class JavascriptParserStrategy implements ILanguageParserStrategy{

    private static String rulesetFilePath;
    private static String dockerFilePath;
    private final String outputPath = "src/main/resources";

    static {
        try (InputStream input = new FileInputStream("src/main/config.properties")) {
            Properties prop = new Properties();
            prop.load(input);
            rulesetFilePath = prop.getProperty("ESlint_ruleset.filepath");
            dockerFilePath = prop.getProperty("DockerESlint.filepath");

            if (rulesetFilePath == null || rulesetFilePath.isEmpty()) {
                throw new IOException("ESlint_ruleset.filepath not found in config.properties.");
            }
            if (dockerFilePath == null || dockerFilePath.isEmpty()) {
                throw new IOException("DockerESlint.filepath not found in config.properties.");
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void execute(String sourceDir) {
        runJavascriptAnalysis(sourceDir);
    }

    private void runJavascriptAnalysis(String sourceDir){
        try{
            buildDockerImage();
            runEslintDocker(sourceDir);
            copyResultFromDocker();
            cleanUpDocker();
            ESLintSarifParser.parseESLint();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    private void buildDockerImage() throws InterruptedException, IOException {
        ProcessBuilder buildProcessBuilder = new ProcessBuilder("docker", "build", "-t", "eslint-analysis", "-f", dockerFilePath, ".");
        buildProcessBuilder.inheritIO();
        Process buildProcess = buildProcessBuilder.start();
        buildProcess.waitFor();
    }

    private void runEslintDocker(String sourceDir) throws IOException, InterruptedException {
        File eslintConfigFile = new File(rulesetFilePath);
        String eslintConfigPath = eslintConfigFile.getAbsolutePath();

        File sourceFile = new File(sourceDir);
        String sourceDirPath = sourceFile.getAbsolutePath();

        File customRules = new File(outputPath + "/depth-of-inheritance-tree.js");
        String customRulesPath = customRules.getAbsolutePath();

        ProcessBuilder runProcessBuilder = getProcessBuilder(customRulesPath, sourceDirPath, eslintConfigPath);
        Process runProcess = runProcessBuilder.start();
        runProcess.waitFor();
    }

    private ProcessBuilder getProcessBuilder(String customRulesPath, String sourceDirPath, String eslintConfigPath) {
        ProcessBuilder runProcessBuilder = new ProcessBuilder(
                "docker", "run", "--name", "eslint-runner",
                "-v", customRulesPath + ":/app/depth-of-inheritance-tree.js",
                "-v", sourceDirPath + ":/app",                    // Mounts the source directory to /app in the container
                "-v", eslintConfigPath + ":/app/eslint.config.js", // Mounts ESLint config file to /app/.eslintrc.js in the container
                "eslint-analysis");
        runProcessBuilder.inheritIO();
        return runProcessBuilder;
    }

    private void copyResultFromDocker() throws IOException, InterruptedException {
        File outputDir = new File(outputPath);
        if (!outputDir.exists()) outputDir.mkdirs();
        Path outputPath = outputDir.toPath().resolve("eslint_report.txt");

        ProcessBuilder copyProcessBuilder = new ProcessBuilder(
                "docker", "cp", "eslint-runner:/app/eslint_report.txt", outputPath.toString());
        Process copyProcess = copyProcessBuilder.start();
        int copyExitCode = copyProcess.waitFor();

        if (copyExitCode == 0 && Files.exists(outputPath)) {
            System.out.println("ESLint report copied to resources/eslint_report.txt");
        } else {
            System.out.println("Failed to copy ESLint report. Report may not have been generated.");
        }
    }

    private void cleanUpDocker() throws InterruptedException, IOException {
        ProcessBuilder rmProcessBuilder = new ProcessBuilder("docker", "rm", "eslint-runner");
        rmProcessBuilder.inheritIO();
        Process rmProcess = rmProcessBuilder.start();
        rmProcess.waitFor();
    }
}
