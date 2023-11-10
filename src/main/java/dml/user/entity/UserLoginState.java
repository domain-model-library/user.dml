package dml.user.entity;

/**
 * @author zheng chengdong
 */
public interface UserLoginState {
    void setUserID(Object userID);

    Object getUserID();

    UserSession getCurrentUserSession();

    void setCurrentUserSession(UserSession currentUserSession);
}
