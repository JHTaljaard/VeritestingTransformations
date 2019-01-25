package gov.nasa.jpf.symbc.veritesting.RangerDiscovery;

import gov.nasa.jpf.symbc.veritesting.VeritestingUtil.Pair;
import gov.nasa.jpf.symbc.veritesting.ast.def.FieldRef;
import gov.nasa.jpf.symbc.veritesting.ast.def.FieldRefVarExpr;
import gov.nasa.jpf.symbc.veritesting.ast.transformations.Environment.DynamicRegion;
import gov.nasa.jpf.symbc.veritesting.ast.transformations.fieldaccess.SubscriptPair;
import gov.nasa.jpf.vm.*;
import za.ac.sun.cs.green.expr.Expression;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.*;

public class DiscoverContract {

    /* This map is used to populate the spfQuery for z3, for later mainpulation. */
    public static ArrayList<Pair> z3QuerySet = new ArrayList<>();

    public static String contractMethodName;

    private static ArrayList<String> freeInput = new ArrayList<>();
    private static ArrayList<String> stateInput = new ArrayList<>();
    private static ArrayList<String> stateOutput = new ArrayList<>();
    private static ArrayList<String> intermediateVar = new ArrayList<>();

    private static ArrayList<String> jkindInVar = new ArrayList<>();
    private static ArrayList<String> jkindOutVar = new ArrayList<>();

    public static DynamicRegion dynRegion;



    public static String generateKMerge(String query, ArrayList z3FunDecSet, String fileName){
        
        //TODO
        discoverJKindVar();
        String rangerTransition = generateRangerTransition(query, z3FunDecSet, fileName);
        ArrayList<Pair<String, String>> inputMapping = generateMapping(jkindInVar, stateInput);
        ArrayList<Pair<String, String>> outputMapping = generateMapping(jkindOutVar, stateOutput);
        String contractAssertion = generateContractAssertion(inputMapping, outputMapping);

        return null;

    }

    private static ArrayList<Pair<String, String>> generateMapping(ArrayList<String> jkindInVar, ArrayList<String> stateInput) {
        //TODO
        return null;
    }


    private static void discoverJKindVar() { //this really should automatically collect the inputs from the jkind file.
        jkindInVar.add("sig");
        jkindInVar.add("ignition");
        jkindInVar.add("reset_flag");
        jkindInVar.add("start_bt_val~0.reset_flag$");
        jkindInVar.add("launch_bt_val~0.reset_flag");
        jkindInVar.add("start_bt_val~0.start_bt");
        jkindInVar.add("launch_bt_val~0.start_bt");
        jkindInVar.add("start_bt_val~0.launch_bt");
        jkindInVar.add("launch_bt_val~0.launch_bt");
        jkindInVar.add("start_bt_val~0.start_bt_out");
        jkindInVar.add("launch_bt_val~0.launch_bt_out");


        jkindOutVar.add("sig");
        jkindOutVar.add("ignition");
        jkindOutVar.add("reset_flag");
        jkindOutVar.add("start_bt_val~0.reset_flag$");
        jkindOutVar.add("launch_bt_val~0.reset_flag");
        jkindOutVar.add("start_bt_val~0.start_bt");
        jkindOutVar.add("launch_bt_val~0.start_bt");
        jkindOutVar.add("start_bt_val~0.launch_bt");
        jkindOutVar.add("launch_bt_val~0.launch_bt");
        jkindOutVar.add("start_bt_val~0.start_bt_out");
        jkindOutVar.add("launch_bt_val~0.launch_bt_out");
    }

    /**
     * used to generate a transition function R for discovery of the contract of the implementation.
     *
     * @param query
     * @param z3FunDecSet
     * @param fileName
     * @return
     */
    public static String generateRangerTransition(String query, ArrayList z3FunDecSet, String fileName) {
        intermediateVar = z3FunDecSet;
        collectOutput();
        dumpInputOutput(fileName);
        String transitionHeader = generateTransitionHeader();
        String body = generateBody(query);
        body = "(and \n \t(= symVar 1)\n" + body + ")";

        String rangerTransition = transitionHeader + body + ")";

        String instantiation0 = generateInstanitaion(0) +"\n";
        String instantiation1 = generateInstanitaion(1) +"\n";

        return rangerTransition +"\n" + instantiation0 + "\n" + instantiation1;
    }



