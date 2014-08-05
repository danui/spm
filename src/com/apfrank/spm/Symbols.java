package com.apfrank.spm;

public class Symbols {

    /*
     * Enumeration of symbol types
     */
    
    public static final int TODO   = 0;
    public static final int DONE   = 1;
    public static final int CANCEL = 2;
    public static final int MAYBE  = 3;
    
    public static final int NUM_SYMBOLS = 4;

    public static final String getName(int code) {
        switch (code) {
            case TODO: return "TODO";
            case DONE: return "DONE";
            case CANCEL: return "CANCEL";
            case MAYBE: return "MAYBE";
        }
        return "UNKNOWN";
    }
}
