package sk.r3n.module;

import sk.r3n.util.R3NException;

public interface Module {

    public void directIO(String code, Object[] data) throws R3NException;

}
