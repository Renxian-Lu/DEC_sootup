package analysis.exercise1;

import analysis.AbstractAnalysis;
import analysis.VulnerabilityReporter;

import java.util.List;
import java.util.Objects;
import javax.annotation.Nonnull;

import sootup.core.jimple.basic.Immediate;
import sootup.core.jimple.basic.Value;
import sootup.core.jimple.common.constant.StringConstant;
import sootup.core.jimple.common.expr.AbstractInvokeExpr;
import sootup.core.jimple.common.expr.JStaticInvokeExpr;
import sootup.core.jimple.common.expr.JVirtualInvokeExpr;
import sootup.core.jimple.common.stmt.JAssignStmt;
import sootup.core.jimple.common.stmt.JInvokeStmt;
import sootup.core.jimple.common.stmt.Stmt;
import sootup.core.model.SootClass;
import sootup.core.model.SootMethod;
import sootup.core.signatures.MethodSignature;
import sootup.core.types.ClassType;
import sootup.java.core.JavaSootMethod;

public class MisuseAnalysis extends AbstractAnalysis {
    public MisuseAnalysis(@Nonnull JavaSootMethod method, @Nonnull VulnerabilityReporter reporter) {
        super(method, reporter);
    }

    @Override
    protected void flowThrough(@Nonnull Stmt stmt) {

        // TODO: Implement your analysis here.
        if (stmt instanceof JAssignStmt) {
            JAssignStmt assignStmt = (JAssignStmt) stmt;
            Value rightOp = assignStmt.getRightOp();

            if (rightOp instanceof JStaticInvokeExpr) {
                JStaticInvokeExpr staticInvokeExpr = (JStaticInvokeExpr) rightOp;
                MethodSignature methodSig = staticInvokeExpr.getMethodSignature();
                // Check if the method is Cipher.getInstance
                if ("javax.crypto.Cipher".equals(methodSig.getDeclClassType().toString()) && "getInstance".equals(methodSig.getName())) {
                    List<Immediate> args = staticInvokeExpr.getArgs();
                    if (!args.isEmpty()) {
                        StringConstant arg = (StringConstant) args.get(0);
                        // Check if the argument is "AES"
                        if ("AES".equals(arg.getValue())) {
                            // Report the vulnerability
                            reporter.reportVulnerability(method.getSignature(), stmt);
                        }
                    }
                }
            }
        }
    }
}