    private static String generateInstanitaion(int i) {
        String varInstance_i = declareVarInstance(i);
        String rInstance = declareRInstance(i);
        return varInstance_i + "\n" + rInstance;
    }

    private static String declareRInstance(int i) {
        String R = "(assert (R ";
        String varBinds = "";
        varBinds += generateBinds(freeInput, i);
        if(i==0)
            varBinds += generateBinds(stateInput, i);
        else
            varBinds += generateBinds(stateOutput, i-1);

        varBinds += generateBinds(stateOutput, i);
        varBinds += generateBinds(intermediateVar, i);

        return R + varBinds + " ))";
    }

    private static String generateBinds(ArrayList<String> arrayList, int i) {
        String bind = "";

        for (String varName : arrayList) {
            bind += " " + generateVarName(varName, i) ;
        }

        return bind;
    }

    private static String declareVarInstance(int i) {
        String declareInState_i = "";
        String declareFree_i;
        String declareOutput_i;
        String intermediateVar_i;
        if (i == 0)
            declareInState_i = declareVars_i(0, stateInput);

        declareFree_i = declareVars_i(i, freeInput);
        declareOutput_i = declareVars_i(i, stateOutput);
        intermediateVar_i = declareVars_i(i, intermediateVar);

        return declareFree_i + declareInState_i + declareOutput_i + intermediateVar_i;

    }

    private static String declareVars_i(int i, ArrayList<String> stateInput) {
        String inState = "";
        for (String varName : stateInput) {
            inState += "(declare-fun " + generateVarName(varName, i) + "() Int)\n";
        }
        return inState;
    }

    private static String generateVarName(String varName, int i){
        return varName+"$"+i;
    }

