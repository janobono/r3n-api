/*
 * Copyright 2014 janobono. All rights reserved.
 * Use of this source code is governed by a Apache 2.0
 * license that can be found in the LICENSE file.
 */
package sk.r3n.jdbc;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import sk.r3n.sql.DataType;

import java.io.Serializable;

/**
 * Object to store parameters used in sql queries.
 *
 * @author janobono
 * @since 25 August 2014
 */
@AllArgsConstructor
@Getter
@Setter
@ToString
public class SqlParam implements Serializable {

    /**
     * Parameter data type.
     */
    private DataType dataType;

    /**
     * Parameter value.
     */
    private Object value;
}
