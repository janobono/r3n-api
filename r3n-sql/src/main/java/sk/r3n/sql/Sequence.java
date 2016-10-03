package sk.r3n.sql;

import java.io.Serializable;

public class Sequence implements Serializable {

    private String name;

    public Sequence(String name) {
        this.name = name;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        boolean result = false;
        if (obj instanceof Sequence) {
            Sequence sequenceObj = (Sequence) obj;
            result = sequenceObj.name.equals(name);
        }
        return result;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
