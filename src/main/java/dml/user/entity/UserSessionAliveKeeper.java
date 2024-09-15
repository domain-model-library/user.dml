package dml.user.entity;

import dml.keepalive.entity.AliveKeeperBase;

public class UserSessionAliveKeeper extends AliveKeeperBase {

    private String sessionID;

    @Override
    public void setId(Object id) {
        this.sessionID = (String) id;
    }

    @Override
    public Object getId() {
        return sessionID;
    }
}
