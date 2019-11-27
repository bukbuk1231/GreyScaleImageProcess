package utils;

import java.util.List;
import java.util.Map;

public class LZWCode {

    public Map<Integer, List<Integer>> table;
    public String code;

    public LZWCode(String code, Map<Integer, List<Integer>> table) {
        this.code = code;
        this.table = table;
    }
}
