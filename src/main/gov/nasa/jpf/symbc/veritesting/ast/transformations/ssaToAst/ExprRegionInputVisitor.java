package gov.nasa.jpf.symbc.veritesting.ast.transformations.ssaToAst;

import gov.nasa.jpf.symbc.veritesting.ast.def.WalaVarExpr;
import gov.nasa.jpf.symbc.veritesting.ast.transformations.Environment.InputTable;
import gov.nasa.jpf.symbc.veritesting.ast.transformations.Environment.SlotParamTable;
import gov.nasa.jpf.symbc.veritesting.ast.visitors.ExprMapVisitor;
import gov.nasa.jpf.symbc.veritesting.ast.visitors.ExprVisitor;
import za.ac.sun.cs.green.expr.Expression;

import java.util.HashMap;


/**
 * Conditional region input visitor that visits all use vars to collect a possible first use for every stack slot.
 */
public class ExprRegionInputVisitor extends ExprMapVisitor implements ExprVisitor<Expression> {

//SH:

    // maps seen stack slots to the smallest Wala variable number seen for that stack slot
    private HashMap<Integer, Integer> seenSlots;
    private InputTable inputTable;
    private SlotParamTable slotParamTable;
    public DefUseVisit defUseVisit;

    public ExprRegionInputVisitor(InputTable inputTable, SlotParamTable slotParamTable) {
        this.inputTable = inputTable;
        this.slotParamTable = slotParamTable;
        this.seenSlots = new HashMap();
    }

    @Override
    public Expression visit(WalaVarExpr expr) {
        int[] varSlots = slotParamTable.lookup(expr.number);
        if (varSlots != null) {
            for (int i = 0; i < varSlots.length; i++)
                if (seenBiggerWalaNum(varSlots[i], expr.number)) {
                    if (defUseVisit == DefUseVisit.USE)
                        inputTable.add(expr.number, varSlots[i]);
                    seenSlots.put(varSlots[i], expr.number);
                }
        }
        return expr;
    }

    private boolean seenBiggerWalaNum(int varSlot, int expNum) {
        if (!seenSlots.containsKey(varSlot)) return true;
        return seenSlots.get(varSlot) > expNum;
    }

}
