package dml.user.service.result;

import dml.user.entity.User;

/**
 * @author zheng chengdong
 */
public class RegisterNewUserResult {
    private boolean accountExists;
    private User newUser;

    public boolean isAccountExists() {
        return accountExists;
    }

    public void setAccountExists(boolean accountExists) {
        this.accountExists = accountExists;
    }

    public User getNewUser() {
        return newUser;
    }

    public void setNewUser(User newUser) {
        this.newUser = newUser;
    }
}
