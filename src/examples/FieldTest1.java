public class FieldTest1 extends TestRegionBaseClass {

    int testFunction(int in0, int in1, int in2, int in3, int in4, int in5,
                     boolean b0, boolean b1, boolean b2, boolean b3, boolean b4, boolean b5) {
        return fieldTest1(in0);
    }

    int count = 0;

    public int fieldTest1(int x) {
        if (x != 0) count = 1;
//        assert( x != 0 ? count == 1 : true);
        return count;
    }

    public static void main(String[] args) {
        TestVeritesting t = new TestVeritesting();
        FieldTest1 s = new FieldTest1();
        t.runTest(s);
    }
}
