package dml.user.service.shared;

import dml.user.entity.OpenIDUserBind;

/**
 * @author zheng chengdong
 */
public class SharedLoginByOpenIDResult {
    private boolean createNewUser;
    private OpenIDUserBind openIDUserBind;

    public boolean isCreateNewUser() {
        return createNewUser;
    }

    public void setCreateNewUser(boolean createNewUser) {
        this.createNewUser = createNewUser;
    }

    public OpenIDUserBind getOpenIDUserBind() {
        return openIDUserBind;
    }

    public void setOpenIDUserBind(OpenIDUserBind openIDUserBind) {
        this.openIDUserBind = openIDUserBind;
    }

}
