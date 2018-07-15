package gov.nasa.jpf.symbc.veritesting.VeritestingAST.WALAInstructions;

import com.ibm.wala.ssa.SSAInvokeInstruction;
import com.ibm.wala.ssa.SSANewInstruction;
import gov.nasa.jpf.symbc.veritesting.VeritestingAST.Expressions.Expr;
import gov.nasa.jpf.symbc.veritesting.VeritestingAST.Expressions.VarExpr;
import gov.nasa.jpf.symbc.veritesting.VeritestingAST.Expressions.WalaVarExpr;
import gov.nasa.jpf.symbc.veritesting.VeritestingAST.Visitors.AstVisitor;

public class InvokeInstruction extends Instruction {
    public final VarExpr [] result;
    public final Expr [] params;

    public InvokeInstruction(SSAInvokeInstruction ins, VarExpr [] result, Expr [] params)
    {
        super(ins);
        this.result = result;
        this.params = params;
    }

    public InvokeInstruction(SSAInvokeInstruction ins)
    {
        super(ins);
        result = new VarExpr [ins.getNumberOfReturnValues()];
        for (int i = 0; i < ins.getNumberOfReturnValues(); i++) {
            result[i] = new WalaVarExpr(ins.getReturnValue(i));
        }
        this.params = new Expr[ins.getNumberOfParameters()];
        for (int i = 0; i < ins.getNumberOfParameters(); i++) {
            result[i] = new WalaVarExpr(ins.getUse(i));
        }
    }

    @Override
    public <T, S extends T> T accept(AstVisitor<T, S> visitor) {
        return visitor.visit(this);
    }
}