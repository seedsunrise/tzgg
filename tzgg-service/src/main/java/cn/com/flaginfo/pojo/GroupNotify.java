package cn.com.flaginfo.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

public class GroupNotify {
    private String id;

    private String title;

    private String publishPeople;

    private String publishOrganize;

    private Date publishTime;

    private String status;

    private String createUserName;

    private String createUserId;

    private Date createTime;

    private String updateUserName;

    private String updateUserId;

    private Date updateTime;

    private String spId;

    private Integer del;

    private String publishOrgId;

    private String content;

    private String publishPeopleId;

    private Integer type;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id == null ? null : id.trim();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title == null ? null : title.trim();
    }

    public String getPublishPeople() {
        return publishPeople;
    }

    public void setPublishPeople(String publishPeople) {
        this.publishPeople = publishPeople == null ? null : publishPeople.trim();
    }

    public String getPublishOrganize() {
        return publishOrganize;
    }

    public void setPublishOrganize(String publishOrganize) {
        this.publishOrganize = publishOrganize == null ? null : publishOrganize.trim();
    }

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone="GMT+8")
    public Date getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(Date publishTime) {
        this.publishTime = publishTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status == null ? null : status.trim();
    }

    public String getCreateUserName() {
        return createUserName;
    }

    public void setCreateUserName(String createUserName) {
        this.createUserName = createUserName == null ? null : createUserName.trim();
    }

    public String getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(String createUserId) {
        this.createUserId = createUserId == null ? null : createUserId.trim();
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getUpdateUserName() {
        return updateUserName;
    }

    public void setUpdateUserName(String updateUserName) {
        this.updateUserName = updateUserName == null ? null : updateUserName.trim();
    }

    public String getUpdateUserId() {
        return updateUserId;
    }

    public void setUpdateUserId(String updateUserId) {
        this.updateUserId = updateUserId == null ? null : updateUserId.trim();
    }

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone="GMT+8")
    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getSpId() {
        return spId;
    }

    public void setSpId(String spId) {
        this.spId = spId == null ? null : spId.trim();
    }

    public Integer getDel() {
        return del;
    }

    public void setDel(Integer del) {
        this.del = del;
    }

    public String getPublishOrgId() {
        return publishOrgId;
    }

    public void setPublishOrgId(String publishOrgId) {
        this.publishOrgId = publishOrgId == null ? null : publishOrgId.trim();
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content == null ? null : content.trim();
    }

    public String getPublishPeopleId() {
        return publishPeopleId;
    }

    public void setPublishPeopleId(String publishPeopleId) {
        this.publishPeopleId = publishPeopleId == null ? null : publishPeopleId.trim();
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }
}