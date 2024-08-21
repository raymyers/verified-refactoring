package ai.mender;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.printer.XmlPrinter;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;
import com.github.javaparser.utils.CodeGenerationUtils;
import com.github.javaparser.utils.SourceRoot;

public class Views {
    public static void main(String[] args) {
        SourceRoot sourceRoot = new SourceRoot(CodeGenerationUtils.mavenModuleRoot(RefactorEngine.class).resolve("src/main/java"));
        configureParser(sourceRoot.getParserConfiguration());

        // Our sample is in the root of this directory, so no package name.
        CompilationUnit cu = sourceRoot.parse(GildedRoseRefactor.class.getPackageName(), "GildedRose.java");
        ClassOrInterfaceDeclaration klass = cu.getClassByName(GildedRose.class.getSimpleName()).get();
        MethodDeclaration method = klass.getMethodsByName("updateItem").get(0);

//        System.out.println(new YamlPrinter(true).output(method));
        System.out.println(new XmlPrinter(true).output(method));
//        cu.toString()
//        method.findAll(IfStmt.class).forEach(ifStmt -> {
//            if (ifStmt.getElseStmt().isPresent()) {
//                Node condition = ifStmt.getCondition();
//                select(condition);
//            }
//        });
//        method.findAll(FieldAccessExpr.class).forEach(fieldAccess -> {
//            if (fieldAccess.getScope() instanceof NameExpr) {
//                NameExpr scope = (NameExpr) fieldAccess.getScope();
//                if (scope.getName().getIdentifier().equals("item") && fieldAccess.getName().getIdentifier().equals("name")) {
//                    select(fieldAccess);
//                }
//            }
//        });



        method.findAll(BinaryExpr.class).forEach(binaryExpr -> {
            if (binaryExpr.getOperator() == BinaryExpr.Operator.MINUS) {
                if (binaryExpr.getLeft().equals(binaryExpr.getRight())) {
                    select(binaryExpr);
                }
            }
        });
    }

    private static void select(Node node) {
        System.out.println(node.getRange());
        System.out.println(node);
    }


    private static ClassOrInterfaceDeclaration parseCodeAndGetClass(String code, String className) {
        JavaParser javaParser = createParser();

        ParseResult<CompilationUnit> parseResult = javaParser.parse(code);
        CompilationUnit compilationUnit = parseResult.getResult().get();

        return compilationUnit.getClassByName(className).get();
    }

    private static JavaParser createParser() {
        JavaParser javaParser = new JavaParser();
        configureParser(javaParser.getParserConfiguration());
        return javaParser;
    }

    private static void configureParser(ParserConfiguration parserConfiguration) {
        // Set up a minimal type solver that only looks at the classes used to run this sample.
        CombinedTypeSolver combinedTypeSolver = new CombinedTypeSolver();
        combinedTypeSolver.add(new ReflectionTypeSolver());

        // Configure JavaParser to use type resolution
        JavaSymbolSolver symbolSolver = new JavaSymbolSolver(combinedTypeSolver);

        parserConfiguration.setSymbolResolver(symbolSolver);
    }
}
