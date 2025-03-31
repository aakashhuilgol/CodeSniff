package org.mining.util.gitmetrics.metrics.churns;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.mining.util.gitmetrics.antlr.JavaLexer;
import org.mining.util.gitmetrics.antlr.JavaParser;
import org.mining.util.gitmetrics.antlr.JavaParserBaseListener;

import java.util.ArrayList;
import java.util.List;

public class JavaChurn extends BaseChurn {

    public static List<MethodRange> parseMethods(String fileContent) throws RecognitionException {
        List<MethodRange> methods = new ArrayList<>();
        CharStream input = CharStreams.fromString(fileContent);
        JavaLexer lexer = new JavaLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        JavaParser parser = new JavaParser(tokens);
        ParseTree tree = parser.compilationUnit();
        ParseTreeWalker walker = new ParseTreeWalker();
        JavaChurn.MethodListener listener = new MethodListener(methods);
        walker.walk(listener, tree);
        return methods;
    }

    private static class MethodListener extends JavaParserBaseListener {
        private final List<JavaChurn.MethodRange> methods;

        public MethodListener(List<JavaChurn.MethodRange> methods) {
            this.methods = methods;
        }

        @Override
        public void enterMethodDeclaration(JavaParser.MethodDeclarationContext ctx) {
            String methodName = ctx.identifier().getText();
            List<String> parameterList = getParameters(ctx.formalParameters());
            String methodSignature = ctx.typeTypeOrVoid() != null ? ctx.typeTypeOrVoid().getText() + " " + methodName : methodName;
            methodSignature += "(" + String.join(", ", parameterList) + ")";
            int lineNumber = ctx.start.getLine();
            methods.add(new MethodRange(methodSignature, lineNumber, null));
        }

        private List<String> getParameters(JavaParser.FormalParametersContext formalParametersContext) {
            List<String> parameterList = new ArrayList<>();
            if (formalParametersContext != null && formalParametersContext.formalParameterList() != null) {
                for (JavaParser.FormalParameterContext paramContext : formalParametersContext.formalParameterList().formalParameter()) {
                    String type = paramContext.typeType().getText();
                    String paramName = paramContext.variableDeclaratorId().getText();
                    parameterList.add(type + " " + paramName);
                }
            }
            return parameterList;
        }
    }

}
