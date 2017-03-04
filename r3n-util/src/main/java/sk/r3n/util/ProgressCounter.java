/* 
 * Copyright 2016 janobono. All rights reserved.
 * Use of this source code is governed by a Apache 2.0
 * license that can be found in the LICENSE file.
 */
package sk.r3n.util;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class ProgressCounter implements Serializable {

    BigDecimal progress;

    BigDecimal step;

    public ProgressCounter(int totalStep) {
        this(100, totalStep);
    }

    public ProgressCounter(int totalProgress, int totalStep) {
        super();
        if (totalProgress <= 0) {
            totalProgress = 100;
        }

        if (totalStep <= 0) {
            totalStep = 1;
        }

        step = new BigDecimal(totalProgress);
        int scale = Integer.toString(totalStep).length();
        if (scale < Integer.toString(totalProgress).length()) {
            scale = Integer.toString(totalProgress).length();
        }
        BigDecimal divider = new BigDecimal(totalStep);
        step = step.divide(divider, scale, RoundingMode.HALF_UP);

        progress = BigDecimal.ZERO;
        progress.setScale(scale);
    }

    public int inc() {
        progress = progress.add(step);
        return progress.intValue();
    }

    public int inc(int stepCount) {
        if (stepCount <= 0) {
            return inc();
        }
        progress = progress.add(step.multiply(new BigDecimal(stepCount)));
        return progress.intValue();
    }

    public int to(int stepCount) {
        if (stepCount <= 0) {
            return 0;
        }
        progress = step.multiply(new BigDecimal(stepCount));
        return progress.intValue();
    }
}
