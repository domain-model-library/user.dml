package dml.user.entity;

/**
 * @author zheng chengdong
 */
public interface UserCurrentSession {
    void setUserID(Object userID);

    Object getUserID();

    UserSession getCurrentSession();

    void setCurrentSession(UserSession currentSession);
}
