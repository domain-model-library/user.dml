package dml.user.entity;

public abstract class AutoLiftUserBanBase implements AutoLiftUserBan {

    protected long autoLiftTime;

    @Override
    public void setAutoLiftTime(long autoLiftTime) {
        this.autoLiftTime = autoLiftTime;
    }

    @Override
    public boolean checkLift(long currentTime) {
        return currentTime > autoLiftTime;
    }
}
