package sk.r3n.sw;

public interface LongTermJobListener {

    void jobInProgress();

    void jobInProgress(int value);

    void jobInProgress(String message);

    void jobInProgress(String message, int value);
}
