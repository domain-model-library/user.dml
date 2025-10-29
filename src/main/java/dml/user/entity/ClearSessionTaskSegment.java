package dml.user.entity;

import dml.largescaletaskmanagement.entity.LargeScaleTaskSegmentBase;

import java.util.List;

public class ClearSessionTaskSegment extends LargeScaleTaskSegmentBase {

    private String id;
    private List<String> sessionIdList;

    public void setId(Object id) {
        this.id = (String) id;
    }

    @Override
    public Object getId() {
        return id;
    }

    public List<String> getSessionIdList() {
        return sessionIdList;
    }

    public void setSessionIdList(List<String> sessionIdList) {
        this.sessionIdList = sessionIdList;
    }
}