    private static void dumpInputOutput(String fileName) {
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(fileName + "FreeIN"), "utf-8"))) {
            writer.write(writeArrayList(freeInput));
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error : " + e);
        }

        try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(fileName + "StateIN"), "utf-8"))) {
            writer.write(writeArrayList(stateInput));
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error : " + e);
        }

        try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(fileName + "OUT"), "utf-8"))) {
            writer.write(writeArrayList(stateOutput));
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error : " + e);
        }

    }

    private static String writeArrayList(ArrayList<String> stringArrayList) {
        String s = "";
        for (int i = 0; i < stringArrayList.size(); i++)
            s += stringArrayList.get(i) + "\n";
        return s;
    }

    private static void collectOutput() {
        for (Iterator<Map.Entry<FieldRef, SubscriptPair>> fieldRefItr = dynRegion.psm.table.entrySet().iterator();
             ((Iterator) fieldRefItr).hasNext(); ) {

            Map.Entry<FieldRef, SubscriptPair> entry = fieldRefItr.next();
            stateOutput.add((new FieldRefVarExpr(entry.getKey(), entry.getValue())).toString());
        }
        Collections.sort(stateOutput);
    }

    private static String generateTransitionHeader() {
        String header = "(define-fun R (";
        String parameters = "";
        intermediateVar.removeAll(stateInput);
        intermediateVar.removeAll(stateOutput);
        intermediateVar.removeAll(freeInput);

        parameters += createParameters(freeInput);
        parameters += createParameters(stateInput);
        parameters += createParameters(stateOutput);
        parameters += createParameters(intermediateVar);
        
        header += parameters + ") Bool\n";

        return header;
    }




    private static String createParameters(ArrayList<String> inputArray) {
        String parameters = "";
        for (String varName : inputArray) {
            parameters += "(" + varName + " Int) ";
        }
        return  parameters;
    }


    private static String generateBody(String query) {
        assert (query.length() > 0);

        String constraints = new String();
        /*removing the outer solve*/
        query = query.substring(8, query.length() - 1);

        int startingIndex = 0;
        int endingIndex = query.length();
        while (startingIndex < endingIndex) {
            Pair startEndIndecies = findAssertion(query, startingIndex);

            startingIndex = (int) startEndIndecies.getFirst();
            int assertionEndIndex = (int) startEndIndecies.getSecond();

            String assertion = query.substring(startingIndex, assertionEndIndex + 1); //+1 because substring is not inclusive for the endIndex.
            constraints += "\t" + assertion + "\n";
            startingIndex = assertionEndIndex + 1;
        }
        return constraints;
    }



    public static String generateContractAssertion(ArrayList<Pair<String, String>> inputMapping, ArrayList<Pair<String, String>> outputMapping) {
        //TODO
        return null;

    }



    /**
     * This takes the starting index of an opening bracket for which we want to find a matching closing bracket. It returns the index of the closing bracket.
     *
     * @param query
     * @param startingIndex
     * @return
     */
    private static Pair findAssertion(String query, int startingIndex) {
        int closingIndex = 0;
        int bracket = 0;
        boolean closingBracketFound = false;
        boolean firstOpenBracketEncountered = false;
        int walkingIndex = startingIndex;

        /*This loop tries to find the index of the first opening bracket. At the end of the loop, the walkingIndex will have this index number.*/
        while (!firstOpenBracketEncountered) {
            char c = query.charAt(walkingIndex);
            if (c == '(')
                firstOpenBracketEncountered = true;
            else {
                ++walkingIndex;
            }
        }

        startingIndex = walkingIndex;
        while (!closingBracketFound) {
            char c = query.charAt(walkingIndex);
            if (c == '(')
                ++bracket;
            else if (c == ')')
                --bracket;

            if (bracket == 0) {
                closingBracketFound = true;
                closingIndex = walkingIndex;
            }
            ++walkingIndex;
        }

        return new Pair(startingIndex, closingIndex);
    }

    public static void collectInput(ThreadInfo ti, Expression[] params, String currClassName, DynamicRegion dynRegion) {
        StackFrame sf = ti.getTopFrame();
        collectParameterInput(sf, params, dynRegion);
        collectStateInput(ti, currClassName);
    }

    private static void collectStateInput(ThreadInfo ti, String currClassName) {
        ElementInfo objRef = null;
        Iterator<ElementInfo> heapItr = ti.getHeap().iterator();

        while (objRef == null & heapItr.hasNext()) {
            ElementInfo tempObj = heapItr.next();
            if (("L" + tempObj.getClassInfo().getName() + ";").equals(currClassName))
                objRef = tempObj;
        }


        if (objRef != null) {
            FieldInfo[] declaredFields = objRef.getClassInfo().getDeclaredInstanceFields();
            for (int i = 0; i < declaredFields.length; i++) {
                Object fieldSym = objRef.getFields().getFieldAttr(i);
                //if the field has a symbolic value then this is a symbolic state of the object that should be considered as an input
                if (fieldSym != null)
                    stateInput.add(fieldSym.toString());
            }
        }
        Collections.sort(stateInput);
    }


    private static void collectParameterInput(StackFrame sf, Expression[] params, DynamicRegion dynRegion) {
        // for now dealing with just a single input method, to generalize I need to add this into a for loop
        int[] slot = (int[]) dynRegion.slotParamTable.lookup(params[1]);

        if (slot != null && slot.length > 0) {
            gov.nasa.jpf.symbc.numeric.Expression sym =
                    (gov.nasa.jpf.symbc.numeric.Expression) sf.getLocalAttr(slot[0]);
            if (sym != null)
                freeInput.add(sym.toString());
        }
    }


    /**
     * used to generate SMT formate that is executed by z3 out of the box
     *
     * @param query
     * @param z3FunDecSet
     * @return
     */


    public static String toSMT(String query, HashSet z3FunDecSet) {
        assert (query.length() > 0);

        String newQuery = new String();
        /*removing the outer solve*/
        query = query.substring(8, query.length() - 1);

        int startingIndex = 0;
        int endingIndex = query.length();
        while (startingIndex < endingIndex) {
            Pair startEndIndecies = findAssertion(query, startingIndex);

            startingIndex = (int) startEndIndecies.getFirst();
            int assertionEndIndex = (int) startEndIndecies.getSecond();

            String assertion = query.substring(startingIndex, assertionEndIndex + 1); //+1 because substring is not inclusive for the endIndex.
            newQuery += "(assert " + assertion + ")\n";
            startingIndex = assertionEndIndex + 1;
        }

        newQuery = //"  (set-logic QF_BV)\n" +
                "  (set-option :produce-unsat-cores true)\n" +
                        generateFunDec(z3FunDecSet) +
                        newQuery;


        return newQuery;
    }

    private static String generateFunDec(HashSet<String> z3FunDecSet) {
        String funDec = "";
        for (String varName : z3FunDecSet) {
            funDec = funDec + "(declare-fun " + varName + " () Int)\n";
        }
        return funDec;
    }


}
