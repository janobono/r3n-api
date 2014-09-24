package sk.r3n.sw.util;

public interface LongTermJobListener {

    public void jobInProgress();

    public void jobInProgress(int value);

    public void jobInProgress(String message);

    public void jobInProgress(String message, int value);

}
