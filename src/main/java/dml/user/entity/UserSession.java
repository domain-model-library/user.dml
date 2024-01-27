package dml.user.entity;

/**
 * @author zheng chengdong
 */
public interface UserSession {
    String getId();

    void setId(String id);

    void setUserID(Object userID);

    Object getUserID();
}
