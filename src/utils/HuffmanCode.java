package utils;

import java.util.Map;

public class HuffmanCode {
    public String code;
    public Map<Integer, String> table;
    public HuffmanCode(String code, Map<Integer, String> table) {
        this.code = code;
        this.table = table;
    }
}
