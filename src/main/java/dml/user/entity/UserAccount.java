package dml.user.entity;

/**
 * @author zheng chengdong
 */
public interface UserAccount {
    boolean verifyPassword(String password);

    User getUser();

    void setUser(User user);
}
