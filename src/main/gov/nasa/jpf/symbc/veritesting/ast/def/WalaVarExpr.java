package gov.nasa.jpf.symbc.veritesting.ast.def;

import za.ac.sun.cs.green.expr.Variable;
import za.ac.sun.cs.green.expr.Visitor;
import za.ac.sun.cs.green.expr.VisitorException;

import java.util.List;

/**
 * This is the class of Wala Variables in RangerIR.
 */
public final class WalaVarExpr extends Variable {
    /**
     * This number matches the number defined for a specific Wala Variable.
     */
    public final int number;

    public WalaVarExpr(int var) {
        super("@w" + var);
        this.number = var;
    }


    @Override
    public void accept(Visitor visitor) throws VisitorException {
        visitor.preVisit(this);
        visitor.postVisit(this);
    }

    @Override
    public boolean equals(Object o) {
        if (o != null && o instanceof WalaVarExpr) {
            WalaVarExpr other = (WalaVarExpr)o;
            return this.number == other.number;
        }
        return false;
    }


    /**
     * Gets the symbolic name to be used for vars in SPF.
     * @return retrunds symbolic name, which is the name of the WalaVarExpr, without the @ sign
     */
    public String getSymName(){
        return "w"+ Integer.toString(number);
    }



    @Override
    public String toString() {
        return getName();
    }

    @Override
    public int getLength() {
        return 0;
    }

    @Override
    public int getLeftLength() {
        return 0;
    }

    @Override
    public int numVar() {
        return 0;
    }

    @Override
    public int numVarLeft() {
        return 0;
    }

    @Override
    public List<String> getOperationVector() {
        return null;
    }

    public WalaVarExpr clone(){
        return new WalaVarExpr(number);
    }
}
