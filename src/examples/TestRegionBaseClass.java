public abstract class TestRegionBaseClass {
    // Classes that wish to test a veritesting region should put the region inside a method and call it from a
    // overridden testFunction
    abstract Outputs testFunction(int in0, int in1, int in2, int in3, int in4, int in5,
                                  boolean b0, boolean b1, boolean b2, boolean b3, boolean b4, boolean b5,
                                  char c0, char c1, char c2, char c3, char c4, char c5);
}