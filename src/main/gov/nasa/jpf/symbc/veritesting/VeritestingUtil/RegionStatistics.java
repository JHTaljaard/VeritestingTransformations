package gov.nasa.jpf.symbc.veritesting.VeritestingUtil;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class RegionStatistics {
    public final String regionKey;
    ArrayList<FailEntry> failReasonList = new ArrayList<>();

    /**
     * counts the number of times we successfully summaries a veritesting region and placing it on the PC was SAT
     */
    public int veriHitNumber = 0;

    /**
     * counts the number of times we couldn't veritest a region and we left it for SPF to deal with it.
     */
    public int spfHitNumber = 0;

    /**
     * counts the number of times we hit a region and it was concrete
     */
    public int concreteNumber = 0;

    public long fixedPointTime = 0;

    RegionStatistics(String regionKey, FailEntry failEntry, int veriHitNumber, int spfHitNumber, int concreteNumber, long fixedPointTime) {
        this.regionKey = regionKey;
        this.concreteNumber = concreteNumber;
        if (failEntry != null)
            this.failReasonList.add(failEntry);
        this.veriHitNumber = veriHitNumber;
        this.spfHitNumber = spfHitNumber;
        this.fixedPointTime = fixedPointTime;
    }

    public String print() {
        String table = new String("\n*** Region Key = " + regionKey + "\n" +
                "veriHitNumber | spfHitNumber | concreteHit  |  fixedPointTime \n" + veriHitNumber + "      | " + spfHitNumber
                + "         | " + concreteNumber + "         | " +  TimeUnit.NANOSECONDS.toMillis(fixedPointTime) + " msec"
                + "\n-------------------------------------------");

        table += "\nFail Reasons\n";
        for (FailEntry fail : failReasonList) {
            table += fail.toString();
        }

        return table;
    }
}
