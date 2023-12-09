package analysis.exercise2;

import analysis.FileStateFact;
import analysis.ForwardAnalysis;
import analysis.VulnerabilityReporter;

import java.util.*;
import java.util.stream.Collectors;

import sootup.core.jimple.basic.Local;
import sootup.core.jimple.basic.Value;
import sootup.core.jimple.common.expr.AbstractInstanceInvokeExpr;
import sootup.core.jimple.common.expr.AbstractInvokeExpr;
import sootup.core.jimple.common.expr.JSpecialInvokeExpr;
import sootup.core.jimple.common.expr.JVirtualInvokeExpr;
import sootup.core.jimple.common.ref.JStaticFieldRef;
import sootup.core.jimple.common.stmt.*;
import sootup.core.signatures.FieldSignature;
import sootup.core.signatures.MethodSignature;
import sootup.core.types.Type;
import sootup.java.core.JavaSootMethod;


import javax.annotation.Nonnull;

public class TypeStateAnalysis extends ForwardAnalysis<Set<FileStateFact>> {

    public TypeStateAnalysis(@Nonnull JavaSootMethod method, @Nonnull VulnerabilityReporter reporter) {
        super(method, reporter);
        // System.out.println(method.getBody());
    }

    @Override
    protected void flowThrough(@Nonnull Set<FileStateFact> in, @Nonnull Stmt stmt, @Nonnull Set<FileStateFact> out) {
        copy(in, out);
        // TODO: Implement your flow function here.

        /*
         * 1. create set for alias as filestatefact
         * 2. put alias inside this set
         * 3. update state according to alias
         * */

        if (stmt instanceof JAssignStmt) {
            JAssignStmt assignStmt = (JAssignStmt) stmt;
            Value leftOp = assignStmt.getLeftOp();
            Value rightOp = assignStmt.getRightOp();

            // Check if rightOp is already tracked in facts
            for (FileStateFact fact : out) {
                if (fact.containsAlias(rightOp)) {
                    // If rightOp is tracked, add leftOp as an alias
                    fact.addAlias(leftOp);
                    break;
                }
            }
        }

        if (stmt instanceof JInvokeStmt) {
            JInvokeStmt invokeStmt = (JInvokeStmt) stmt;

            AbstractInstanceInvokeExpr invokeExpr = (AbstractInstanceInvokeExpr) invokeStmt.getInvokeExpr();
            String methodName = invokeExpr.getMethodSignature().getName();

            // Assuming 'file' is the local variable referring to the File object.
            Value fileValue = invokeExpr.getBase();

            switch (methodName) {
                case "open":
                    updateState(out, fileValue, FileStateFact.FileState.Open);
                    break;
                case "close":
                    updateState(out, fileValue, FileStateFact.FileState.Close);
                    break;
                default:
                    updateState(out, fileValue, FileStateFact.FileState.Init);
                    // Add other cases as needed.
            }
        }

        if (stmt instanceof JReturnStmt || stmt instanceof JReturnVoidStmt) {
            for (FileStateFact fact : out) {
                if (fact.getState() == FileStateFact.FileState.Open) {
                    reporter.reportVulnerability(method.getSignature(), stmt);
                }
            }
        }

        prettyPrint(in, stmt, out);
        // Other types of statements can be processed here if necessary.
    }

    private void updateState(Set<FileStateFact> facts, Value fileValue, FileStateFact.FileState newState) {
        // Find existing facts that contain the fileValue or its aliases

        // Find all FileStateFacts that have any alias matching fileValue or its known aliases
        FileStateFact relatedFact = null;
        for (FileStateFact fact : facts) {
            if (fact.containsAlias(fileValue)) {
                relatedFact = fact;
                break;
            }
        }

        if (relatedFact != null) {
            // Update the state of the existing fact and add the new alias if necessary
            relatedFact.updateState(newState);
            if (!relatedFact.containsAlias(fileValue)) {
                relatedFact.addAlias(fileValue);
            }
        } else {
            // If no existing fact for this file, create a new one
            FileStateFact newFact = new FileStateFact(newState);
            newFact.addAlias(fileValue);
            facts.add(newFact);
        }

    }

    @Nonnull
    @Override
    protected Set<FileStateFact> newInitialFlow() {
        // TODO: Implement your initialization here.
        // The following line may be just a place holder, check for yourself if
        // it needs some adjustments.
        return new HashSet<>();
    }

    @Override
    protected void copy(@Nonnull Set<FileStateFact> source, @Nonnull Set<FileStateFact> dest) {
        // TODO: Implement the copy function here.
//		dest.clear();
        for (FileStateFact fsf : source) {
            dest.add(new FileStateFact(fsf));
        }

    }

    @Override
    protected void merge(@Nonnull Set<FileStateFact> in1, @Nonnull Set<FileStateFact> in2, @Nonnull Set<FileStateFact> out) {
        // TODO: Implement the merge function here.
        out.clear();
        out.addAll(in1);
        out.addAll(in2); // Assumes that FileStateFact objects can be meaningfully combined.
    }

}
