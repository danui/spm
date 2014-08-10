package com.apfrank.spm;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

import java.util.LinkedList;
import java.util.Iterator;
import java.util.HashSet;
import java.util.Set;

/**
 * A chain of (name,pattern) pairs.
 *
 * 
 */
public class SymbolFilter {

    private static class NamePattern {
        private String name;
        private Pattern pattern;
        public NamePattern(String name, Pattern pattern) {
            this.name = name;
            this.pattern = pattern;
        }
        public String getName() {
            return name;
        }
        public Pattern getPattern() {
            return pattern;
        }
    }
    
    private Set<String> knownSymbols;
    private LinkedList<NamePattern> filter;
    
    public SymbolFilter() {
        filter = new LinkedList<NamePattern>();
        knownSymbols = new HashSet<String>();
    }
    
    /**
     * Appends (name,pattern) pair to end of filter.
     */
    public void addSymbol(String name, Pattern pattern) {
        knownSymbols.add(name);
        filter.addLast(new NamePattern(name, pattern));
    }
    
    public Set<String> getKnownSymbols() {
        return knownSymbols;
    }
    
    /**
     * Get the name of the first symbol that matches line.
     *
     * @return Symbol string, or null.
     */
    public String getSymbol(String line) {
        Iterator<NamePattern> iter = filter.iterator();
        String symbol = null;
        while (iter.hasNext()) {
            NamePattern i = iter.next();
            Matcher m = i.getPattern().matcher(line);
            if (m.matches()) {
                return i.getName();
            }
        }
        return symbol;
    }
    
}
