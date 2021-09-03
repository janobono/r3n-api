/*
 * Copyright 2014 janobono. All rights reserved.
 * Use of this source code is governed by a Apache 2.0
 * license that can be found in the LICENSE file.
 */
package sk.r3n.sql.impl;

import sk.r3n.sql.Column;
import sk.r3n.sql.DataType;

/**
 * Function column definition object.
 *
 * @author janobono
 * @since 26 September 2014
 */
public record ColumnFunction(String columnId, DataType dataType, String function, Column... members) implements Column {
}
