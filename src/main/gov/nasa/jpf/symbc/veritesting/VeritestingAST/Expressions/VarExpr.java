package gov.nasa.jpf.symbc.veritesting.VeritestingAST.Expressions;

import gov.nasa.jpf.symbc.veritesting.VeritestingAST.Visitors.ExprVisitor;

public interface VarExpr {

    public abstract <T> T accept(ExprVisitor<T> visitor);

}