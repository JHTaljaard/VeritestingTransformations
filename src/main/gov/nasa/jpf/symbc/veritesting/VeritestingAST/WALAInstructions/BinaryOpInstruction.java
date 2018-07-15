package gov.nasa.jpf.symbc.veritesting.VeritestingAST.WALAInstructions;

import com.ibm.wala.shrikeBT.IBinaryOpInstruction;
import com.ibm.wala.ssa.SSABinaryOpInstruction;
import com.ibm.wala.ssa.SSAInstruction;
import com.ibm.wala.types.TypeReference;
import gov.nasa.jpf.symbc.veritesting.VeritestingAST.Expressions.Expr;
import gov.nasa.jpf.symbc.veritesting.VeritestingAST.Expressions.VarExpr;
import gov.nasa.jpf.symbc.veritesting.VeritestingAST.Expressions.WalaVarExpr;
import gov.nasa.jpf.symbc.veritesting.VeritestingAST.Visitors.AstVisitor;

public class BinaryOpInstruction extends Instruction {

    public final VarExpr def;
    public final Expr lhs;
    public final IBinaryOpInstruction.IOperator op;
    public final Expr rhs;

    public BinaryOpInstruction(SSAInstruction ins, VarExpr def, Expr lhs, IBinaryOpInstruction.IOperator op, Expr rhs) {
        super(ins);
        this.def = def;
        this.lhs = lhs;
        this.op = op;
        this.rhs = rhs;
    }

    public BinaryOpInstruction(SSABinaryOpInstruction ins) {
        super(ins);
        def = new WalaVarExpr(ins.getDef());
        lhs = new WalaVarExpr(ins.getUse(0));
        op = ins.getOperator();
        rhs = new WalaVarExpr(ins.getUse(1));
    }

    @Override
    public <T, S extends T> T accept(AstVisitor<T, S> visitor) {
        return visitor.visit(this);
    }
}
