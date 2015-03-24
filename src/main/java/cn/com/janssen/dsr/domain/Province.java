package cn.com.janssen.dsr.domain;

public class Province {
    private int code;
    private String name;

    public Province(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}
