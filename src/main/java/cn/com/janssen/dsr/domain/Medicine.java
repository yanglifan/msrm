package cn.com.janssen.dsr.domain;

public class Medicine {
    private Integer id;
    private String name;
    private String fullName;

    public Medicine() {
    }

    public Medicine(String name) {
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }
}
