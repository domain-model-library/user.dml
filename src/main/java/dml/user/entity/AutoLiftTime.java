package dml.user.entity;

/**
 * @author zheng chengdong
 */
public interface AutoLiftTime {
    void setUserID(Object userID);

    Object getUserID();

    boolean timeToLift(long currentTime);
}
