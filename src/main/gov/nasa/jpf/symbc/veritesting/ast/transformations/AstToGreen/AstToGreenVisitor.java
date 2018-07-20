package gov.nasa.jpf.symbc.veritesting.ast.transformations.AstToGreen;

import gov.nasa.jpf.symbc.veritesting.ast.def.*;
import gov.nasa.jpf.symbc.veritesting.ast.visitors.ExprVisitorAdapter;
import za.ac.sun.cs.green.expr.Expression;
import za.ac.sun.cs.green.expr.Operation;


/*
    Preconditions:
        - Only statements are assignment statements and gamma statements

        - All IfThenElse expressions are in "normal form"
            - No IfThenElse/Gamma expressions embedded within expressions other than
              IfThenElse expressions
                e.g.: (if x then y else z) + (if a then b else c)
            - No IfThenElse expressions as conditions of other IfThenElse expressions:
                e.g.: (if (if x then y else z) then a else b)


     */

public class AstToGreenVisitor {

    ExprVisitorAdapter<Expression> eva;
    AstToGreenExprVisitor exprVisitor;

    public AstToGreenVisitor() {
        exprVisitor = new AstToGreenExprVisitor();
        eva = new ExprVisitorAdapter<>(exprVisitor);
    }

    public Expression bad(Object obj) {
        String name = obj.getClass().getCanonicalName();
        throw new IllegalArgumentException("Unsupported class: " + name +
                " value: " + obj.toString() + " seen in AstToGreenExprVisitor");
    }


    public Expression assignStmt(AssignmentStmt stmt) {
        exprVisitor.setAssign(stmt.lhs);
        return eva.accept(stmt.rhs);
    }

    public Expression compositionStmt(CompositionStmt stmt) {
        Expression lhs = transform(stmt.s1);
        Expression rhs = transform(stmt.s2);
        return new Operation(Operation.Operator.AND, lhs, rhs);
    }

    public Expression transform(Stmt stmt) {
        if (stmt instanceof AssignmentStmt) {
            return assignStmt((AssignmentStmt)stmt);
        } else if (stmt instanceof CompositionStmt) {
            return compositionStmt((CompositionStmt)stmt);
        } else {
            return bad(stmt);
        }
    }
}