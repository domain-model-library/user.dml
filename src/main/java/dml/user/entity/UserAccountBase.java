package dml.user.entity;

/**
 * @author zheng chengdong
 */
public abstract class UserAccountBase implements UserAccount {
    protected String password;

    @Override
    public boolean verifyPassword(String password) {
        return this.password.equals(password);
    }
}
