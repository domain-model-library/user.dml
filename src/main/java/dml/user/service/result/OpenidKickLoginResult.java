package dml.user.service.result;

/**
 * @author zheng chengdong
 */
public class OpenidKickLoginResult {
    private OpenidLoginResult loginResult;
    private String removedUserSessionId;

    public OpenidLoginResult getLoginResult() {
        return loginResult;
    }

    public void setLoginResult(OpenidLoginResult loginResult) {
        this.loginResult = loginResult;
    }

    public String getRemovedUserSessionId() {
        return removedUserSessionId;
    }

    public void setRemovedUserSessionId(String removedUserSessionId) {
        this.removedUserSessionId = removedUserSessionId;
    }
}
