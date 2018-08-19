package gov.nasa.jpf.symbc.veritesting.ast.transformations.Uniquness;

import gov.nasa.jpf.symbc.veritesting.ast.def.FieldRefVarExpr;
import gov.nasa.jpf.symbc.veritesting.ast.def.WalaVarExpr;
import gov.nasa.jpf.symbc.veritesting.ast.transformations.Environment.DynamicRegion;
import gov.nasa.jpf.symbc.veritesting.ast.transformations.Environment.VarTypeTable;
import gov.nasa.jpf.symbc.veritesting.ast.visitors.ExprMapVisitor;
import gov.nasa.jpf.symbc.veritesting.ast.visitors.ExprVisitor;
import za.ac.sun.cs.green.expr.Expression;

import static gov.nasa.jpf.symbc.veritesting.VeritestingUtil.ExprUtil.createGreenVar;

/**
 * Unique Expression Visitor that ensures the uniqueness of vars used inside the region.
 */
public class ExpUniqueVisitor extends ExprMapVisitor implements ExprVisitor<Expression> {

    int uniqueNum;

    ExpUniqueVisitor(int uniqueNum) {
        super();
        this.uniqueNum = uniqueNum;
    }

    @Override
    public Expression visit(WalaVarExpr expr) {
        String varId = Integer.toString(expr.number);
        varId = varId.concat(Integer.toString(uniqueNum));
        return new WalaVarExpr(Integer.valueOf(varId));
    }

    @Override
    public Expression visit(FieldRefVarExpr expr) {
        String varId = Integer.toString(expr.subscript.globalSubscript);
        varId = varId.concat(Integer.toString(uniqueNum));
        expr.subscript.globalSubscript = Integer.valueOf(varId);
        return new FieldRefVarExpr(expr.fieldRef, expr.subscript);
    }
}
