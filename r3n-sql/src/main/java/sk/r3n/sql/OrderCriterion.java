/*
 * Copyright 2014 janobono. All rights reserved.
 * Use of this source code is governed by a Apache 2.0
 * license that can be found in the LICENSE file.
 */
package sk.r3n.sql;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * Order criterion object.
 *
 * @author janobono
 * @since 18 August 2014
 */
@Getter
@Setter
@ToString
public class OrderCriterion implements Serializable {

    private Column column;

    private Order order;
}
