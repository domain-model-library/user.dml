package dml.user.service.result;

import dml.user.entity.UserAutoLiftBan;

/**
 * @author zheng chengdong
 */
public class CheckBanAndLiftResult {
    private boolean banned;
    private UserAutoLiftBan autoLiftBan;

    public boolean isBanned() {
        return banned;
    }

    public void setBanned(boolean banned) {
        this.banned = banned;
    }

    public UserAutoLiftBan getAutoLiftBan() {
        return autoLiftBan;
    }

    public void setAutoLiftBan(UserAutoLiftBan autoLiftBan) {
        this.autoLiftBan = autoLiftBan;
    }
}
