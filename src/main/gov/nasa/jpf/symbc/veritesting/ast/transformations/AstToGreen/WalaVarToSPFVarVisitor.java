package gov.nasa.jpf.symbc.veritesting.ast.transformations.AstToGreen;

import gov.nasa.jpf.symbc.veritesting.ast.def.FieldRefVarExpr;
import gov.nasa.jpf.symbc.veritesting.ast.def.GammaVarExpr;
import gov.nasa.jpf.symbc.veritesting.ast.def.IfThenElseExpr;
import gov.nasa.jpf.symbc.veritesting.ast.def.WalaVarExpr;
import gov.nasa.jpf.symbc.veritesting.ast.transformations.Environment.VarTypeTable;
import gov.nasa.jpf.symbc.veritesting.ast.visitors.ExprVisitor;
import gov.nasa.jpf.symbc.veritesting.ast.visitors.ExprVisitorAdapter;
import za.ac.sun.cs.green.expr.*;

import static gov.nasa.jpf.symbc.veritesting.VeritestingUtil.ExprUtil.createGreenVar;

public class WalaVarToSPFVarVisitor implements ExprVisitor<Expression> {

    private final VarTypeTable varTypeTable;

    protected final ExprVisitorAdapter<Expression> eva =
            new ExprVisitorAdapter<Expression>(this);

    public WalaVarToSPFVarVisitor(VarTypeTable varTypeTable) {
        this.varTypeTable = varTypeTable;
    }

    @Override
    public Expression visit(IntConstant expr) {
        return expr;
    }

    @Override
    public Expression visit(IntVariable expr) {
        return expr;
    }

    @Override
    public Expression visit(Operation expr) {
        return expr;
    }

    @Override
    public Expression visit(RealConstant expr) {
        return expr;
    }

    @Override
    public Expression visit(RealVariable expr) {
        return expr;
    }

    @Override
    public Expression visit(StringConstantGreen expr) {
        return expr;
    }

    @Override
    public Expression visit(StringVariable expr) {
        return expr;
    }

    @Override
    public Expression visit(IfThenElseExpr expr) {
        return expr;
    }

    @Override
    public Expression visit(WalaVarExpr expr) {
        String type = varTypeTable.lookup(expr.number);
        if (type != null)
            return createGreenVar(type, expr.getSymName());
        else
            return expr;
    }

    @Override
    public Expression visit(FieldRefVarExpr expr) {
        return expr;
    }

    @Override
    public Expression visit(GammaVarExpr expr) {
        return new GammaVarExpr(eva.accept(expr.condition),
                eva.accept(expr.thenExpr),
                eva.accept(expr.elseExpr));
    }

}