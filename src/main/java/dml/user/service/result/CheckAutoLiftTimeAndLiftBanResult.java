package dml.user.service.result;

import dml.user.entity.AutoLiftTime;
import dml.user.entity.UserBan;

/**
 * @author zheng chengdong
 */
public class CheckAutoLiftTimeAndLiftBanResult {
    private boolean liftSuccess;
    private UserBan userBan;
    private AutoLiftTime autoLiftTime;


    public boolean isLiftSuccess() {
        return liftSuccess;
    }

    public void setLiftSuccess(boolean liftSuccess) {
        this.liftSuccess = liftSuccess;
    }

    public UserBan getUserBan() {
        return userBan;
    }

    public void setUserBan(UserBan userBan) {
        this.userBan = userBan;
    }

    public AutoLiftTime getAutoLiftTime() {
        return autoLiftTime;
    }

    public void setAutoLiftTime(AutoLiftTime autoLiftTime) {
        this.autoLiftTime = autoLiftTime;
    }
}
