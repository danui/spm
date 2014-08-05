package com.apfrank.spm;

public class DataPoint {

    private Commit commit;
    private Path path;
    private int[] count;
    private int totalCount;

    public DataPoint(Commit commit, Path path) {
        this.commit = commit;
        this.path = path;

        count = new int[Symbols.NUM_SYMBOLS];
        for (int i = 0; i < Symbols.NUM_SYMBOLS; ++i) {
            count[i] = 0;
        }
        totalCount = 0;
    }

    public Commit getCommit() {
        return commit;
    }

    public Path getPath() {
        return path;
    }

    public int getCount(int symbolCode) {
        return count[symbolCode];
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void increment(int symbolCode) {
        count[symbolCode] += 1;
        totalCount += 1;
    }
}
