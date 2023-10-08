package dml.user.service.result;

import dml.user.entity.AutoLiftTime;

/**
 * @author zheng chengdong
 */
public class CheckAndRemoveLiftTimeResult {
    private boolean toLift;
    private AutoLiftTime autoLiftTime;


    public boolean isToLift() {
        return toLift;
    }

    public void setToLift(boolean toLift) {
        this.toLift = toLift;
    }

    public AutoLiftTime getAutoLiftTime() {
        return autoLiftTime;
    }

    public void setAutoLiftTime(AutoLiftTime autoLiftTime) {
        this.autoLiftTime = autoLiftTime;
    }
}
