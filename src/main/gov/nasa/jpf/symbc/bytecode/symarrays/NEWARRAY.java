/*
 * Copyright (C) 2014, United States Government, as represented by the
 * Administrator of the National Aeronautics and Space Administration.
 * All rights reserved.
 *
 * Symbolic Pathfinder (jpf-symbc) is licensed under the Apache License, 
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 *        http://www.apache.org/licenses/LICENSE-2.0. 
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and 
 * limitations under the License.
 */

package gov.nasa.jpf.symbc.bytecode.symarrays;




import gov.nasa.jpf.symbc.arrays.ArrayExpression;
import gov.nasa.jpf.symbc.numeric.*;
import gov.nasa.jpf.symbc.string.SymbolicLengthInteger;
import gov.nasa.jpf.symbc.veritesting.StaticRegionException;
import gov.nasa.jpf.vm.ChoiceGenerator;
import gov.nasa.jpf.vm.ClassInfo;
import gov.nasa.jpf.vm.ClassLoaderInfo;
import gov.nasa.jpf.vm.ElementInfo;
import gov.nasa.jpf.vm.Heap;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.ThreadInfo;
import za.ac.sun.cs.green.expr.IntVariable;

import java.util.ArrayList;
import java.util.Map;

import static gov.nasa.jpf.symbc.veritesting.AdapterSynth.SPFAdapterSynth.getVal;
import static gov.nasa.jpf.symbc.veritesting.VeritestingUtil.SpfUtil.maybeParseConstraint;

/**
 * Symbolic version of the NEWARRAY class from jpf-core. Has some extra code to
 * detect if a symbolic variable is being used as the size of the new array, and
 * treat it accordingly.
 * 
 * Someone with more experience should review this :)
 * TODO: to review; Corina: this code does not make too much sense: for now I will comment it out 
 * who wrote it?
 * 
 */

public class NEWARRAY extends gov.nasa.jpf.jvm.bytecode.NEWARRAY {

    private static final int c = 3;//TODO make this into a configuration option
    private static final boolean stopIfCExceeded = true; //TODO make this into a configuration option
    ArrayList<Long> values ;

	public NEWARRAY(int typeCode) {
    	super(typeCode);
    }
	
