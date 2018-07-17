package gov.nasa.jpf.symbc.veritesting.ast.visitors;

import gov.nasa.jpf.symbc.veritesting.ast.def.*;
import za.ac.sun.cs.green.expr.*;

public class AstMapVisitor extends ExprMapVisitor implements AstVisitor<Stmt> {

    ExprMapVisitor exprVisitor;
    ExprVisitorAdapter<Expression> eva;

    public AstMapVisitor(ExprMapVisitor exprVisitor) {
        this.eva = exprVisitor.eva;
        this.exprVisitor = exprVisitor;
    }
    @Override
    public Stmt visit(AssignmentStmt a) {
        return new AssignmentStmt((VarExpr)eva.accept(a.lhs), eva.accept(a.rhs));
    }

    @Override
    public Stmt visit(CompositionStmt a) {
        return new CompositionStmt(a.s1.accept(this), a.s2.accept(this));

    }

    @Override
    public Stmt visit(IfThenElseStmt a) {
        return new IfThenElseStmt(eva.accept(a.condition), a.thenStmt.accept(this), a.elseStmt.accept(this));
    }

    @Override
    public Stmt visit(SkipStmt a) {
        return a;
    }

    @Override
    public Stmt visit(SPFCaseStmt c) {
        return new SPFCaseStmt(eva.accept(c.spfCondition), c.reason);
    }

    @Override
    public Stmt visit(ArrayLoadInstruction c) {
        return new ArrayLoadInstruction(c.getOriginal(),
                (VarExpr)eva.accept(c.arrayref),
                (VarExpr)eva.accept(c.index),
                c.elementType,
                (VarExpr)eva.accept(c.def));
    }

    @Override
    public Stmt visit(ArrayStoreInstruction c) {
        return new ArrayStoreInstruction(c.getOriginal(),
                (VarExpr)eva.accept(c.arrayref),
                (VarExpr)eva.accept(c.index),
                c.elementType,
                eva.accept(c.assignExpr));
    }

    @Override
    public Stmt visit(SwitchInstruction c) {
        return c;
    }

    @Override
    public Stmt visit(ReturnInstruction c) {

        return new ReturnInstruction(c.getOriginal(), eva.accept(c.rhs));
    }

    @Override
    public Stmt visit(GetInstruction c) {
        return new GetInstruction(c.getOriginal(),
                (VarExpr)eva.accept(c.def),
                (VarExpr)eva.accept(c.ref),
                c.field);
    }

    @Override
    public Stmt visit(PutInstruction c) {
        return new PutInstruction(c.getOriginal(),
                (VarExpr)eva.accept(c.def),
                c.field,
                eva.accept(c.assignExpr));
    }

    @Override
    public Stmt visit(NewInstruction c) {
        return c;
    }

    @Override
    public Stmt visit(InvokeInstruction c) {
        VarExpr [] results = new VarExpr [c.result.length];
        for (int i=0; i < results.length; i++) {
            results[i] = (VarExpr)eva.accept(c.result[i]);
        }
        Expression [] params = new Expression [c.params.length];
        for (int i=0; i < params.length; i++) {
            params[i] = eva.accept(c.params[i]);
        }
        return new InvokeInstruction(c.getOriginal(), results, params);
    }

    @Override
    public Stmt visit(ArrayLengthInstruction c) {

        return new ArrayLengthInstruction(c.getOriginal(),
                (VarExpr)eva.accept(c.def),
                (VarExpr)eva.accept(c.arrayref));
    }

    @Override
    public Stmt visit(ThrowInstruction c) {
        return new ThrowInstruction(c.getOriginal());
    }

    @Override
    public Stmt visit(CheckCastInstruction c) {
        return new CheckCastInstruction(c.getOriginal(),
                (VarExpr)eva.accept(c.result),
                eva.accept(c.val),
                c.declaredResultTypes);
    }

    @Override
    public Stmt visit(InstanceOfInstruction c) {
        return new InstanceOfInstruction(c.getOriginal(),
                (VarExpr)eva.accept(c.result),
                eva.accept(c.val),
                c.checkedType);
    }

    @Override
    public Stmt visit(PhiInstruction c) {

        Expression [] rhs = new Expression[c.rhs.length];
        for (int i=0; i < rhs.length; i++) {
            rhs[i] = eva.accept(c.rhs[i]);
        }

        return new PhiInstruction(c.getOriginal(),
                (VarExpr)eva.accept(c.def),
                rhs);
    }

    @Override
    public Expression visit(IntConstant expr) {
        return null;
    }

    @Override
    public Expression visit(IntVariable expr) {
        return null;
    }

    @Override
    public Expression visit(Operation expr) {
        return null;
    }

    @Override
    public Expression visit(RealConstant expr) {
        return eva.accept(expr);
    }

    @Override
    public Expression visit(RealVariable expr) {
        return eva.accept(expr);
    }

    @Override
    public Expression visit(StringConstantGreen expr) {
        return eva.accept(expr);
    }

    @Override
    public Expression visit(StringVariable expr) {
        return eva.accept(expr);
    }

    @Override
    public Expression visit(WalaVarExpr expr) {
        return eva.accept((Expression)expr);
    }

    @Override
    public Expression visit(FieldRefVarExpr expr) {
        return eva.accept((Expression)expr);
    }

    @Override
    public Expression visit(GammaVarExpr expr) {
        return eva.accept((Expression)expr);
    }
}
