package josh.shell;

public class Range {
    public int start;
    public int end;

    public Range(int tokenStart, int tokenEnd) {
        this.start = tokenStart;
        this.end = tokenEnd;
    }

    @Override
    public String toString() {
        return "[" + start + "," + end + ']';
    }
}
