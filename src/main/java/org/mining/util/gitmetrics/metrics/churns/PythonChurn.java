package org.mining.util.gitmetrics.metrics.churns;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.mining.util.gitmetrics.antlr.Python3Lexer;
import org.mining.util.gitmetrics.antlr.Python3Parser;
import org.mining.util.gitmetrics.antlr.Python3ParserBaseListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PythonChurn extends BaseChurn {

    public static List<MethodRange> parseMethods(String fileContent) throws IOException {
        List<MethodRange> methods = new ArrayList<>();
        CharStream input = CharStreams.fromString(fileContent);
        Python3Lexer lexer = new Python3Lexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        Python3Parser parser = new Python3Parser(tokens);
        ParseTree tree = parser.file_input();
        ParseTreeWalker walker = new ParseTreeWalker();
        MethodListener listener = new MethodListener(methods);
        walker.walk(listener, tree);
        return methods;
    }

    private static class MethodListener extends Python3ParserBaseListener {
        private final List<MethodRange> methods;

        public MethodListener(List<MethodRange> methods) {
            this.methods = methods;
        }

        @Override
        public void enterFuncdef(Python3Parser.FuncdefContext ctx) {
            String methodName = ctx.name().getText();
            String parameters = getParameters(ctx.parameters());
            int lineNumber = ctx.start.getLine();
            methods.add(new MethodRange(methodName + "(" + parameters + ")", lineNumber, null));
        }

        private String getParameters(Python3Parser.ParametersContext ctx) {
            if (ctx == null || ctx.typedargslist() == null) {
                return "";
            }
            StringBuilder params = new StringBuilder();
            for (Python3Parser.TfpdefContext context : ctx.typedargslist().tfpdef()) {
                String paramName = context.name().getText();
                params.append(paramName).append(", ");
            }
            if (!params.isEmpty()) {
                params.setLength(params.length() - 2);
            }
            return params.toString();
        }
    }

}
