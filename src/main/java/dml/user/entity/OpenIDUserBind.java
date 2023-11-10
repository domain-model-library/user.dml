package dml.user.entity;

/**
 * @author zheng chengdong
 */
public interface OpenIDUserBind {
    void setOpenID(String openID);

    void setUser(User user);

    User getUser();
}
