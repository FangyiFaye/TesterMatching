import java.util.Comparator;

public class MatchResult implements Comparable<MatchResult> {
    private Tester tester;
    private int totalExpeirence;

    public MatchResult(Tester tester, int totalExpeirence) {
        this.tester = tester;
        this.totalExpeirence = totalExpeirence;
    }

    public Tester getTester() {
        return tester;
    }

    public int getTotalExpeirence() {
        return totalExpeirence;
    }

    /**
     * Implement Comparable in order to sort MatchResult
     * @param result
     * @return
     */
    @Override
    public int compareTo(MatchResult result) {
        if (this.totalExpeirence > result.totalExpeirence) {
            return 1;
        } else if (this.totalExpeirence < result.totalExpeirence) {
            return -1;
        } else {
            return 0;
        }
    }
}
