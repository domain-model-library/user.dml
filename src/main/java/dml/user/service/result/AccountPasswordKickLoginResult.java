package dml.user.service.result;

import dml.user.entity.UserSession;

/**
 * @author zheng chengdong
 */
public class AccountPasswordKickLoginResult {
    private AccountPasswordLoginResult loginResult;
    private String removedUserSessionId;

    public AccountPasswordLoginResult getLoginResult() {
        return loginResult;
    }

    public void setLoginResult(AccountPasswordLoginResult loginResult) {
        this.loginResult = loginResult;
    }

    public String getRemovedUserSessionId() {
        return removedUserSessionId;
    }

    public void setRemovedUserSessionId(String removedUserSessionId) {
        this.removedUserSessionId = removedUserSessionId;
    }

    public UserSession getNewUserSession() {
        return loginResult.getNewUserSession();
    }
}
