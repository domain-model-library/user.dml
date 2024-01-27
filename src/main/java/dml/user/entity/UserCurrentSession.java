package dml.user.entity;

/**
 * @author zheng chengdong
 */
public interface UserCurrentSession {
    void setUserID(Object userID);

    Object getUserID();

    String getCurrentSessionID();

    void setCurrentSessionID(String currentSessionID);
}
