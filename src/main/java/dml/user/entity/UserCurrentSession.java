package dml.user.entity;

/**
 * @author zheng chengdong
 */
public class UserCurrentSession {
    private Object userID;
    private String currentSessionID;

    public Object getUserID() {
        return userID;
    }

    public void setUserID(Object userID) {
        this.userID = userID;
    }

    public String getCurrentSessionID() {
        return currentSessionID;
    }

    public void setCurrentSessionID(String currentSessionID) {
        this.currentSessionID = currentSessionID;
    }
}
