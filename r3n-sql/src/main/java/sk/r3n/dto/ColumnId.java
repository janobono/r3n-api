/*
 * Copyright 2014 janobono. All rights reserved.
 * Use of this source code is governed by a Apache 2.0
 * license that can be found in the LICENSE file.
 */
package sk.r3n.dto;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Allows identify column for mapping.
 *
 * @author janobono
 * @since 22 August 2014
 */
@Target(value = {ElementType.FIELD})
@Retention(value = RetentionPolicy.RUNTIME)
public @interface ColumnId {

    /**
     * @return Table identifier.
     */
    String table() default "";

    /**
     * @return Column identifier.
     */
    String column() default "";
}
