/*
 * Copyright 2017 janobono. All rights reserved.
 * Use of this source code is governed by a Apache 2.0
 * license that can be found in the LICENSE file.
 */
package sk.r3n.sql;

import java.io.Serializable;

/**
 * Criteria content interface.
 *
 * @author janobono
 * @since 10 January 2017
 */
public interface CriteriaContent extends Serializable {

    Operator getOperator();
}
