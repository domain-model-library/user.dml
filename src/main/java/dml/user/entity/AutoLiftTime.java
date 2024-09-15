package dml.user.entity;

/**
 * @author zheng chengdong
 */
public class AutoLiftTime {

    private Object userID;

    private long liftTime;

    public boolean timeToLift(long currentTime) {
        return currentTime >= liftTime;
    }

    public void setUserID(Object userID) {
        this.userID = userID;
    }

    public Object getUserID() {
        return userID;
    }

    public long getLiftTime() {
        return liftTime;
    }

    public void setLiftTime(long liftTime) {
        this.liftTime = liftTime;
    }
}
