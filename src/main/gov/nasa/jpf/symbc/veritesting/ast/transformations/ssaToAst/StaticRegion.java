package gov.nasa.jpf.symbc.veritesting.ast.transformations.ssaToAst;

import com.ibm.wala.ssa.IR;
import gov.nasa.jpf.symbc.veritesting.ast.def.Stmt;


public class StaticRegion {
    private Stmt staticStmt;
    public final IR ir;
    protected StackSlotTable stackSlotTable;

    public StaticRegion(Stmt staticStmt, IR ir){
        this.staticStmt = staticStmt;
        this.ir  = ir;
        stackSlotTable = new StackSlotTable(ir);
    }

    public StackSlotTable getStackSlotTable() {
        return stackSlotTable;
    }

    public Stmt getStaticStmt() {
        return staticStmt;
    }


    public void setStaticStmt(Stmt staticStmt) {
        this.staticStmt = staticStmt;
    }
}
