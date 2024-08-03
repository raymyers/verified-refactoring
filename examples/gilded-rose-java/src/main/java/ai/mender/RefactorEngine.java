package ai.mender;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.utils.CodeGenerationUtils;
import com.github.javaparser.utils.Log;
import com.github.javaparser.utils.SourceRoot;

import java.util.List;
import java.util.Optional;

public class RefactorEngine {

    public static void main(String[] args) {
        // JavaParser has a minimal logging class that normally logs nothing.
        // Let's ask it to write to standard out:
        Log.setAdapter(new Log.StandardOutStandardErrorAdapter());
        
        // SourceRoot is a tool that read and writes Java files from packages on a certain root directory.
        // In this case the root directory is found by taking the root from the current Maven module,
        // with src/main/resources appended.
        SourceRoot sourceRoot = new SourceRoot(CodeGenerationUtils.mavenModuleRoot(RefactorEngine.class).resolve("src/main/java"));

        // Our sample is in the root of this directory, so no package name.
        CompilationUnit cu = sourceRoot.parse(GildedRoseRefactor.class.getPackageName(), "GildedRose.java");

        Log.info("");

        ClassOrInterfaceDeclaration klass = cu.getClassByName("GildedRose").get();
        List<MethodDeclaration> methods = klass.getMethodsByName("updateItem");
        MethodDeclaration method = methods.get(0);
        createSpecializedMethod(klass, method, "updateBrie", "Aged Brie");
        createSpecializedMethod(klass, method, "updateSulfuras", "Sulfuras, Hand of Ragnaros");
        createSpecializedMethod(klass, method, "updatePasses", "Backstage passes to a TAFKAL80ETC concert");
        createSpecializedMethod(klass, method, "updateNormal", "..normal..");

        System.out.println(klass);
    }

    private static void createSpecializedMethod(ClassOrInterfaceDeclaration klass, MethodDeclaration method, String methodName, String itemName) {
        MethodDeclaration newMethod = method.clone().setName(methodName);
        klass.addMember(newMethod);
        specializeForItemName(newMethod, itemName);
    }

    static void specializeForItemName(MethodDeclaration method, String nameVal) {
        replaceFieldAccess(method, "item", "name", new StringLiteralExpr(nameVal));

        evaluateEqualsOnStringLiterals(method);

        simplifyNotOnBooleanLiterals(method);
        simplifyAndOnBooleanLiterals(method);

        simplifyIfsOnBooleanLiterals(method);

        collapseNestedBlocks(method);

        removeEmptyIfs(method);
        removeEmptyIfs(method);
    }

    static void replaceFieldAccess(MethodDeclaration method, String scope, String fieldName, StringLiteralExpr expr) {
        method.findAll(FieldAccessExpr.class).forEach(n -> {
            if (n.getScope().toString().equals(scope) && n.getName().getIdentifier().equals(fieldName)) {
                n.replace(expr);
            }
        });
    }

    static void evaluateEqualsOnStringLiterals(MethodDeclaration method) {
        method.findAll(MethodCallExpr.class).forEach(n -> {
            if (n.getName().getIdentifier().equals("equals")) {
                if (n.getScope().get() instanceof StringLiteralExpr) {
                    StringLiteralExpr scopeLit = (StringLiteralExpr) n.getScope().get();
                    if (n.getArguments().size() == 1) {
                        Expression arg = n.getArguments().get(0);
                        if (arg instanceof StringLiteralExpr) {
                            StringLiteralExpr argLit = (StringLiteralExpr) arg;
                            boolean result = argLit.getValue().equals(scopeLit.getValue());
                            n.replace(new BooleanLiteralExpr(result));
                        }
                    }
                }
            }

        });
    }

    static void simplifyNotOnBooleanLiterals(MethodDeclaration method) {
        method.findAll(UnaryExpr.class).forEach(n -> {
            if (n.getOperator() == UnaryExpr.Operator.LOGICAL_COMPLEMENT && n.getExpression() instanceof BooleanLiteralExpr) {
                boolean val = ((BooleanLiteralExpr) n.getExpression()).getValue();
                n.replace(new BooleanLiteralExpr(!val));
            }
        });
    }

    static void simplifyAndOnBooleanLiterals(MethodDeclaration method) {
        method.findAll(BinaryExpr.class).forEach(n -> {
            if (n.getOperator() == BinaryExpr.Operator.AND && n.getLeft() instanceof BooleanLiteralExpr) {
                boolean val = ((BooleanLiteralExpr) n.getLeft()).getValue();
                if (val) {
                    n.replace(n.getRight());
                } else {
                    n.replace(new BooleanLiteralExpr(false));
                }
            }
        });
    }

    static void simplifyIfsOnBooleanLiterals(MethodDeclaration method) {
        method.findAll(IfStmt.class).forEach(n -> {
            if (n.getCondition() instanceof BooleanLiteralExpr) {

                if (((BooleanLiteralExpr) n.getCondition()).getValue()) {
                    n.replace(n.getThenStmt());
                } else {
                    Optional<Statement> elseStmt = n.getElseStmt();
                    if (elseStmt.isPresent()) {
                        n.replace(elseStmt.get());
                    } else {
                        n.remove();
                    }
                }
            }
        });
    }

    static void collapseNestedBlocks(MethodDeclaration method) {
        method.findAll(BlockStmt.class).forEach(n -> {
            // Collapse block statements directly within other block statements
            Optional<Node> parentNode = n.getParentNode();
            if (parentNode.isPresent() && parentNode.get() instanceof BlockStmt) {
                var parentBlock = (BlockStmt) parentNode.get();
                NodeList<Statement> statements = parentBlock.getStatements();
                int index = statements.indexOf(n);
                n.remove();

                for (var childStatment : n.getStatements()) {
                    statements.add(index++, childStatment);
                }

            }
        });
    }

    static void removeEmptyIfs(MethodDeclaration method) {
        method.findAll(IfStmt.class).forEach(n -> {
            // Remove if statements
            // WARN: Not ok if condition has side-effects
            Statement thenStmt = n.getThenStmt();
            if (isEmptyBlock(thenStmt) && (!n.hasElseBranch() || isEmptyBlock(n.getElseStmt().get()))) {
                n.remove();
            }

        });
    }

    static boolean isEmptyBlock(Statement thenStmt) {
        return thenStmt.isBlockStmt() && thenStmt.asBlockStmt().getStatements().size() == 0;
    }
}