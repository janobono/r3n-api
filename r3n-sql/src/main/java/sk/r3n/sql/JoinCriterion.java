/*
 * Copyright 2014 janobono. All rights reserved.
 * Use of this source code is governed by a Apache 2.0
 * license that can be found in the LICENSE file.
 */
package sk.r3n.sql;

import java.io.Serializable;

/**
 * Join criterion object.
 *
 * @author janobono
 * @since 18 August 2014
 */
public class JoinCriterion implements Serializable {

    private Join join;

    private Table table;

    private final CriteriaManager criteriaManager;

    public JoinCriterion(Join join, Table table) {
        this.join = join;
        this.table = table;
        criteriaManager = new CriteriaManager();
    }

    public Join getJoin() {
        return join;
    }

    public void setJoin(Join join) {
        this.join = join;
    }

    public Table getTable() {
        return table;
    }

    public void setTable(Table table) {
        this.table = table;
    }

    public CriteriaManager getCriteriaManager() {
        return criteriaManager;
    }
}
