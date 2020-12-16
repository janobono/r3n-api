package sk.r3n.util;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ProgressCounterTest {

    @Test
    public void test() {
        // Create progress counter
        ProgressCounter progressCounter = new ProgressCounter(100);
        // No progress yet
        assertThat(progressCounter.actualProgress()).isEqualTo(0);
        // Our step is 1
        assertThat(progressCounter.step()).isEqualTo(1);
        // We move one step
        assertThat(progressCounter.inc()).isEqualTo(1);
        // We move 10 steps
        assertThat(progressCounter.inc(10)).isEqualTo(11);
        // We move to step 50
        assertThat(progressCounter.to(50)).isEqualTo(50);
        // Move to total progress
        do {
            assertThat(progressCounter.inc()).isEqualTo(progressCounter.actualProgress());
        } while (progressCounter.actualProgress() < 100);
        // Next move but result is always total progress
        // you will see warning in log: WARN sk.r3n.util.ProgressCounter - Progress 101 is more than TotalProgress 100.
        assertThat(progressCounter.inc()).isEqualTo(100);
    }
}
