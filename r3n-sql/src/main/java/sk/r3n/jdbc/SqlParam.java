/*
 * Copyright 2014 janobono. All rights reserved.
 * Use of this source code is governed by a Apache 2.0
 * license that can be found in the LICENSE file.
 */
package sk.r3n.jdbc;

import sk.r3n.sql.DataType;

/**
 * Object to store parameters used in sql queries.
 *
 * @author janobono
 * @since 25 August 2014
 */
public record SqlParam(DataType dataType, Object value) {
}
