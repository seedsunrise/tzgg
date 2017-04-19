package cn.com.flaginfo.pojo;

import java.util.List;

public class NotifyRelationOrg {
    private Integer id;

    private String notifyId;

    private String orgId;

    private String orgName;

    private String spId;

    private List<NotifyRelationPerson> notifyRelationPersons;

    public List<NotifyRelationPerson> getNotifyRelationPersons() {
        return notifyRelationPersons;
    }

    public void setNotifyRelationPersons(List<NotifyRelationPerson> notifyRelationPersons) {
        this.notifyRelationPersons = notifyRelationPersons;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNotifyId() {
        return notifyId;
    }

    public void setNotifyId(String notifyId) {
        this.notifyId = notifyId == null ? null : notifyId.trim();
    }

    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId == null ? null : orgId.trim();
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName == null ? null : orgName.trim();
    }

    public String getSpId() {
        return spId;
    }

    public void setSpId(String spId) {
        this.spId = spId == null ? null : spId.trim();
    }
}