package dml.user.entity;

/**
 * @author zheng chengdong
 */
public interface OpenIDUserBind {
    void setId(String id);

    void setUser(User user);

    User getUser();
}
