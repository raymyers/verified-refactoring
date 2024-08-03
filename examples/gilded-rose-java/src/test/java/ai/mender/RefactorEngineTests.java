package ai.mender;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import org.approvaltests.Approvals;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import java.util.List;

public class RefactorEngineTests {
    @Test
    public void replaceFieldAccess_updatesMatchingAccess() {
        String code = """
                class A {
                    public void foo() {
                        a.field;
                    }
                }
                """;
        MethodDeclaration method = parseCodeAndGetMethod(code, "A", "foo");
        StringLiteralExpr replacement = new StringLiteralExpr("hello");
        RefactorEngine.replaceFieldAccess(method, "a", "field", replacement);
        Approvals.verify(method);
    }

    @Test
    public void replaceFieldAccess_doNotUpdateNonMatching() {
        String code = """
                class A {
                    public void foo() {
                        a.field;
                    }
                }
                """;
        MethodDeclaration method = parseCodeAndGetMethod(code, "A", "foo");
        StringLiteralExpr replacement = new StringLiteralExpr("hello");
        String before = method.toString();
        RefactorEngine.replaceFieldAccess(method, "a", "otherField", replacement);
        String after = method.toString();

        Assertions.assertEquals(before, after);
    }

    private static MethodDeclaration parseCodeAndGetMethod(String code, String className, String methodName) {
        JavaParser javaParser = new JavaParser();
        ParseResult<CompilationUnit> parseResult = javaParser.parse(code);
        CompilationUnit compilationUnit = parseResult.getResult().get();

        ClassOrInterfaceDeclaration klass = compilationUnit.getClassByName(className).get();

        List<MethodDeclaration> methods = klass.getMethodsByName(methodName);
        return methods.get(0);
    }
}
