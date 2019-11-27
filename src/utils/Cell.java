package utils;

import java.util.Set;

public  class Cell {
    public Set<Integer> set;
    public double prob;
    public Cell(Set<Integer> set, double prob) {
        this.set = set;
        this.prob = prob;
    }
}
