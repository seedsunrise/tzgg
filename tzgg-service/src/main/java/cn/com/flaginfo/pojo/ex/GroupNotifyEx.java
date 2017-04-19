package cn.com.flaginfo.pojo.ex;

import cn.com.flaginfo.pojo.Attachments;
import cn.com.flaginfo.pojo.GroupNotify;
import cn.com.flaginfo.pojo.NotifyRelationOrg;
import cn.com.flaginfo.pojo.NotifyRelationPerson;

import java.util.List;

public class GroupNotifyEx extends GroupNotify{

    private String startTime;

    private String endTime;

    private List<NotifyRelationPerson> notifyPersonRelations;

    private List<Attachments> attachmentses;

    private String range;

    private String publishType;

    private String notifyType;

    private List<GroupNotifyEx> notifyExes;

    private List<NotifyRelationOrg> notifyRelationOrgs;

    private String operator;

    private String roleType;

    /**
     * 操作类型
     */
    public static enum operatorType{

        DETAIL("详情","detail"),EDIT("已发布","edit");

        public String name;
        public String code;
        private operatorType(String name, String code){
            this.name = name;
            this.code = code;
        }
        public static operatorType findByCode(String code){
            operatorType[] types = operatorType.values();
            for(operatorType type:types){
                if (type.code.equals(code))
                    return type;
            }
            return null;
        }
    }

    public String getRoleType() {
        return roleType;
    }

    public void setRoleType(String roleType) {
        this.roleType = roleType;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public List<NotifyRelationOrg> getNotifyRelationOrgs() {
        return notifyRelationOrgs;
    }

    public void setNotifyRelationOrgs(List<NotifyRelationOrg> notifyRelationOrgs) {
        this.notifyRelationOrgs = notifyRelationOrgs;
    }

    public List<GroupNotifyEx> getNotifyExes() {
        return notifyExes;
    }

    public void setNotifyExes(List<GroupNotifyEx> notifyExes) {
        this.notifyExes = notifyExes;
    }

    public String getNotifyType() {
        return notifyType;
    }

    public void setNotifyType(String notifyType) {
        this.notifyType = notifyType;
    }

    public String getPublishType() {
        return publishType;
    }

    public void setPublishType(String publishType) {
        this.publishType = publishType;
    }

    public List<Attachments> getAttachmentses() {
        return attachmentses;
    }

    public void setAttachmentses(List<Attachments> attachmentses) {
        this.attachmentses = attachmentses;
    }

    public String getRange() {
        return range;
    }

    public void setRange(String range) {
        this.range = range;
    }

    public List<NotifyRelationPerson> getNotifyPersonRelations() {
        return notifyPersonRelations;
    }

    public void setNotifyPersonRelations(List<NotifyRelationPerson> notifyPersonRelations) {
        this.notifyPersonRelations = notifyPersonRelations;
    }

    /**
     * 状态
     */
    public static enum statusType{

        SAVE("已保存","save"),PUBLISH("已发布","publish"),CLOSE("已关闭","close");

        public String name;
        public String code;
        private statusType(String name, String code){
            this.name = name;
            this.code = code;
        }
        public static statusType findByCode(String code){
            statusType[] types = statusType.values();
            for(statusType type:types){
                if (type.code.equals(code))
                    return type;
            }
            return null;
        }
    }

    /**
     * 状态
     */
    public static enum rangeType{

        COMMON("公开","common"),PERSON("指定","person");

        public String name;
        public String code;
        private rangeType(String name, String code){
            this.name = name;
            this.code = code;
        }
        public static statusType findByCode(String code){
            statusType[] types = statusType.values();
            for(statusType type:types){
                if (type.code.equals(code))
                    return type;
            }
            return null;
        }
    }

    /**
     * 指定发布范围
     */
    public static enum publishRangeType{

        PARTY("发送到部门","party"),PERSON("发送到个人发布","person");

        public String name;
        public String code;
        private publishRangeType(String name, String code){
            this.name = name;
            this.code = code;
        }
        public static statusType findByCode(String code){
            statusType[] types = statusType.values();
            for(statusType type:types){
                if (type.code.equals(code))
                    return type;
            }
            return null;
        }
    }

    /**
     * 指定通知范围
     */
    public static enum notifyRangeType{

        MANAGER("管理员","manager"),MEMBER("成员","member");

        public String name;
        public String code;
        private notifyRangeType(String name, String code){
            this.name = name;
            this.code = code;
        }
        public static statusType findByCode(String code){
            statusType[] types = statusType.values();
            for(statusType type:types){
                if (type.code.equals(code))
                    return type;
            }
            return null;
        }
    }

    /**
     * 指定通知范围
     */
    public static enum readStatusType{

        READ("管理员","read"),UNREAD("成员","unread");

        public String name;
        public String code;
        private readStatusType(String name, String code){
            this.name = name;
            this.code = code;
        }
        public static statusType findByCode(String code){
            statusType[] types = statusType.values();
            for(statusType type:types){
                if (type.code.equals(code))
                    return type;
            }
            return null;
        }
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }
}