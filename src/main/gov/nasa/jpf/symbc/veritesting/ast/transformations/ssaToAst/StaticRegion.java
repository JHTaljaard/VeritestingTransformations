package gov.nasa.jpf.symbc.veritesting.ast.transformations.ssaToAst;

import com.ibm.wala.ssa.IR;
import gov.nasa.jpf.symbc.veritesting.ast.def.Stmt;

import java.util.*;


public class StaticRegion {
    private Stmt staticStmt;
    public final IR ir;
    protected StackSlotTable stackSlotTable;
    public final ArrayList<Integer> outputVars;

    public StaticRegion(Stmt staticStmt, IR ir){
        this.staticStmt = staticStmt;
        this.ir  = ir;
        stackSlotTable = new StackSlotTable(ir);
        outputVars = computeOutputVars();

    }

    //SH: outputVars are computed by finding the maximum wala var for each
    //stackSlot.
    private ArrayList<Integer> computeOutputVars() {
        ArrayList<Integer> outputVars = new ArrayList<>();
        HashSet<Integer> allSlots = stackSlotTable.getSlots();
        Iterator<Integer> slotsIter = allSlots.iterator();
        while(slotsIter.hasNext()){
            HashSet<Integer> varsForSlot = stackSlotTable.getVarsOfSlot(slotsIter.next());
            outputVars.add(Collections.max(varsForSlot));
        }
        return outputVars;
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

    public void printOutputVar() {
        System.out.println("\nRegion Output Vars:");
        outputVars.forEach(var -> System.out.println(var ));
    }
}
