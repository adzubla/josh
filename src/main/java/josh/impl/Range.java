package josh.impl;

public class Range {
    int start;
    int end;

    public Range(int tokenStart, int tokenEnd) {
        this.start = tokenStart;
        this.end = tokenEnd;
    }

    @Override
    public String toString() {
        return "[" + start + "," + end + ']';
    }
}
