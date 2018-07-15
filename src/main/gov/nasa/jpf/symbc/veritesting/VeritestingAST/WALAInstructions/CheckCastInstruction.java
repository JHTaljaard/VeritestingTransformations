package gov.nasa.jpf.symbc.veritesting.VeritestingAST.WALAInstructions;

import com.ibm.wala.ssa.SSACheckCastInstruction;
import com.ibm.wala.types.TypeReference;
import gov.nasa.jpf.symbc.veritesting.VeritestingAST.Expressions.Expr;
import gov.nasa.jpf.symbc.veritesting.VeritestingAST.Expressions.VarExpr;
import gov.nasa.jpf.symbc.veritesting.VeritestingAST.Expressions.WalaVarExpr;
import gov.nasa.jpf.symbc.veritesting.VeritestingAST.Visitors.AstVisitor;

public class CheckCastInstruction extends Instruction {

    public final VarExpr result;
    public final Expr val;
    public final TypeReference [] declaredResultTypes;

    public CheckCastInstruction(SSACheckCastInstruction ins, VarExpr result, Expr val, TypeReference [] declaredResultTypes) {
        super(ins);
        this.result = result;
        this.val = val;
        this.declaredResultTypes = declaredResultTypes;
    }

    public CheckCastInstruction(SSACheckCastInstruction ins) {
        super(ins);
        result = new WalaVarExpr(ins.getDef());
        val = new WalaVarExpr(ins.getUse(0));
        declaredResultTypes = ins.getDeclaredResultTypes();
    }

    @Override
    public <T, S extends T> T accept(AstVisitor<T, S> visitor) {
        return visitor.visit(this);
    }
}