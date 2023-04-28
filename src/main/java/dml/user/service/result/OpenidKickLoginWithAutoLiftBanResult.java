package dml.user.service.result;

/**
 * @author zheng chengdong
 */
public class OpenidKickLoginWithAutoLiftBanResult {
    private OpenidKickLoginResult openidKickLoginResult;
    private CheckBanAndAutoLiftResult checkBanAndAutoLiftResult;

    public OpenidKickLoginResult getOpenidKickLoginResult() {
        return openidKickLoginResult;
    }

    public void setOpenidKickLoginResult(OpenidKickLoginResult openidKickLoginResult) {
        this.openidKickLoginResult = openidKickLoginResult;
    }

    public CheckBanAndAutoLiftResult getCheckBanAndAutoLiftResult() {
        return checkBanAndAutoLiftResult;
    }

    public void setCheckBanAndAutoLiftResult(CheckBanAndAutoLiftResult checkBanAndAutoLiftResult) {
        this.checkBanAndAutoLiftResult = checkBanAndAutoLiftResult;
    }
}
