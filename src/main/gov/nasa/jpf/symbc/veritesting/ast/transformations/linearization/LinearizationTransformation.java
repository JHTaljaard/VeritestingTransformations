package gov.nasa.jpf.symbc.veritesting.ast.transformations.linearization;

import gov.nasa.jpf.symbc.veritesting.ast.def.Stmt;
import gov.nasa.jpf.symbc.veritesting.ast.transformations.DefaultTransformation;
import gov.nasa.jpf.symbc.veritesting.ast.transformations.DynamicEnvironment.DynamicRegion;

public class LinearizationTransformation extends DefaultTransformation {

    @Override
    public DynamicRegion execute(DynamicRegion region) {
        LinearizationVisitor v = new LinearizationVisitor();
        Stmt stmt = region.dynStmt.accept(v);
        return new DynamicRegion(region.staticRegion,
                stmt,
                region.slotTypeTable,
                region.varTypeTable,
                region.valueSymbolTable,
                region.slotParamTable,
                region.outputTable,
                region.spfCaseSet);
    }
}
