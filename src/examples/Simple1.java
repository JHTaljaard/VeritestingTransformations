
class Simple1 extends TestRegionBaseClass {

    int testFunction(int in0, int in1, int in2, int in3, int in4, int in5,
                     boolean b0, boolean b1, boolean b2, boolean b3, boolean b4, boolean b5) {
        return simpleRegion(in0);
    }
    int simpleRegion(int x) {
        int count;
        if (x != 0) {
            count = 3;
        } else {
            count = 4;
        }
        return count;
    }

    public static void main(String[] args) {
        TestVeritesting t = new TestVeritesting();
        Simple1 s = new Simple1();
        t.runTest(s);
    }
}