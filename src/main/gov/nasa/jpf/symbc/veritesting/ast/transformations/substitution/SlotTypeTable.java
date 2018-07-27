package gov.nasa.jpf.symbc.veritesting.ast.transformations.substitution;

import gov.nasa.jpf.symbc.veritesting.ast.transformations.ssaToAst.StackSlotTable;
import gov.nasa.jpf.symbc.veritesting.ast.transformations.ssaToAst.StaticRegion;
import gov.nasa.jpf.vm.StackFrame;

import java.lang.management.ThreadInfo;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;


//SH: this class populates the type of the stackslots. It should be called dynamically so that we can inquire SPF for the types of the stack slots.

public class SlotTypeTable extends Table<String> {
    StaticRegion staticRegion;
    gov.nasa.jpf.vm.ThreadInfo ti;

    public SlotTypeTable(gov.nasa.jpf.vm.ThreadInfo ti, StaticRegion staticRegion) {
        super("slot-type table","slot", "type");
        this.staticRegion = staticRegion;
        this.ti = ti;
        populateTypes();
    }

    private void populateTypes(){
        StackSlotTable stackSlotTable = staticRegion.stackSlotTable;
        StackFrame sf = ti.getTopFrame();
        HashSet<Integer> allSlots = stackSlotTable.getSlots();
        Iterator<Integer> slotsIter = allSlots.iterator();

        while(slotsIter.hasNext()){
            int slot = slotsIter.next();
            this.add(slot, sf.getLocalVariableType(slot));
        }
    }


    @Override
    public void print() {
        System.out.println("\nprinting " + tableName+" ("+ label1 + "->" + label2 +")");
        table.forEach((v1, v2) -> System.out.println(v1 + " --------- " + v2));
    }
}