	@Override
	public Instruction execute( ThreadInfo ti) {
		/*
		StackFrame frame = ti.getModifiableTopFrame();

	    arrayLength = frame.pop();
	    Heap heap = ti.getHeap();

	    if (arrayLength < 0){
	      return ti.createAndThrowException("java.lang.NegativeArraySizeException");
	    }

	    // there is no clinit for array classes, but we still have  to create a class object
	    // since its a builtin class, we also don't have to bother with NoClassDefFoundErrors
	    String clsName = "[" + type;
	    ClassInfo ci = ClassLoaderInfo.getCurrentResolvedClassInfo(clsName);

	    if (!ci.isRegistered()) {
	      ci.registerClass(ti);
	      ci.setInitialized();
	    }
	   
	    if (heap.isOutOfMemory()) { // simulate OutOfMemoryError
	      return ti.createAndThrowException("java.lang.OutOfMemoryError",
	                                        "trying to allocate new " +
	                                          getTypeName() +
	                                        "[" + arrayLength + "]");
	    }
	    
	    ElementInfo eiArray = heap.newArray(type, arrayLength, ti);
	    int arrayRef = eiArray.getObjectRef();
	    
	    frame.pushRef(arrayRef);

	    return getNext(ti);
		*/
		// old code
	    // Corina: incorrect
	    
		StackFrame sf = ti.getModifiableTopFrame();
		Object attr = sf.getOperandAttr();
		ArrayExpression arrayAttr = null;
        PathCondition pc = null;;

		if(attr instanceof SymbolicLengthInteger) {
			long l = ((SymbolicLengthInteger) attr).solution;
			assert(l>=0 && l<=Integer.MAX_VALUE) : "Array length must be positive integer";
			arrayLength = (int) l;
			sf.pop();
		} else 	if(attr instanceof IntegerExpression) {

            ChoiceGenerator<?> cg = null;
            if (!ti.isFirstStepInsn()) {
                if (ti.getVM().getSystemState().getChoiceGenerator() instanceof PCChoiceGenerator) {
                    pc = ((PCChoiceGenerator) (ti.getVM().getSystemState().getChoiceGenerator())).getCurrentPC();
                } else {
                    pc = new PathCondition();
                    pc._addDet(Comparator.EQ, new IntegerConstant(0), new IntegerConstant(0));
                }
                assert pc != null;

                Map<String, Object> map = attr instanceof SymbolicInteger ?
                        pc.solveWithValuation((SymbolicInteger) attr, null) : null;
                String name = map != null ? ((SymbolicInteger) attr).getName() : null;
                Long lastValue = getVal(map, name);
                if (map == null || map.size() == 0 || lastValue == null) { //reached an unsat state
                    ti.getVM().getSystemState().setIgnored(true);
                    return getNext(ti);
                }
                values = new ArrayList<>();
                values.add(lastValue);
                while (c - values.size() > 0) {
                    pc._addDet(Comparator.NE, (IntegerExpression)attr, new IntegerConstant(lastValue));
                    map = pc.solveWithValuation((SymbolicInteger)attr, null);
                    lastValue = getVal(map, name);
                    if (map == null || map.size() == 0 || lastValue == null) break;
                    else values.add(lastValue);
                }
                if (values.size() == c && stopIfCExceeded) {
                    pc._addDet(Comparator.NE, (IntegerExpression)attr, new IntegerConstant(lastValue));
                    map = pc.solveWithValuation((SymbolicInteger)attr, null);
                    if (map == null || map.size() == 0 || lastValue == null) {
                        return ti.createAndThrowException("too many feasibe solutions for " + name);
                    }
                }
                cg = new PCChoiceGenerator(values.size());

                ti.getVM().setNextChoiceGenerator(cg);
                return this;
            } else {
                cg = ti.getVM().getSystemState().getChoiceGenerator();
                assert (cg instanceof PCChoiceGenerator) : "expected PCChoiceGenerator, got:" + cg;
                sf.pop();
                arrayLength = Math.toIntExact(values.get((Integer)cg.getNextChoice()));
                pc = ((PCChoiceGenerator) cg).getCurrentPC();
            }


            /*if ((Integer)cg.getNextChoice() == 0) {
                pc._addDet(Comparator.EQ, (IntegerExpression)attr, new IntegerConstant(1));
                if (pc.simplify()) {
                    ((PCChoiceGenerator) cg).setCurrentPC(pc);
//                    return ti.createAndThrowException("java.lang.NegativeArraySizeException");
                    sf.pop();
                    arrayLength = 1;
                } else {
                    ti.getVM().getSystemState().setIgnored(true);
                    return getNext(ti);
                }
            } else if ((Integer)cg.getNextChoice() == 1) {
                pc._addDet(Comparator.EQ, (IntegerExpression)attr, new IntegerConstant(0));
                if (pc.simplify()) {
                    ((PCChoiceGenerator) cg).setCurrentPC(pc);
                    sf.pop();
                    arrayLength = 0;
                } else {
                    ti.getVM().getSystemState().setIgnored(true);
                    return getNext(ti);
                }
            } else {
                pc._addDet(Comparator.GE, (IntegerExpression)attr, new IntegerConstant(2));
                if (pc.simplify()) {
                    System.out.println("arraylen >= 2 at " + getMethodInfo() + "#" + getPosition());
                }
                ti.getVM().getSystemState().setIgnored(true);
                return getNext(ti);
            }*/
        } else {
			arrayLength = sf.pop();
		}

		//the remainder of the code is identical to the parent class
		
	    Heap heap = ti.getHeap();

	    if (arrayLength < 0){
	      return ti.createAndThrowException("java.lang.NegativeArraySizeException");
	    }

	    // there is no clinit for array classes, but we still have  to create a class object
	    // since its a builtin class, we also don't have to bother with NoClassDefFoundErrors
	    String clsName = "[" + type;
	    
	    ClassInfo ci = ClassLoaderInfo.getCurrentResolvedClassInfo(clsName);
	    if (!ci.isRegistered()) {
	      ci.registerClass(ti);
	      ci.setInitialized();
	    }
	   
	    if (heap.isOutOfMemory()) { // simulate OutOfMemoryError
	      return ti.createAndThrowException("java.lang.OutOfMemoryError",
	                                        "trying to allocate new " +
	                                          getTypeName() +
	                                        "[" + arrayLength + "]");
	    }
	    
	    ElementInfo eiArray = heap.newArray(type, arrayLength, ti);
	    int arrayRef = eiArray.getObjectRef();
	    
	    sf.pushRef(arrayRef);
	    
        if (attr instanceof IntegerExpression) {
            arrayAttr = new ArrayExpression(eiArray.toString());
            pc._addDet(Comparator.EQ, arrayAttr.length, (IntegerExpression)attr);
            pc.arrayExpressions.put(arrayAttr.getRootName(), arrayAttr);
            try {
                maybeParseConstraint(pc);
            } catch (StaticRegionException e) {
                return ti.createAndThrowException("Failed to send arrayAttr.length constraint to solver");
            }
        }
        sf.setOperandAttr(arrayAttr);

	    ti.getVM().getSystemState().checkGC(); // has to happen after we push the new object ref
	    
	    return getNext(ti);
	    
	}
}
