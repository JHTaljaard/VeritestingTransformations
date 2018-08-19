package gov.nasa.jpf.symbc.veritesting.ast.transformations.ssaToAst;

import gov.nasa.jpf.symbc.veritesting.ast.def.WalaVarExpr;
import gov.nasa.jpf.symbc.veritesting.ast.transformations.Environment.InputTable;
import gov.nasa.jpf.symbc.veritesting.ast.transformations.Environment.SlotParamTable;
import gov.nasa.jpf.symbc.veritesting.ast.visitors.ExprMapVisitor;
import gov.nasa.jpf.symbc.veritesting.ast.visitors.ExprVisitor;
import za.ac.sun.cs.green.expr.Expression;

import java.util.ArrayList;

/**
 * An Expression Boundary Visitor that attempts to discover first "use" var inside a region.
 */
public class ExprBoundaryVisitor extends ExprMapVisitor implements ExprVisitor<Expression> {

    private boolean seenFirstUse = false;
    private int firstUse;

    public ExprBoundaryVisitor() { }

    @Override
    public Expression visit(WalaVarExpr expr) {
        if(seenFirstUse){
            if (expr.number < firstUse){
                firstUse = expr.number;
            }
        }
        else{
            firstUse = expr.number;
            seenFirstUse = true;
        }
        return expr;
    }

    public int getFirstUse() {
        return firstUse;
    }
}
