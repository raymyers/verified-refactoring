package ai.mender.tripservice;

import ai.mender.RefactorEngine;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.Position;
import com.github.javaparser.Range;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.stmt.*;
import com.github.javaparser.ast.type.*;
import com.github.javaparser.printer.YamlPrinter;
import com.github.javaparser.resolution.UnsolvedSymbolException;
import com.github.javaparser.resolution.declarations.ResolvedDeclaration;
import com.github.javaparser.resolution.types.ResolvedType;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.approvaltests.Approvals;
import org.approvaltests.StoryBoardApprovals;
import org.approvaltests.VerifiableStoryBoard;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ExtractMethodTests {
    final String code = """
public class TripService {
	public List<Trip> getTripsByUser(User user) throws UserNotLoggedInException {
		List<Trip> tripList = new ArrayList<Trip>();
		User loggedUser = UserSession.getInstance().getLoggedUser();
		boolean isFriend = false;
		if (loggedUser != null) {
			for (User friend : user.getFriends()) {
				if (friend.equals(loggedUser)) {
					isFriend = true;
					break;
				}
			}
			if (isFriend) {
				tripList = TripDAO.findTripsByUser(user);
			}
			return tripList;
		} else {
			throw new UserNotLoggedInException();
		}
	}
}
                """;

//    * Create a new method, copy the extracted code from the source to the target method,
//    * Choose as parameters for the new method the variables referenced in the extracted
//      code which are in the local scope of the source method,
//    * Remove parameters that are used in the extracted part only and instead declare
//    them as temporary variables within the new method,
//    * Abort if more than one of the parameters are assigned (as changes are not visible
//      n the source method); treat the extracted code as a query if exactly one of the
//        parameters are assigned (assign the result of the method to the variable concerned),
//•   * compile,
//    * Replace the extracted code in the source method with a call to the target method,
//    * compile
// Additional constraints
// * The extracted fragment must not return
// * If the extracted fragment throws an exception, it must not change the value of the query
//    result variable var before.


    @Test
    public void extractUnsound() {
        String hello = "goodbye";
        try {
            hello = "hello";
            throw new RuntimeException();
        } catch (Exception e) {
            // ignore
        }
        Assertions.assertEquals("hello", hello);
    }

    @Test void extractReturn() {
        final String code = """
public class C {
	public boolean a() throws UserNotLoggedInException {
		return true;
	}
}
                """;
        ClassOrInterfaceDeclaration klass = parseCodeAndGetClass(code, "C");
        MethodDeclaration method = klass.getMethodsByName("a").get(0);
        NodeList<Statement> statementsToExtract = method.getBody().get().getStatements();
        MethodDeclaration extractedMethod = extractMethod(method, statementsToExtract, "extracted");
        klass.addMember(extractedMethod);
        Approvals.verify(klass.toString());
    }

    @Test void extractWithGenericReturnType() {
        final String code = """
import java.util.*;
public class C {
	public List<String> a() throws UserNotLoggedInException {
		return new ArrayList<>();
	}
}
                """;
        ClassOrInterfaceDeclaration klass = parseCodeAndGetClass(code, "C");
        MethodDeclaration method = klass.getMethodsByName("a").get(0);
        NodeList<Statement> statementsToExtract = method.getBody().get().getStatements();
        MethodDeclaration extractedMethod = extractMethod(method, statementsToExtract, "extracted");
        klass.addMember(extractedMethod);
        Approvals.verify(klass.toString());
    }
    @Test
    public void replaceFieldAccess_updatesMatchingAccess() {
        VerifiableStoryBoard storyBoard = StoryBoardApprovals.verifyStoryboard();
        ClassOrInterfaceDeclaration klass = parseCodeAndGetClass(code, "TripService");
        MethodDeclaration method = klass.getMethodsByName("getTripsByUser").get(0);
        NodeList<Statement> bodyStmts = method.getBody().get().getStatements();
        storyBoard.addFrame("# Initial", klass.toString());
        {
            String methodName = "getInstance";
            String scope = "UserSession";

            findFirstMatchingMethodCall(method, methodName, scope).ifPresent((methodCallExpr -> {
                var name = "session";
                var initExpr = methodCallExpr.clone();
                methodCallExpr.replace(new NameExpr(name));
                VariableDeclarationExpr declExpr = new VariableDeclarationExpr(new VarType(), name);
                declExpr.getVariables().getFirst().get().setInitializer(initExpr);
                bodyStmts.add(0, new ExpressionStmt(declExpr));
            }));
            method.resolve();
            storyBoard.addFrame("# Extract variable 'session'", klass.toString());
        }
        {
            String daoClassName = "TripDAO";
            String daoVarName = "tripDao";
            findMatchingNameExprs(method, daoClassName).forEach((n) -> {
                n.replace(new NameExpr(daoVarName));
            });
            // declare tripDao Var
            VariableDeclarationExpr declExpr = new VariableDeclarationExpr(new VarType(), daoVarName);
            declExpr.getVariables().getFirst().get().setInitializer(new ObjectCreationExpr().setType(new ClassOrInterfaceType(daoClassName)));
            bodyStmts.add(0, new ExpressionStmt(declExpr));
            method.resolve();
            storyBoard.addFrame("# Extract variable 'tripDao' and convert TripDAO to constructor", klass.toString());
        }
        final MethodDeclaration extractedMethod;
        {

            List<Statement> peeledStmts = bodyStmts.subList(2, bodyStmts.size());
            extractedMethod = extractMethod(method, peeledStmts, "getTripsByUserInner");
            klass.addMember(extractedMethod);
            extractedMethod.resolve();
            storyBoard.addFrame("# Extract method 'getTripsByUserInner'", klass.toString());

        }
        IfStmt ifStmt = extractedMethod.findFirst(IfStmt.class).get();
        ///
        {
            // flip if

            Statement thenStmt = ifStmt.getThenStmt();
            ifStmt.setThenStmt(ifStmt.getElseStmt().get());
            ifStmt.setElseStmt(thenStmt);
            Expression condition = ifStmt.getCondition();
            ifStmt.setCondition(new UnaryExpr(condition, UnaryExpr.Operator.LOGICAL_COMPLEMENT));
            storyBoard.addFrame("# Invert if", klass.toString());
        }
        {
            // breakout else
            Statement elseStmt = ifStmt.getElseStmt().get();
            NodeList<Statement> statementsContainingIf = ifStmt.findAncestor(BlockStmt.class).get().getStatements();
            statementsContainingIf.addAfter(elseStmt.clone(), ifStmt);
            elseStmt.remove();
            storyBoard.addFrame("# Breakout else", klass.toString());
        }

        {
            RefactorEngine.collapseNestedBlocks(extractedMethod);
            storyBoard.addFrame("# Flatten blocks", klass.toString());
        }
        // move isfriend down
        final BlockStmt blockStmt;
        final ExpressionStmt isFriendDeclStmt;
        int declIndex = -1;
        {
            VariableDeclarationExpr isFriendDecl = extractedMethod.findFirst(VariableDeclarationExpr.class, (n) -> n.getVariable(0).getName().getIdentifier().equals("isFriend")).get();
            isFriendDeclStmt = isFriendDecl.findAncestor(ExpressionStmt.class).get();
            blockStmt = isFriendDeclStmt.findAncestor(BlockStmt.class).get();

            declIndex = blockStmt.getStatements().indexOf(isFriendDeclStmt);
            Statement nextStatement = blockStmt.getStatements().get(declIndex + 1);
            blockStmt.getStatements().set(declIndex + 1, isFriendDeclStmt);
            blockStmt.getStatements().set(declIndex, nextStatement);
            storyBoard.addFrame("# Push isFriend down", klass.toString());
        }
        // extract isFriend
        {
            List<Statement> statementsToExtract = blockStmt.getStatements().subList(declIndex + 1, declIndex + 3);
            MethodDeclaration extractedMethod2 = extractMethod(extractedMethod, statementsToExtract, "isFriendOf");
            klass.addMember(extractedMethod2);
            storyBoard.addFrame("# Extract method 'isFriendOf'", klass.toString());
        }
        System.out.println(klass.toString());
        System.out.println(new YamlPrinter(true).output(method));
        StringLiteralExpr replacement = new StringLiteralExpr("hello");
//        RefactorEngine.replaceFieldAccess(method, "a", "field", replacement);
//        Approvals.verify(method);
        storyBoard.close();
    }
    static class Ref {
        private final ResolvedType type;
        private final String typeString;
        private final NameExpr nameExpr;
        private final ResolvedDeclaration declaration;

        Ref(ResolvedType type, String typeString, NameExpr nameExpr, ResolvedDeclaration declaration) {

            this.type = type;
            this.typeString = typeString;
            this.nameExpr = nameExpr;
            this.declaration = declaration;
        }

        @Override
        public String toString() {
            return ToStringBuilder.reflectionToString(this, ToStringStyle.SIMPLE_STYLE);
        }
    }
    private static MethodDeclaration extractMethod(MethodDeclaration method, List<Statement> statementsToExtract, String extractName) {
        Statement firstStmt = statementsToExtract.get(0);
        Position blockBegin = firstStmt.getBegin().get();
        Statement lastStmt = statementsToExtract.get(statementsToExtract.size() - 1);
        Position blockEnd = lastStmt.getEnd().get();
        System.out.println(blockBegin + " >> " + firstStmt);
        System.out.println(blockEnd + " >> " + lastStmt);

        List<Ref> refs = collectRefs(statementsToExtract.stream());
        System.out.println("METHOD");
        System.out.println(method);
//        System.out.println("To Extract:");
//        statementsToExtract.forEach(System.out::println);
//        System.out.println("___");

        List<Ref> laterReferences = collectRefs(method.stream().filter(n ->
                n.getBegin().isPresent() && n.getBegin().get().isAfterOrEqual(blockEnd)));
        List<Ref> laterRefsToDeclaredInRange = laterReferences.stream()
                .filter((ref) ->
                    ref.declaration.toAst().isPresent() &&
                     statementsToExtract.stream().anyMatch(s-> ref.declaration.toAst().get().isDescendantOf(s))
                )
                .collect(Collectors.toList());
        List<Ref> refsDeclaredOutside = refs.stream()
                .filter((ref) ->
                    ref.declaration.toAst().isPresent() &&
                            !statementsToExtract.stream().anyMatch(s-> ref.declaration.toAst().get().isDescendantOf(s)))
                .collect(Collectors.toList());
        System.out.println("refs (all): " + refs);
        System.out.println("laterRefs (all): " + laterReferences);
        System.out.println("laterRefsToDeclaredInRange: " + laterRefsToDeclaredInRange);
        Set<Node> refRangeSet = new HashSet<>();
        List<Ref> outputRefsDeduped = dedupeRefs(laterRefsToDeclaredInRange, refRangeSet);
        List<Ref> inputRefs = dedupeRefs(refsDeclaredOutside, refRangeSet);
        System.out.println("laterRefsToDeclaredInRange: " + outputRefsDeduped.size());
        System.out.println("refsDeclaredOutside " + refsDeclaredOutside);
        System.out.println("inputRefs " + inputRefs);
        String returnType = "";
        boolean blockHasReturn = false;
        List<ReturnStmt> returnStmts = statementsToExtract.stream()
                .flatMap(stmt -> stmt.findAll(ReturnStmt.class).stream()).collect(Collectors.toList());

        String varName = "applesauce";
        if (returnStmts.isEmpty()) {
            if (outputRefsDeduped.size() > 1) {
                throw new RuntimeException("Cannot extract, more than one out var");
            }
            if (outputRefsDeduped.size() == 0) {
                throw new RuntimeException("TODO: Implement zero out vars");
            }
            Ref outRef = outputRefsDeduped.get(0);
            varName = outRef.nameExpr.getName().getIdentifier();
            returnType = outRef.typeString;

        } else {
            blockHasReturn = true;
            // Could require unifying?
            returnType = method.getType().toString();
            if (outputRefsDeduped.size() != 0) {
                throw new RuntimeException("There is both an outvar and a return");
            }
        }

        var extractedStmts = new NodeList<>(statementsToExtract);

        statementsToExtract.clear();

        MethodCallExpr extractedCall = new MethodCallExpr().setName(extractName);
        for (Ref ref : inputRefs) {
            extractedCall.addArgument(ref.nameExpr.getName().getIdentifier());
        }

        System.out.println("Return type: " + returnType);

        MethodDeclaration extractedMethod = new MethodDeclaration()
                .setName(extractName)
                .setType(returnType);
        for (Ref ref : inputRefs) {
            System.out.println(ref.typeString);
            extractedMethod.addParameter(ref.typeString, ref.nameExpr.getName().getIdentifier());

        }
        if (!blockHasReturn) {
            extractedStmts.add(new ReturnStmt(new NameExpr(varName)));
        }

        extractedMethod.setBody(new BlockStmt(extractedStmts));
        if (blockHasReturn) {
            statementsToExtract.add(new ReturnStmt(extractedCall));
        } else {
            VariableDeclarator varDecl = new VariableDeclarator().setType(returnType).setInitializer(extractedCall).setName(varName);
            VariableDeclarationExpr varDeclExpr = new VariableDeclarationExpr(varDecl);
            statementsToExtract.add(new ExpressionStmt(varDeclExpr));
        }

        return extractedMethod;
//    }
        //        Set<ResolvedDeclaration> outgoingDeclarations = laterRefsToDeclaredInRange.stream().map((Ref ref) -> ref.declaration.toAst().get().getRange().get().toString()).collect(Collectors.toSet());
//        System.out.println(outgoingDeclarations.size());

//        Range range = declaration.toAst().get().getRange().get();
//        if (range.isBefore(blockBegin) || range.isAfter(blockEnd)) {
//            System.out.println("Declared outside");
//
//            externalRefs.add());
//        }


    }

//    private static String resolveTypeString(Expression n) {
//        try {
//            ResolvedType resolvedType = n.calculateResolvedType();
//
//            return resolvedType.describe();
////                    System.out.println(n.getName().getIdentifier() + " :: " + resolvedType);
//        } catch (UnsolvedSymbolException e) {
//            return e.getName().replace("Solving ", "");
//        }
//    }

    private static List<Ref> dedupeRefs(List<Ref> laterRefsToDeclaredInRange, Set<Node> refRangeSet) {
        return laterRefsToDeclaredInRange.stream().filter(ref -> refRangeSet.add(ref.declaration.toAst().get())).collect(Collectors.toList());
    }

    private static Range getRange(Ref ref) {
        return ref.declaration.toAst().get().getRange().get();
    }

    private static List<Ref> collectRefs(Stream<? extends Node> nodeStream) {
        return nodeStream.flatMap(s -> s.findAll(NameExpr.class).stream().map(n -> {
            ResolvedType resolvedType = null;
            String typeString;
            if (n.getName().getIdentifier().equals("tripDao")) {
                n.getName();
            }
            try {
                resolvedType = s.getSymbolResolver().calculateType(n);
                typeString = resolvedType.describe();
//                    System.out.println(n.getName().getIdentifier() + " :: " + resolvedType);
            } catch (UnsolvedSymbolException e) {
                typeString = e.getName().replace("Solving ", "");
                System.out.println(n.getName().getIdentifier() + " :: " + e.getName());
            }

            ResolvedDeclaration declaration = s.getSymbolResolver().resolveDeclaration(n, ResolvedDeclaration.class);
            return new Ref(resolvedType, typeString, n, declaration);
        })).collect(Collectors.toList());
    }

    private static Optional<Node> findDeclarationNode(Statement scope, NameExpr nameExpr) {
        ResolvedDeclaration decl = scope.getSymbolResolver().resolveDeclaration(nameExpr, ResolvedDeclaration.class);
        if (decl != null) {
            return decl.toAst();
        }
        return Optional.empty();
    }

    private static Optional<MethodCallExpr> findFirstMatchingMethodCall(MethodDeclaration method, String methodName, String scope) {
        return method.findFirst(MethodCallExpr.class,
                (m) -> m.getName().getIdentifier().equals(methodName) &&
                        m.getScope().isPresent() &&
                        m.getScope().get().toString().equals(scope)
        );
    }

    private static List<NameExpr> findMatchingNameExprs(MethodDeclaration method, String name) {
        return method.findAll(NameExpr.class,
                (n) -> n.getName().getIdentifier().equals(name)
        );
    }

    private static ClassOrInterfaceDeclaration parseCodeAndGetClass(String code, String className) {
        JavaParser javaParser = new JavaParser();
        // Set up a minimal type solver that only looks at the classes used to run this sample.
        CombinedTypeSolver combinedTypeSolver = new CombinedTypeSolver();
        combinedTypeSolver.add(new ReflectionTypeSolver());

        // Configure JavaParser to use type resolution
        JavaSymbolSolver symbolSolver = new JavaSymbolSolver(combinedTypeSolver);
        javaParser.getParserConfiguration().setSymbolResolver(symbolSolver);

        ParseResult<CompilationUnit> parseResult = javaParser.parse(code);
        CompilationUnit compilationUnit = parseResult.getResult().get();

        return compilationUnit.getClassByName(className).get();
    }
}
