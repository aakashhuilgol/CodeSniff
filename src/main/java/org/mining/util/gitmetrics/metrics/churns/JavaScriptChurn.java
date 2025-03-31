package org.mining.util.gitmetrics.metrics.churns;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.mining.util.gitmetrics.antlr.JavaScriptLexer;
import org.mining.util.gitmetrics.antlr.JavaScriptParser;
import org.mining.util.gitmetrics.antlr.JavaScriptParserBaseListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class JavaScriptChurn extends BaseChurn {

    public static List<MethodRange> parseMethods(String fileContent) throws IOException {
        List<MethodRange> methods = new ArrayList<>();
        CharStream input = CharStreams.fromString(fileContent);
        JavaScriptLexer lexer = new JavaScriptLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        JavaScriptParser parser = new JavaScriptParser(tokens);
        ParseTree tree = parser.program();
        ParseTreeWalker walker = new ParseTreeWalker();
        MethodListener listener = new MethodListener(methods);
        walker.walk(listener, tree);
        return methods;
    }

    private static class MethodListener extends JavaScriptParserBaseListener {
        private final List<MethodRange> methods;

        public MethodListener(List<MethodRange> methods) {
            this.methods = methods;
        }

        @Override
        public void enterFunctionDeclaration(JavaScriptParser.FunctionDeclarationContext ctx) {
            String methodName = ctx.getChild(1).getText();
            String parameters = getParameters(ctx.formalParameterList());
            int lineNumber = ctx.start.getLine();
            methods.add(new MethodRange(methodName + "(" + parameters + ")", lineNumber, null));
        }

        @Override
        public void enterFunctionExpression(JavaScriptParser.FunctionExpressionContext ctx) {
            String methodName = "anonymous";
            if (ctx.getChildCount() > 0 && ctx.getChild(0) instanceof JavaScriptParser.ArrowFunctionContext) {
                methodName = "arrow_function";
            }
            else if (ctx.getChildCount() > 0 && ctx.getChild(0) instanceof JavaScriptParser.FunctionExpressionContext) {
                methodName = "anonymous";
            }
            int lineNumber = ctx.start.getLine();
            methods.add(new MethodRange(methodName, lineNumber, null));
        }

        @Override
        public void enterMethodDefinition(JavaScriptParser.MethodDefinitionContext ctx) {
            String methodName = ctx.classElementName().getText();
            String parameters = getParameters(ctx.formalParameterList());
            int lineNumber = ctx.start.getLine();
            methods.add(new MethodRange(methodName + "(" + parameters + ")", lineNumber, null));
        }

        private String getParameters(JavaScriptParser.FormalParameterListContext ctx) {
            if (ctx == null || ctx.formalParameterArg() == null) {
                return "";
            }
            StringBuilder params = new StringBuilder();
            for (JavaScriptParser.FormalParameterArgContext paramCtx : ctx.formalParameterArg()) {
                String paramName = paramCtx.getText();
                params.append(paramName).append(", ");
            }
            if (!params.isEmpty()) {
                params.setLength(params.length() - 2);
            }
            return params.toString();
        }
    }

}