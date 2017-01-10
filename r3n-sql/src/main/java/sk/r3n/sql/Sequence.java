package sk.r3n.sql;

import java.io.Serializable;

public class Sequence implements Serializable {

    private String name;

    public Sequence(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
