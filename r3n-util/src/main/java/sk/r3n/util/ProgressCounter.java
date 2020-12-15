/*
 * Copyright 2016 janobono. All rights reserved.
 * Use of this source code is governed by a Apache 2.0
 * license that can be found in the LICENSE file.
 */
package sk.r3n.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Progress counter class.
 *
 * <p>
 * This class delivers functionality to count progress.
 *
 * @author janobono
 * @since 8 September 2014
 */
public class ProgressCounter implements Serializable {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProgressCounter.class);

    private int totalProgress;

    BigDecimal progress;

    BigDecimal step;

    /**
     * ProgressCounter constructor.
     *
     * <p>
     * Total progress is set to 100.
     *
     * @param totalStep total steps needed to finalize progress
     */
    public ProgressCounter(int totalStep) {
        this(100, totalStep);
    }

    /**
     * ProgressCounter constructor.
     *
     * @param totalProgress maximum progress number
     * @param totalStep     total steps needed to finalize progress
     */
    public ProgressCounter(int totalProgress, int totalStep) {
        super();
        this.totalProgress = totalProgress;
        if (totalProgress <= 0) {
            LOGGER.warn("TotalProgress was set to 100.");
            this.totalProgress = 100;
        }
        init(totalStep);
    }

    private void init(int totalStep) {
        if (totalStep <= 0) {
            LOGGER.warn("TotalStep was set to 1.");
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

    /**
     * Returns actual progress value.
     *
     * @return actual progress
     */
    public int actualProgress() {
        int result = progress.intValue();
        if (result > totalProgress) {
            LOGGER.warn("Progress {} is more than TotalProgress {}.", result, totalProgress);
            result = totalProgress;
        }
        return result;
    }

    /**
     * Returns step value.
     *
     * @return step value
     */
    public int step() {
        return step.intValue();
    }

    /**
     * Increment progress with one step.
     *
     * @return actual progress
     */
    public int inc() {
        progress = progress.add(step);
        return actualProgress();
    }

    /**
     * Increment progress with steps.
     *
     * @param stepCount steps to increment progress
     * @return actual progress
     */
    public int inc(int stepCount) {
        if (stepCount > 0) {
            progress = progress.add(step.multiply(new BigDecimal(stepCount)));
        }
        return actualProgress();
    }

    /**
     * Set progress to step.
     *
     * @param stepCount steps where we want to be
     * @return actual progress
     */
    public int to(int stepCount) {
        if (stepCount > 0) {
            progress = step.multiply(new BigDecimal(stepCount));
        }
        return actualProgress();
    }
}
