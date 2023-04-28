package dml.user.service.result;

/**
 * @author zheng chengdong
 */
public class CheckBanAndAutoLiftResult {

    private boolean banned;
    private CheckAutoLiftTimeAndLiftBanResult checkAutoLiftTimeAndLiftBanResult;

    public boolean isBanned() {
        return banned;
    }

    public void setBanned(boolean banned) {
        this.banned = banned;
    }

    public CheckAutoLiftTimeAndLiftBanResult getCheckAutoLiftTimeAndLiftBanResult() {
        return checkAutoLiftTimeAndLiftBanResult;
    }

    public void setCheckAutoLiftTimeAndLiftBanResult(CheckAutoLiftTimeAndLiftBanResult checkAutoLiftTimeAndLiftBanResult) {
        this.checkAutoLiftTimeAndLiftBanResult = checkAutoLiftTimeAndLiftBanResult;
    }
}
