package cn.com.flaginfo.service.impl;

import cn.com.flaginfo.dao.*;
import cn.com.flaginfo.example.*;
import cn.com.flaginfo.logger.Logger;
import cn.com.flaginfo.pojo.*;
import cn.com.flaginfo.pojo.ex.GroupNotifyEx;
import cn.com.flaginfo.service.ApiService;
import cn.com.flaginfo.service.GroupNotifyService;
import cn.com.flaginfo.user.auth.common.JsonHelper;
import cn.com.flaginfo.user.auth.common.StringUtil;
import cn.com.flaginfo.utils.*;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Created by lenovo on 2016/11/29.
 */
@Service
public class GroupNotifyServiceImpl implements GroupNotifyService {

    Logger logger = new Logger(GroupNotifyServiceImpl.class);

    @Autowired
    private GroupNotifyMapper groupNotifyMapper;

    @Autowired
    private PersonNotifyMapper personNotifyMapper;

    @Autowired
    private AttachmentsMapper attachmentsMapper;

    @Autowired
    private NotifyRelationPersonMapper notifyRelationPersonMapper;

    @Autowired
    private NotifyRelationOrgMapper notifyRelationOrgMapper;

    @Autowired
    private ApiService apiService;

    /**
     * 保存通知公告
     *
     * @param groupNotifyEx
     * @param ssoUser
     */
    @Override
    @Transactional
    public void saveNotify(GroupNotifyEx groupNotifyEx, SsoUser ssoUser) throws NotifyException{
        String id = groupNotifyEx.getId();
        if(StringUtil.isEmpty(id)){ //新增通知公告
            String range = groupNotifyEx.getRange();
            logger.debug("新增通知公告，参数:"+JSONObject.toJSONString(groupNotifyEx));
            if(GroupNotifyEx.rangeType.COMMON.code.equals(range)){  //公共的通知公告
                insertCommonNotify(groupNotifyEx, ssoUser);
            }else if(GroupNotifyEx.rangeType.PERSON.code.equals(range)){ //指定的通知公告
                insertPersonNotify(groupNotifyEx, ssoUser);
                updateNotifyRelation(groupNotifyEx,ssoUser ); //修改指定公告的关系表
            }
        }else{  //更新通知公告
            logger.debug("更新通知公告，参数:"+JSONObject.toJSONString(groupNotifyEx));
            String range = groupNotifyEx.getRange();
            groupNotifyEx = queryNotify(groupNotifyEx, ssoUser);
            if(GroupNotifyEx.rangeType.COMMON.code.equals(range)){  //公共的通知公告
                updateCommonNotify(groupNotifyEx, ssoUser);
            }else if(GroupNotifyEx.rangeType.PERSON.code.equals(range)){ //指定的通知公告
                updatePersonNotify(groupNotifyEx, ssoUser);
                updateNotifyRelation(groupNotifyEx,ssoUser ); //修改指定公告的关系表
            }
        }
        logger.debug("保存公告附件,参数:"+JSONObject.toJSONString(groupNotifyEx));
        insertAttachments(groupNotifyEx, ssoUser); //保存附件
    }

    /**
     * 根据id查询通知公告
     * @param groupNotifyEx
     * @param ssoUser
     * @return
     * @throws NotifyException
     */
    private GroupNotifyEx queryNotify(GroupNotifyEx groupNotifyEx, SsoUser ssoUser) throws NotifyException{
        GroupNotifyExample groupNotifyExample = new GroupNotifyExample();
        GroupNotifyExample.Criteria criteria = groupNotifyExample.createCriteria();
        criteria.andSpIdEqualTo(ssoUser.getSpId()).andIdEqualTo(groupNotifyEx.getId())
                .andDelEqualTo(MutiSiteConstants.NO_DEL);
        List<GroupNotify> groupNotifies = groupNotifyMapper.selectByExample(groupNotifyExample);
        PersonNotifyExample personNotifyExample = new PersonNotifyExample();
        personNotifyExample.createCriteria().andSpIdEqualTo(ssoUser.getSpId()).andIdEqualTo(groupNotifyEx.getId())
                .andDelEqualTo(MutiSiteConstants.NO_DEL);
        List<PersonNotify> personNotifies = personNotifyMapper.selectByExample(personNotifyExample);
        if(CollectionUtils.isEmpty(personNotifies) && CollectionUtils.isEmpty(groupNotifies)){
            throw new NotifyException("没有查询到相关公告");
        }
        if(CollectionUtils.isNotEmpty(groupNotifies)){
            groupNotifyEx.setRange(GroupNotifyEx.rangeType.COMMON.code);
            groupNotifyEx.setStatus(groupNotifies.get(0).getStatus());
            groupNotifyEx.setPublishOrgId(groupNotifies.get(0).getPublishOrgId());
            groupNotifyEx.setPublishOrganize(groupNotifies.get(0).getPublishOrganize());
            groupNotifyEx.setPublishTime(groupNotifies.get(0).getPublishTime());
            groupNotifyEx.setPublishPeople(groupNotifies.get(0).getPublishPeople());
            groupNotifyEx.setPublishPeopleId(groupNotifies.get(0).getPublishPeopleId());
        }else{
            groupNotifyEx.setRange(GroupNotifyEx.rangeType.PERSON.code);
            groupNotifyEx.setStatus(personNotifies.get(0).getStatus());
            groupNotifyEx.setPublishOrgId(personNotifies.get(0).getPublishOrgId());
            groupNotifyEx.setPublishOrganize(personNotifies.get(0).getPublishOrganize());
            groupNotifyEx.setPublishTime(personNotifies.get(0).getPublishTime());
            groupNotifyEx.setPublishPeople(personNotifies.get(0).getPublishPeople());
            groupNotifyEx.setPublishPeopleId(personNotifies.get(0).getPublishPeopleId());
        }
        return groupNotifyEx;
    }


    /**
     * 修改指定公告的关系表
     * @param groupNotifyEx
     * @param ssoUser
     */
    private void  updateNotifyRelation(GroupNotifyEx groupNotifyEx, SsoUser ssoUser) throws NotifyException{
        if(GroupNotifyEx.notifyRangeType.MEMBER.code.equals(groupNotifyEx.getNotifyType())){//插入到部门关系表
            saveNotifyOrgRelation(groupNotifyEx, ssoUser);
        }else{
            saveNotifyPersonRelation(groupNotifyEx, ssoUser);  //插入指定通知参与人关系表
        }
    }

    /**
     * 新增公共的通知公告
     * @param commonNotifyEx
     * @param ssoUser
     */
    private void insertCommonNotify(GroupNotifyEx commonNotifyEx, SsoUser ssoUser) throws NotifyException{
        Date date = new Date();
        UserInfo userInfo = ssoUser.getUserInfo();
        if(null == commonNotifyEx.getCreateTime()){
            commonNotifyEx.setCreateTime(date);
            commonNotifyEx.setCreateUserId(userInfo.getId());
            commonNotifyEx.setCreateUserName(userInfo.getName());
        }
        String id = commonNotifyEx.getId();
        String uuId = StringUtils.isEmpty(id)?StringUtil.getUUID():id;
        commonNotifyEx.setId(uuId);
        commonNotifyEx.setUpdateTime(date);
        commonNotifyEx.setUpdateUserId(userInfo.getId());
        commonNotifyEx.setUpdateUserName(userInfo.getName());
        commonNotifyEx.setDel(MutiSiteConstants.NO_DEL);
        commonNotifyEx.setSpId(ssoUser.getSpId());
        commonNotifyEx.setStatus(commonNotifyEx.getOperator());
        commonNotifyEx.setPublishOrgId(ssoUser.getCurRole().getGroupId());
        if(GroupNotifyEx.statusType.PUBLISH.code.equals(commonNotifyEx.getOperator())){ //如果是发布
            commonNotifyEx.setPublishOrganize(getOrgNameById(ssoUser.getCurRole().getGroupId(), ssoUser));
            commonNotifyEx.setPublishPeopleId(userInfo.getId());
            commonNotifyEx.setPublishPeople(userInfo.getName());
            if(null ==commonNotifyEx.getPublishTime()){//原来没有发布过
                commonNotifyEx.setPublishTime(date);
            }
        }
        commonNotifyEx.setType(1);
        groupNotifyMapper.insertSelective(commonNotifyEx);
        if(GroupNotifyEx.statusType.PUBLISH.code.equals(commonNotifyEx.getStatus())){ //如果是发布
            sendMsg(commonNotifyEx ,ssoUser); //发送app消息
        }
    }

    /**
     * 新增指定的通知公告
     * @param commonNotifyEx
     * @param ssoUser
     */
    private void insertPersonNotify(GroupNotifyEx commonNotifyEx, SsoUser ssoUser){
        PersonNotify personNotify = new PersonNotify();
        Date date = new Date();
        String id = commonNotifyEx.getId();
        String uuId = StringUtils.isEmpty(id)?StringUtil.getUUID():id;
        personNotify.setId(uuId);
        commonNotifyEx.setId(uuId);
        personNotify.setTitle(commonNotifyEx.getTitle());
        personNotify.setStatus(commonNotifyEx.getOperator());
        personNotify.setContent(commonNotifyEx.getContent());
        UserInfo userInfo = ssoUser.getUserInfo();
        if(null == commonNotifyEx.getCreateTime()){
            personNotify.setCreateUserId(userInfo.getId());
            personNotify.setCreateUserName(userInfo.getName());
            personNotify.setCreateTime(date);
        }
        personNotify.setUpdateTime(date);
        personNotify.setUpdateUserId(userInfo.getId());
        personNotify.setUpdateUserName(userInfo.getName());
        personNotify.setDel(MutiSiteConstants.NO_DEL);
        personNotify.setSpId(ssoUser.getSpId());
        personNotify.setPublishType(commonNotifyEx.getPublishType());
        personNotify.setPublishOrgId(ssoUser.getCurRole().getGroupId());
        if(null != commonNotifyEx.getPublishTime()){ //如果以前发布过 那么保留以前的发布信息
            personNotify.setPublishOrganize(commonNotifyEx.getPublishOrganize());
            personNotify.setPublishPeopleId(commonNotifyEx.getPublishPeopleId());
            personNotify.setPublishPeople(commonNotifyEx.getPublishPeople());
            personNotify.setPublishTime(commonNotifyEx.getPublishTime());
        }
        if(GroupNotifyEx.statusType.PUBLISH.code.equals(commonNotifyEx.getOperator())){ //如果是发布
            personNotify.setPublishOrganize(getOrgNameById(ssoUser.getCurRole().getGroupId(),ssoUser));
            personNotify.setPublishPeopleId(userInfo.getId());
            personNotify.setPublishPeople(userInfo.getName());
            if(null ==commonNotifyEx.getPublishTime()){ // 原来没有发布过
                personNotify.setPublishTime(date);
            }
        }
        String notifyType = commonNotifyEx.getNotifyType();
        if(StringUtils.isEmpty(notifyType)){
            notifyType = GroupNotifyEx.notifyRangeType.MEMBER.code;
        }
        personNotify.setNotifyType(notifyType);
        personNotifyMapper.insertSelective(personNotify);
    }

    /**
     * 更新公共的通知公告
     * @param groupNotifyEx
     * @param ssoUser
     */
    private void updateCommonNotify(GroupNotifyEx groupNotifyEx, SsoUser ssoUser) throws NotifyException{
        if(!GroupNotifyEx.rangeType.COMMON.code.equals(groupNotifyEx.getRange())){ //该公告原来是指定的
            delPersonNotifyRelations(groupNotifyEx, ssoUser); //删除原来通知公告的关联表
            insertCommonNotify(groupNotifyEx, ssoUser);
            return;
        }
        String status = groupNotifyEx.getStatus();
        if(GroupNotifyEx.statusType.PUBLISH.code.equals(status)){  //已发布的通知公告
            throw new NotifyException("已发布的公告不允许修改");
        }
        Date date = new Date();
        UserInfo userInfo = ssoUser.getUserInfo();
        groupNotifyEx.setUpdateTime(date);
        groupNotifyEx.setUpdateUserId(userInfo.getId());
        groupNotifyEx.setUpdateUserName(userInfo.getName());
        groupNotifyEx.setStatus(groupNotifyEx.getOperator());
        if(GroupNotifyEx.statusType.PUBLISH.code.equals(groupNotifyEx.getOperator())){
            groupNotifyEx.setStatus(GroupNotifyEx.statusType.PUBLISH.code);
            buildPublishParams(groupNotifyEx, ssoUser);//构造发布通告的参数
        }
        GroupNotifyExample groupNotifyExample = new GroupNotifyExample();
        GroupNotifyExample.Criteria criteria = groupNotifyExample.createCriteria();
        criteria.andSpIdEqualTo(ssoUser.getSpId()).andIdEqualTo(groupNotifyEx.getId())
                .andDelEqualTo(MutiSiteConstants.NO_DEL);
        groupNotifyMapper.updateByExampleSelective(groupNotifyEx, groupNotifyExample);
        if(GroupNotifyEx.statusType.PUBLISH.code.equals(groupNotifyEx.getOperator())){ //如果是发布
            sendMsg(groupNotifyEx ,ssoUser); //发送app消息
        }
    }

    /**
     * 删除指定通知公告的关联关系
     * @param groupNotifyEx
     * @param ssoUser
     */
    private void delPersonNotifyRelations(GroupNotifyEx groupNotifyEx, SsoUser ssoUser){
        AttachmentsExample attachmentsExample = new AttachmentsExample();
        attachmentsExample.createCriteria().andNotifyIdEqualTo(groupNotifyEx.getId()).andSpIdEqualTo(ssoUser.getSpId());
        attachmentsMapper.deleteByExample(attachmentsExample);  //删除指定通知公告对应的附件
        NotifyRelationPersonExample notifyRelationPersonExample = new NotifyRelationPersonExample();
        notifyRelationPersonExample.createCriteria().andSpIdEqualTo(ssoUser.getSpId()).andNotifyIdEqualTo(groupNotifyEx.getId());
        notifyRelationPersonMapper.deleteByExample(notifyRelationPersonExample); //删除指定通知公告和人的关联表
        PersonNotifyExample personNotifyExample = new PersonNotifyExample();
        personNotifyExample.createCriteria().andSpIdEqualTo(ssoUser.getSpId()).andIdEqualTo(groupNotifyEx.getId());
        personNotifyMapper.deleteByExample(personNotifyExample);// 删除原来的数据
    }

    /**
     * 更新指定的通知公告
     * @param commonNotifyEx
     * @param ssoUser
     */
    private void updatePersonNotify(GroupNotifyEx commonNotifyEx, SsoUser ssoUser) throws NotifyException{
        if(!GroupNotifyEx.rangeType.PERSON.code.equals(commonNotifyEx.getRange())){ //该公告原来是公共的
            delCommonNotifyRelations(commonNotifyEx, ssoUser); //删除原来通知公告的关联表
            insertPersonNotify(commonNotifyEx, ssoUser);
            return;
        }
        String status = commonNotifyEx.getStatus();
        if(GroupNotifyEx.statusType.PUBLISH.code.equals(status)){  //已发布的通知公告
            throw new NotifyException("已发布的公告不允许修改");
        }
        Date date = new Date();
        UserInfo userInfo = ssoUser.getUserInfo();
        commonNotifyEx.setUpdateTime(date);
        commonNotifyEx.setUpdateUserId(userInfo.getId());
        commonNotifyEx.setUpdateUserName(userInfo.getName());
        commonNotifyEx.setStatus(commonNotifyEx.getOperator());
        PersonNotify personNotify = JSONObject.parseObject(JSONObject.toJSONString(commonNotifyEx), PersonNotify.class);
        if(GroupNotifyEx.statusType.PUBLISH.code.equals(commonNotifyEx.getOperator())){
            personNotify.setStatus(GroupNotifyEx.statusType.PUBLISH.code);
            buildPublishParams(personNotify, ssoUser);//构造发布通告的参数
        }
        PersonNotifyExample personNotifyExample = new PersonNotifyExample();
        personNotifyExample.createCriteria().andSpIdEqualTo(ssoUser.getSpId()).andIdEqualTo(commonNotifyEx.getId())
                .andDelEqualTo(MutiSiteConstants.NO_DEL);
        personNotifyMapper.updateByExampleSelective(personNotify, personNotifyExample);
    }

    private void delCommonNotifyRelations(GroupNotifyEx commonNotifyEx, SsoUser ssoUser){
        AttachmentsExample attachmentsExample = new AttachmentsExample();
        attachmentsExample.createCriteria().andNotifyIdEqualTo(commonNotifyEx.getId()).andSpIdEqualTo(ssoUser.getSpId());
        attachmentsMapper.deleteByExample(attachmentsExample);  //删除公共通知公告对应的附件
        NotifyRelationOrgExample notifyRelationOrgExample = new NotifyRelationOrgExample();
        notifyRelationOrgExample.createCriteria().andSpIdEqualTo(ssoUser.getSpId()).andNotifyIdEqualTo(commonNotifyEx.getId());
        notifyRelationOrgMapper.deleteByExample(notifyRelationOrgExample); //删除公共获得和组织的关联表
        GroupNotifyExample groupNotifyExample = new GroupNotifyExample();
        groupNotifyExample.createCriteria().andIdEqualTo(commonNotifyEx.getId()).andSpIdEqualTo(ssoUser.getSpId());
        groupNotifyMapper.deleteByExample(groupNotifyExample); //删除原来的数据
    }

    /**
     * 构造发布公告相关的发布参数
     * @param notify
     * @param ssoUser
     */
    private void buildPublishParams(Object notify, SsoUser ssoUser){
        UserInfo userInfo = ssoUser.getUserInfo();
        SpRole spRole = ssoUser.getCurRole();
        if(notify instanceof GroupNotifyEx){
            GroupNotifyEx commonNotifyEx = (GroupNotifyEx)notify;
            if(null == commonNotifyEx.getPublishTime()){
                commonNotifyEx.setPublishTime(new Date());
            }
            commonNotifyEx.setPublishPeopleId(userInfo.getId());
            commonNotifyEx.setPublishPeople(userInfo.getName());
            commonNotifyEx.setPublishOrganize(getOrgNameById(spRole.getGroupId(), ssoUser));
            commonNotifyEx.setPublishOrgId(spRole.getGroupId());
        }else if(notify instanceof  PersonNotify){
            PersonNotify personNotify = (PersonNotify)notify;
            if(null == personNotify.getPublishTime()){
                personNotify.setPublishTime(new Date());
            }
            personNotify.setPublishPeopleId(userInfo.getId());
            personNotify.setPublishPeople(userInfo.getName());
            personNotify.setPublishOrganize(getOrgNameById(spRole.getGroupId(), ssoUser));
            personNotify.setPublishOrgId(spRole.getGroupId());
        }
    }

    /**
     * 保存指定通知公告部门关系表(先删除，在增加)
     * @param groupNotifyEx
     * @param ssoUser
     */
    private void saveNotifyOrgRelation(GroupNotifyEx groupNotifyEx, SsoUser ssoUser){
        NotifyRelationPersonExample notifyPersonRelationExample = new NotifyRelationPersonExample();
        notifyPersonRelationExample.createCriteria().andSpIdEqualTo(ssoUser.getSpId()).andNotifyIdEqualTo(groupNotifyEx.getId());
        notifyRelationPersonMapper.deleteByExample(notifyPersonRelationExample);  //删除以前的关系表
        NotifyRelationOrgExample notifyRelationOrgExample = new NotifyRelationOrgExample();
        notifyRelationOrgExample.createCriteria().andNotifyIdEqualTo(groupNotifyEx.getId()).andSpIdEqualTo(ssoUser.getSpId());
        notifyRelationOrgMapper.deleteByExample(notifyRelationOrgExample);  //删除以前的关系表
        List<NotifyRelationOrg> notifyRelationOrgs = groupNotifyEx.getNotifyRelationOrgs();
        if(CollectionUtils.isNotEmpty(notifyRelationOrgs)){
            for(NotifyRelationOrg notifyRelationOrg:notifyRelationOrgs){
                notifyRelationOrg.setSpId(ssoUser.getSpId());
                notifyRelationOrg.setNotifyId(groupNotifyEx.getId());
                notifyRelationOrgMapper.insertSelective(notifyRelationOrg);
            }
        }
        if(GroupNotifyEx.statusType.PUBLISH.code.equals(groupNotifyEx.getOperator())){ //如果是发布
            sendOrgMsg(notifyRelationOrgs, ssoUser,"2",groupNotifyEx.getTitle()); //发送app消息
        }
    }

    /**
     * 保存指定通知公告人员关系表(先删除，在增加)
     * @param groupNotifyEx
     * @param ssoUser
     */
    private void saveNotifyPersonRelation(GroupNotifyEx groupNotifyEx, SsoUser ssoUser) throws NotifyException{
        NotifyRelationPersonExample notifyPersonRelationExample = new NotifyRelationPersonExample();
        notifyPersonRelationExample.createCriteria().andSpIdEqualTo(ssoUser.getSpId()).andNotifyIdEqualTo(groupNotifyEx.getId());
        notifyRelationPersonMapper.deleteByExample(notifyPersonRelationExample);  //删除以前的关系表
        NotifyRelationOrgExample notifyRelationOrgExample = new NotifyRelationOrgExample();
        notifyRelationOrgExample.createCriteria().andNotifyIdEqualTo(groupNotifyEx.getId()).andSpIdEqualTo(ssoUser.getSpId());
        notifyRelationOrgMapper.deleteByExample(notifyRelationOrgExample);  //删除以前的关系表
        List<NotifyRelationPerson> personRelations = groupNotifyEx.getNotifyPersonRelations();
        if(GroupNotifyEx.publishRangeType.PARTY.code.equals(groupNotifyEx.getPublishType())){ //发送到部门管理员
            personRelations = getManagerByGroupId(personRelations, ssoUser);  //获取部门的管理员
        }else if(GroupNotifyEx.publishRangeType.PERSON.code.equals(groupNotifyEx.getPublishType())){ //发送到本部门成员
            personRelations = getManagerOrg(personRelations, ssoUser);
        }
        if(CollectionUtils.isNotEmpty(personRelations)){
            for (NotifyRelationPerson notifyPersonRelation : personRelations){
                notifyPersonRelation.setId(StringUtil.getUUID());
                notifyPersonRelation.setNotifyId(groupNotifyEx.getId());
                notifyPersonRelation.setSpId(ssoUser.getSpId());
                notifyPersonRelation.setStatus(GroupNotifyEx.readStatusType.UNREAD.code);
                notifyRelationPersonMapper.insertSelective(notifyPersonRelation);
            }
        }
        if(GroupNotifyEx.statusType.PUBLISH.code.equals(groupNotifyEx.getOperator())){ //如果是发布操作
            sendMsg(personRelations, ssoUser,groupNotifyEx.getTitle()); //发送app消息
        }
    }

    /**
     * 获取本部门的部门id和名称
     * @param personRelations
     * @param ssoUser
     * @return
     */
    private List<NotifyRelationPerson> getManagerOrg(List<NotifyRelationPerson> personRelations, SsoUser ssoUser){
        if(CollectionUtils.isNotEmpty(personRelations)){
            SpRole spRole = ssoUser.getCurRole();
            for(NotifyRelationPerson notifyRelationPerson:personRelations){
                notifyRelationPerson.setOrgId(spRole.getGroupId());
                notifyRelationPerson.setOrgName(spRole.getGroupName());
            }
        }
        return personRelations;
    }

    /**
     * 获取节点的管理员
     * @param personRelations
     * @param ssoUser
     * @return
     */
    private List<NotifyRelationPerson> getManagerByGroupId(List<NotifyRelationPerson> personRelations, SsoUser ssoUser) throws NotifyException{
        logger.info("开始获取支部的管理员");
        if(CollectionUtils.isEmpty(personRelations)){
            return null;
        }
        List<NotifyRelationPerson> notifyRelationPersons = new ArrayList<>();
        for(NotifyRelationPerson notifyPersonRelation : personRelations){
            JSONObject jsonObject = apiService.queryManagerByGroupId(notifyPersonRelation.getOrgId(), ssoUser);
            if(null == jsonObject){
                NotifyRelationPerson notifyRelationPerson = new NotifyRelationPerson();
                notifyRelationPerson.setOrgId(notifyPersonRelation.getOrgId());
                notifyRelationPerson.setOrgName(notifyPersonRelation.getOrgName());
                notifyRelationPerson.setUserId("-1");
                notifyRelationPersons.add(notifyRelationPerson);
                continue;
            }
            String roleId = jsonObject.getString("id");
            JSONArray jsonArray = apiService.getMembersByRole(roleId, ssoUser);
            if(CollectionUtils.isEmpty(jsonArray)){
                NotifyRelationPerson notifyRelationPerson = new NotifyRelationPerson();
                notifyRelationPerson.setOrgId(notifyPersonRelation.getOrgId());
                notifyPersonRelation.setOrgName(notifyPersonRelation.getOrgName());
                notifyPersonRelation.setUserId("-1");
                notifyRelationPersons.add(notifyRelationPerson);
                continue;
            }
            for(int i = 0; i< jsonArray.size();i++){
                JSONObject userObject = jsonArray.getJSONObject(i);
                NotifyRelationPerson notifyRelationPerson = new NotifyRelationPerson();
                notifyRelationPerson.setUserId(userObject.getJSONObject("userMap").getString("userId"));
                notifyRelationPerson.setUserName(userObject.getString("name"));
                notifyRelationPerson.setOrgId(notifyPersonRelation.getOrgId());
                notifyRelationPerson.setOrgName(notifyPersonRelation.getOrgName());
                notifyRelationPerson.setPhone(userObject.getString("mdn"));
                notifyRelationPersons.add(notifyRelationPerson);
            }
        }
        logger.info("支部的管理员获取完毕");
        return notifyRelationPersons;
    }

    /**
     * 新增附件(先删除，在增加)
     * @param commonNotifyEx
     * @param ssoUser
     */
    private void insertAttachments(GroupNotifyEx commonNotifyEx, SsoUser ssoUser){
        AttachmentsExample attachmentsExample = new AttachmentsExample();
        attachmentsExample.createCriteria().andNotifyIdEqualTo(commonNotifyEx.getId()).andSpIdEqualTo(ssoUser.getSpId());
        attachmentsMapper.deleteByExample(attachmentsExample);  //删除以前的附件
        List<Attachments> attachmentses = commonNotifyEx.getAttachmentses();
        Date date = new Date();
        if(CollectionUtils.isNotEmpty(attachmentses)){
            for(Attachments attachments:attachmentses){
                attachments.setId(StringUtil.getUUID());
                attachments.setNotifyId(commonNotifyEx.getId());
                attachments.setCreateTime(date);
                attachments.setSpId(ssoUser.getSpId());
                attachments.setFileType(getFileType(attachments.getFileType()));//文件类型转换
                attachmentsMapper.insertSelective(attachments);
            }
        }
    }

    /**
     * 文件类型转换
     * @param type
     * @return
     */
    private String getFileType(String type){
        if(StringUtils.isEmpty(type)){
            return null;
        }
        switch (type){
            case "doc":
            case "docx":
                return "doc";
            case "ppt":
            case "pptx":
                return "ppt";
            case "xls":
            case "xlsx":
                return "xls";
            case "txt":
                return "txt";
            case "pdf":
                return "pdf";
            case "jpg":
            case "jpeg":
            case "png":
                return "img";
        }
        return "";
    }


    /**
     * 查询通知公告
     *
     * @param groupNotifyEx
     * @param page
     * @return
     */
    @Override
    public List<GroupNotifyEx> queryNotify(GroupNotifyEx groupNotifyEx, Page page) {
        PageHelper.startPage(page.getPageNo(),page.getPageSize());
        List<GroupNotifyEx> groupNotifyExes = groupNotifyMapper.queryNotify(groupNotifyEx);
        PageInfo<GroupNotifyEx> pageInfo = new PageInfo<>(groupNotifyExes);
        page.setTotalRecord(pageInfo.getTotal());
        return groupNotifyExes;
    }

    /**
     * 查询通知公告详情
     *
     * @param commonNotifyEx
     * @return
     */
    @Override
    public GroupNotifyEx detailNotify(GroupNotifyEx commonNotifyEx, SsoUser ssoUser) throws NotifyException{
        String range = commonNotifyEx.getRange();
        if(GroupNotifyEx.rangeType.COMMON.code.equals(range)){ //公共的通知公告
            commonNotifyEx = detailCommonNotify(commonNotifyEx, ssoUser);
        }else if(GroupNotifyEx.rangeType.PERSON.code.equals(range)){//指定的通知公告
            commonNotifyEx = detailPersonNotify(commonNotifyEx, ssoUser);
        }
        commonNotifyEx.setRange(range);
        List<Attachments> attachmentses = queryAttachments(commonNotifyEx);
        commonNotifyEx.setAttachmentses(attachmentses);
        return commonNotifyEx;
    }

    /**
     * 公共通知公告的详情
     * @param groupNotifyEx
     * @param ssoUser
     * @return
     */
    private GroupNotifyEx detailCommonNotify(GroupNotifyEx groupNotifyEx, SsoUser ssoUser) throws NotifyException{
        GroupNotifyExample groupNotifyExample = new GroupNotifyExample();
        groupNotifyExample.createCriteria().andIdEqualTo(groupNotifyEx.getId()).andSpIdEqualTo(ssoUser.getSpId())
                .andDelEqualTo(MutiSiteConstants.NO_DEL);
        List<GroupNotify> groupNotifies = groupNotifyMapper.selectByExample(groupNotifyExample);
        if(CollectionUtils.isEmpty(groupNotifies)){
            throw new NotifyException("没有查询到相关公告");
        }
        groupNotifyEx = (GroupNotifyEx)groupNotifies.get(0);
        String name = getOrgNameById(groupNotifyEx.getPublishOrgId(),ssoUser);
        NotifyRelationOrg notifyRelationOrg = new NotifyRelationOrg();
        notifyRelationOrg.setOrgName(name);
        List<NotifyRelationOrg> notifyRelationOrgs = new ArrayList<>();
        notifyRelationOrgs.add(notifyRelationOrg);
        groupNotifyEx.setNotifyRelationOrgs(notifyRelationOrgs);
        groupNotifyEx.setRange(GroupNotifyEx.rangeType.COMMON.code);
        return groupNotifyEx;
    }

    /**
     * 指定通知公告详情
     * @param groupNotifyEx
     * @param ssoUser
     * @return
     */
    private GroupNotifyEx detailPersonNotify(GroupNotifyEx groupNotifyEx, SsoUser ssoUser) throws NotifyException{
        PersonNotifyExample personNotifyExample = new PersonNotifyExample();
        personNotifyExample.createCriteria().andIdEqualTo(groupNotifyEx.getId())
                .andSpIdEqualTo(ssoUser.getSpId()).andDelEqualTo(MutiSiteConstants.NO_DEL);
        List<PersonNotify> personNotifies = personNotifyMapper.selectByExample(personNotifyExample);
        if(CollectionUtils.isEmpty(personNotifies)){
            throw new NotifyException("没有查询到相关公告");
        }
        String operator = groupNotifyEx.getOperator();
        groupNotifyEx = JSONObject.parseObject(JSONObject.toJSONString(personNotifies.get(0)),GroupNotifyEx.class);
        groupNotifyEx.setRange(groupNotifyEx.getPublishType());
        if(GroupNotifyEx.notifyRangeType.MEMBER.code.equals(groupNotifyEx.getNotifyType()) &&
                !GroupNotifyEx.publishRangeType.PERSON.code.equals(groupNotifyEx.getPublishType())){ //查询通知到的部门所有成员 只保存了部门关联
            NotifyRelationOrgExample notifyRelationOrgExample = new NotifyRelationOrgExample();
            notifyRelationOrgExample.createCriteria().andNotifyIdEqualTo(groupNotifyEx.getId()).andSpIdEqualTo(ssoUser.getSpId());
            groupNotifyEx.setNotifyRelationOrgs(notifyRelationOrgMapper.selectByExample(notifyRelationOrgExample));
        }else{ //查询通知到的人  包括按照部门的管理员发布和按照个人发布
            NotifyRelationPersonExample notifyPersonRelationExample = new NotifyRelationPersonExample();
            notifyPersonRelationExample.createCriteria().andNotifyIdEqualTo(groupNotifyEx.getId())
                    .andSpIdEqualTo(ssoUser.getSpId());
            List<NotifyRelationPerson> notifyPersonRelations = notifyRelationPersonMapper.selectByExample(notifyPersonRelationExample);
            groupNotifyEx.setNotifyRelationOrgs(personToOrg(notifyPersonRelations));
            if(GroupNotifyEx.notifyRangeType.MANAGER.code.equals(groupNotifyEx.getNotifyType()) &&
                    GroupNotifyEx.operatorType.EDIT.code.equals(operator)){ //部门管理员并且是编辑页的详情
                notifyPersonRelations = retainOrg(notifyPersonRelations);
            }
            if(GroupNotifyEx.publishRangeType.PERSON.code.equals(groupNotifyEx.getPublishType()) &&
                    GroupNotifyEx.operatorType.EDIT.code.equals(operator)){ //如果是发送到个人并且是编辑页面
                notifyPersonRelations = retainPerson(notifyPersonRelations);
            }
            groupNotifyEx.setNotifyPersonRelations(notifyPersonRelations);
        }
        return groupNotifyEx;
    }

    /**
     * 将公告和人的关联表里面的组织提取出来
     * @param notifyPersonRelations
     * @return
     */
    private List<NotifyRelationOrg> personToOrg(List<NotifyRelationPerson> notifyPersonRelations){
        List<NotifyRelationOrg> notifyRelationOrgs = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(notifyPersonRelations)){
            List<String> temp = new ArrayList<>();
            for (NotifyRelationPerson notifyRelationPerson:notifyPersonRelations){
                if(!temp.contains(notifyRelationPerson.getOrgId())){
                    temp.add(notifyRelationPerson.getOrgId());
                    NotifyRelationOrg notifyRelationOrg = new NotifyRelationOrg();
                    notifyRelationOrg.setOrgId(notifyRelationPerson.getOrgId());
                    notifyRelationOrg.setOrgName(notifyRelationPerson.getOrgName());
                    notifyRelationOrg.setNotifyRelationPersons( getPeronByOrg(notifyPersonRelations, notifyRelationOrg));
                    notifyRelationOrgs.add(notifyRelationOrg);
                }
            }
        }
        return notifyRelationOrgs;
    }

    /**
     * 根据组织赛选出组织对应的人
     * @param notifyPersonRelations
     * @param notifyRelationOrg
     * @return
     */
    private List<NotifyRelationPerson> getPeronByOrg(List<NotifyRelationPerson> notifyPersonRelations, NotifyRelationOrg notifyRelationOrg){
        List<NotifyRelationPerson> notifyRelationPersons = new ArrayList<>();
        String orgId = notifyRelationOrg.getOrgId();
        for (NotifyRelationPerson notifyRelationPerson:notifyPersonRelations){
            if("-1".equals(notifyRelationPerson.getUserId())){
                continue;
            }
            if(orgId.equals(notifyRelationPerson.getOrgId())){
                notifyRelationPersons.add(notifyRelationPerson);
            }
        }
        return notifyRelationPersons;
    }

    /**
     * 只保留人
     * @param notifyPersonRelations
     * @return
     */
    private List<NotifyRelationPerson>  retainPerson(List<NotifyRelationPerson> notifyPersonRelations){
        List<NotifyRelationPerson> notifyRelationPersons = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(notifyPersonRelations)){
            for (NotifyRelationPerson notifyRelationPerson:notifyPersonRelations){
                notifyRelationPerson.setId(notifyRelationPerson.getUserId());
                notifyRelationPerson.setOrgName(notifyRelationPerson.getUserName());
                notifyRelationPersons.add(notifyRelationPerson);
            }
        }
        return notifyRelationPersons;
    }

    /**
     * 只保留组织
     * @param notifyPersonRelations
     * @return
     */
    private List<NotifyRelationPerson>  retainOrg(List<NotifyRelationPerson> notifyPersonRelations){
        List<NotifyRelationPerson> notifyRelationPersons = new ArrayList<>();
        List<String> temp = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(notifyPersonRelations)){
            for (NotifyRelationPerson notifyRelationPerson:notifyPersonRelations){
                if(!temp.contains(notifyRelationPerson.getOrgId())){
                    temp.add(notifyRelationPerson.getOrgId());
                    notifyRelationPerson.setId(notifyRelationPerson.getOrgId());
                    notifyRelationPersons.add(notifyRelationPerson);
                }
            }
        }
        return notifyRelationPersons;
    }

    /**
     * 查询通知公告附件
     * @param commonNotifyEx
     * @return
     */
    public List<Attachments> queryAttachments(GroupNotifyEx commonNotifyEx){
        AttachmentsExample attachmentsExample = new AttachmentsExample();
        attachmentsExample.createCriteria().andNotifyIdEqualTo(commonNotifyEx.getId()).andSpIdEqualTo(commonNotifyEx.getSpId());
        return attachmentsMapper.selectByExample(attachmentsExample);
    }

    /**
     * 通知公告关闭
     *
     * @param commonNotifyEx
     * @param ssoUser
     */
    @Override
    @Transactional
    public void closeNotify(GroupNotifyEx commonNotifyEx, SsoUser ssoUser) throws NotifyException{
        String range = commonNotifyEx.getRange();
        if(GroupNotifyEx.rangeType.COMMON.code.equals(range)) { //公共的通知公告
            closeCommonNotify(commonNotifyEx, ssoUser);
        }else if(GroupNotifyEx.rangeType.PERSON.code.equals(range)){ //指定的通知公告
            closePersonNotify(commonNotifyEx, ssoUser);
        }
    }

    /**
     * 公共通知公告关闭
     * @param groupNotifyEx
     * @param ssoUser
     * @param ssoUser
     */
    private void closeCommonNotify(GroupNotifyEx groupNotifyEx, SsoUser ssoUser) throws NotifyException{
        GroupNotifyExample notifyExample = new GroupNotifyExample();
        notifyExample.createCriteria().andIdEqualTo(groupNotifyEx.getId()).
                andSpIdEqualTo(ssoUser.getSpId()).andDelEqualTo(MutiSiteConstants.NO_DEL);
        List<GroupNotify> groupNotifies = groupNotifyMapper.selectByExample(notifyExample);
        if(CollectionUtils.isEmpty(groupNotifies)){
            throw new NotifyException("没有查询到相关公告");
        }
        groupNotifyEx = (GroupNotifyEx)groupNotifies.get(0);
        if(!GroupNotifyEx.statusType.PUBLISH.code.equals(groupNotifyEx.getStatus())){  //如果状态不是已发布
            throw new NotifyException("已发布状态的公告才可以关闭");
        }
        GroupNotify groupNotify = new GroupNotify();
        groupNotify.setStatus(GroupNotifyEx.statusType.CLOSE.code);
        groupNotify.setUpdateTime(new Date());
        UserInfo userInfo = ssoUser.getUserInfo();
        groupNotify.setUpdateUserId(userInfo.getId());
        groupNotify.setUpdateUserName(userInfo.getName());
        groupNotifyMapper.updateByExampleSelective(groupNotify, notifyExample);
    }

    /**
     * 指定通知公共关闭
     * @param commonNotifyEx
     * @param ssoUser
     */
    private void closePersonNotify(GroupNotifyEx commonNotifyEx, SsoUser ssoUser) throws NotifyException{
        PersonNotifyExample personNotifyExample = new PersonNotifyExample();
        personNotifyExample.createCriteria().andIdEqualTo(commonNotifyEx.getId()).
                andSpIdEqualTo(ssoUser.getSpId()).andDelEqualTo(MutiSiteConstants.NO_DEL);
        List<PersonNotify> personNotifies = personNotifyMapper.selectByExample(personNotifyExample);
        if(CollectionUtils.isEmpty(personNotifies)){
            throw new NotifyException("没有查询到相关公告");
        }
        PersonNotify personNotify = personNotifies.get(0);
        if(!GroupNotifyEx.statusType.PUBLISH.code.equals(personNotify.getStatus())){  //如果状态不是已发布
            throw new NotifyException("已发布状态的公告才可以关闭");
        }
        personNotify = new PersonNotify();
        personNotify.setStatus(GroupNotifyEx.statusType.CLOSE.code);
        UserInfo userInfo = ssoUser.getUserInfo();
        personNotify.setUpdateTime(new Date());
        personNotify.setUpdateUserId(userInfo.getId());
        personNotify.setUpdateUserName(userInfo.getName());
        personNotifyMapper.updateByExampleSelective(personNotify, personNotifyExample);
    }

    /**
     * 通知公告列表发布
     *
     * @param commonNotifyEx
     * @param ssoUser
     * @throws cn.com.flaginfo.utils.NotifyException
     */
    @Override
    @Transactional
    public void pubListNotify(GroupNotifyEx commonNotifyEx, SsoUser ssoUser) throws NotifyException {
        String range = commonNotifyEx.getRange();
        if(GroupNotifyEx.rangeType.COMMON.code.equals(range)) { //公共的通知公告
            pubListCommonNotify(commonNotifyEx, ssoUser);
        }else if(GroupNotifyEx.rangeType.PERSON.code.equals(range)){ //指定的通知公告
            pubListPersonNotify(commonNotifyEx, ssoUser);
        }
    }

    /**
     * 列表发布公共的通知公告
     * @param groupNotifyEx
     * @param ssoUser
     * @throws NotifyException
     */
    private void pubListCommonNotify(GroupNotifyEx groupNotifyEx, SsoUser ssoUser) throws NotifyException{
        GroupNotifyExample groupNotifyExample = new GroupNotifyExample();
        groupNotifyExample.createCriteria().andIdEqualTo(groupNotifyEx.getId()).
                andSpIdEqualTo(ssoUser.getSpId()).andDelEqualTo(MutiSiteConstants.NO_DEL);
        List<GroupNotify> groupNotifies = groupNotifyMapper.selectByExample(groupNotifyExample);
        if(CollectionUtils.isEmpty(groupNotifies)){
            throw new NotifyException("没有查询到相关公告");
        }
        groupNotifyEx = (GroupNotifyEx)groupNotifies.get(0);
        if(GroupNotifyEx.statusType.PUBLISH.code.equals(groupNotifyEx.getStatus())){
            throw new NotifyException("该公告已经发布了");
        }
        GroupNotify groupNotify = new GroupNotify();
        Date date = new Date();
        UserInfo userInfo = ssoUser.getUserInfo();
        String orgId = ssoUser.getCurRole().getGroupId();
        groupNotify.setStatus(GroupNotifyEx.statusType.PUBLISH.code);
        groupNotify.setUpdateTime(date);
        groupNotify.setUpdateUserId(userInfo.getId());
        groupNotify.setUpdateUserName(userInfo.getName());
        if(null != groupNotifyEx.getPublishTime()){
            groupNotify.setPublishTime(groupNotifyEx.getPublishTime());
        }else{
            groupNotify.setPublishTime(date);
        }
        groupNotify.setPublishPeople(userInfo.getName());
        groupNotify.setPublishPeopleId(userInfo.getId());
        groupNotify.setPublishOrgId(orgId);
        String orgName = getOrgNameById(orgId, ssoUser);
        groupNotify.setPublishOrganize(orgName);
        groupNotifyMapper.updateByExampleSelective(groupNotify, groupNotifyExample);
        sendMsg(groupNotifyEx, ssoUser);//发送app消息
    }

    /**
     * 列表发布指定的通知公告
     * @param commonNotifyEx
     * @param ssoUser
     * @throws NotifyException
     */
    private void pubListPersonNotify(GroupNotifyEx commonNotifyEx, SsoUser ssoUser) throws NotifyException{
        PersonNotifyExample personNotifyExample = new PersonNotifyExample();
        personNotifyExample.createCriteria().andIdEqualTo(commonNotifyEx.getId()).
                andSpIdEqualTo(ssoUser.getSpId()).andDelEqualTo(MutiSiteConstants.NO_DEL);
        List<PersonNotify> personNotifies = personNotifyMapper.selectByExample(personNotifyExample);
        if(CollectionUtils.isEmpty(personNotifies)){
            throw new NotifyException("没有查询到相关公告");
        }
        PersonNotify personNotify = personNotifies.get(0);
        Date publishDate = personNotify.getPublishTime();
        if(GroupNotifyEx.statusType.PUBLISH.code.equals(personNotify.getStatus())){
            throw new NotifyException("已关闭的公告才能发布");
        }
        if(GroupNotifyEx.publishRangeType.PARTY.code.equals(personNotify.getPublishType()) &&
                GroupNotifyEx.notifyRangeType.MEMBER.code.equals(personNotify.getNotifyType())){
            NotifyRelationOrgExample notifyRelationOrgExample = new NotifyRelationOrgExample();
            notifyRelationOrgExample.createCriteria().andNotifyIdEqualTo(personNotify.getId()).andSpIdEqualTo(personNotify.getSpId());
            List<NotifyRelationOrg> notifyRelationOrgs = notifyRelationOrgMapper.selectByExample(notifyRelationOrgExample);
            sendOrgMsg(notifyRelationOrgs,ssoUser,"2",personNotify.getTitle());
        }else{
            NotifyRelationPersonExample notifyRelationPersonExample = new NotifyRelationPersonExample();
            notifyRelationPersonExample.createCriteria().andNotifyIdEqualTo(personNotify.getId()).andSpIdEqualTo(personNotify.getSpId());
            List<NotifyRelationPerson> notifyRelationPersons = notifyRelationPersonMapper.selectByExample(notifyRelationPersonExample); //查询公告相关的发送人
            sendMsg(notifyRelationPersons, ssoUser,personNotify.getTitle()); //发送消息
        }
        personNotify = new PersonNotify();
        Date date = new Date();
        UserInfo userInfo = ssoUser.getUserInfo();
        String orgId = ssoUser.getCurRole().getGroupId();
        personNotify.setStatus(GroupNotifyEx.statusType.PUBLISH.code);
        personNotify.setUpdateTime(date);
        personNotify.setUpdateUserId(userInfo.getId());
        personNotify.setUpdateUserName(userInfo.getName());
        if(null != publishDate){
            personNotify.setPublishTime(publishDate);
        }else {
            personNotify.setPublishTime(date);
        }
        personNotify.setPublishOrgId(orgId);
        personNotify.setPublishOrganize(ssoUser.getCurRole().getGroupName());
        personNotify.setPublishPeople(userInfo.getName());
        personNotify.setPublishPeopleId(userInfo.getId());
        personNotifyMapper.updateByExampleSelective(personNotify, personNotifyExample);
    }

    /**
     * 根据组id获取组name
     * @param groupId
     * @param ssoUser
     * @return
     */
    private String getOrgNameById(String groupId, SsoUser ssoUser){
        JSONObject jsonObject = apiService.queryGroupById(groupId, ssoUser);
        if(null != jsonObject){
            return jsonObject.getString("name");
        }
        return "";
    }

    /**
     * 通知公告删除
     *
     * @param commonNotifyEx
     * @param ssoUser
     * @throws cn.com.flaginfo.utils.NotifyException
     */
    @Override
    @Transactional
    public void delNotify(GroupNotifyEx commonNotifyEx, SsoUser ssoUser) throws NotifyException {
        List<GroupNotifyEx> notifyExes = commonNotifyEx.getNotifyExes();
        List<String> listTitles = new ArrayList<>();
        for(GroupNotifyEx notifyEx:notifyExes){
            String range = notifyEx.getRange();
            String title = "";
            if(GroupNotifyEx.rangeType.COMMON.code.equals(range)) { //公共的通知公告
                title = delCommonNotify(notifyEx, ssoUser);
            }else if(GroupNotifyEx.rangeType.PERSON.code.equals(range)){ //指定的通知公告
                title = delPersonNotify(notifyEx, ssoUser);
            }
            if(StringUtils.isNotEmpty(title)){
                listTitles.add(title);
            }
        }
        if(CollectionUtils.isNotEmpty(listTitles)){
            throw new NotifyException(JsonHelper.parseToJson(listTitles));
        }
    }

    /**
     * 公共通知公告删除
     * @param groupNotifyEx
     * @param ssoUser
     * @throws cn.com.flaginfo.utils.NotifyException
     */
    private String delCommonNotify(GroupNotifyEx groupNotifyEx, SsoUser ssoUser) throws NotifyException{
        GroupNotifyExample groupNotifyExample = new GroupNotifyExample();
        groupNotifyExample.createCriteria().andIdEqualTo(groupNotifyEx.getId()).
                andSpIdEqualTo(ssoUser.getSpId()).andDelEqualTo(MutiSiteConstants.NO_DEL);
        List<GroupNotify> groupNotifies = groupNotifyMapper.selectByExample(groupNotifyExample);
        if(CollectionUtils.isNotEmpty(groupNotifies)){
            GroupNotify groupNotify = groupNotifies.get(0);
            if(GroupNotifyEx.statusType.PUBLISH.code.equals(groupNotify.getStatus())){ //此公告是发布状态
                return groupNotify.getTitle();
            }
        }
        GroupNotify groupNotify = new GroupNotify();
        groupNotify.setDel(MutiSiteConstants.DEL);
        groupNotifyMapper.updateByExampleSelective(groupNotify, groupNotifyExample);
        return "";
    }

    /**
     * 指定通知公告删除
     * @param commonNotifyEx
     * @param ssoUser
     * @throws NotifyException
     */
    private String delPersonNotify(GroupNotifyEx commonNotifyEx, SsoUser ssoUser) throws NotifyException{
        PersonNotifyExample personNotifyExample = new PersonNotifyExample();
        personNotifyExample.createCriteria().andIdEqualTo(commonNotifyEx.getId()).
                andSpIdEqualTo(ssoUser.getSpId()).andDelEqualTo(MutiSiteConstants.NO_DEL);
        List<PersonNotify> personNotifies = personNotifyMapper.selectByExample(personNotifyExample);
        if(CollectionUtils.isNotEmpty(personNotifies)){
            PersonNotify personNotify = personNotifies.get(0);
            if(GroupNotifyEx.statusType.PUBLISH.code.equals(personNotify.getStatus())){ //此公告是发布状态
                return personNotify.getTitle();
            }
        }
        PersonNotify personNotify = new PersonNotify();
        personNotify.setDel(MutiSiteConstants.DEL);
        personNotifyMapper.updateByExampleSelective(personNotify, personNotifyExample);
        return "";
    }

    /**
     * 新建页面通知公告发布
     *
     * @param commonNotifyEx
     * @param ssoUser
     * @throws cn.com.flaginfo.utils.NotifyException
     */
    @Override
    @Transactional
    public void pubNewNotify(GroupNotifyEx commonNotifyEx, SsoUser ssoUser) throws NotifyException {
        saveNotify(commonNotifyEx, ssoUser);// 保存通知公告
    }

    /**
     * 指定通知公告发送到管理员和指定人发送app消息
     * @param notifyRelationPersonsx
     * @param ssoUser
     */
    private void sendMsg(List<NotifyRelationPerson> notifyRelationPersonsx, SsoUser ssoUser, String notifyTitle){
       if(CollectionUtils.isEmpty(notifyRelationPersonsx)){
           return;
       }
        String notifyId = notifyRelationPersonsx.get(0).getNotifyId();
        JSONObject jsonObject = new JSONObject();
        List<String> phones = new ArrayList<>();
        for(NotifyRelationPerson notifyRelationPerson: notifyRelationPersonsx){
            String phone = notifyRelationPerson.getPhone();
            if(StringUtils.isNotEmpty(phone)){
                if(!phones.contains(phone)){
                    phones.add(phone);
                }
            }
        }
        jsonObject.put("mdn", StringUtils.join(phones, ";"));
        UserInfo userInfo = ssoUser.getUserInfo();
        try {
            NotifySendUtils.doSend(userInfo.getId(),notifyTitle,ssoUser.getSpId(),
                    SystemConfig.getString(MutiSiteConstants.APP_CONTEXT_DOMAIN)+"page/notice/detail.html?id="+notifyId+"&type=3",jsonObject);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 指定通知公告发送到部门发送app消息
     * @param notifyRelationOrgs
     * @param ssoUser
     */
    private void sendOrgMsg(List<NotifyRelationOrg> notifyRelationOrgs, SsoUser ssoUser,String type,String notifyTitle){
        if(CollectionUtils.isEmpty(notifyRelationOrgs)){
            return;
        }
        UserInfo userInfo = ssoUser.getUserInfo();
        JSONObject jsonObject = new JSONObject();
        List<String> orgIds = new ArrayList<>();
        String notifyId = notifyRelationOrgs.get(0).getNotifyId();
        for(NotifyRelationOrg notifyRelationOrg:notifyRelationOrgs){
            String orgId = notifyRelationOrg.getOrgId();
            if(StringUtils.isNotEmpty(orgId)){
                if(!orgIds.contains(orgId)){
                    orgIds.add(orgId);
                }
            }
        }
        jsonObject.put("contactsGroupList",orgIds);
        try {
            NotifySendUtils.doSend(userInfo.getId(),notifyTitle,ssoUser.getSpId(),
                    SystemConfig.getString(MutiSiteConstants.APP_CONTEXT_DOMAIN)+"page/notice/detail.html?id="+notifyId+"&type="+type,jsonObject);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 公共的通知公告发送消息
     * @param groupNotifyEx
     * @param ssoUser
     * @throws NotifyException
     */
    private void sendMsg(GroupNotifyEx groupNotifyEx, SsoUser ssoUser) throws NotifyException{
        String groupId = groupNotifyEx.getPublishOrgId();
        UserInfo userInfo = ssoUser.getUserInfo();
        Set<String> orgIds = new HashSet<>();
        orgIds.add(groupId);
        JSONArray jsonArray = apiService.getChildrenListByGroup(groupId, ssoUser);
        if(CollectionUtils.isNotEmpty(jsonArray)){
            int size = jsonArray.size();
            for(int i = 0; i < size; i++){
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                orgIds.add(jsonObject.getString("id"));
            }
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("contactsGroupList", orgIds);
        try {
            NotifySendUtils.doSend(userInfo.getId(),groupNotifyEx.getTitle(),ssoUser.getSpId(),
                    SystemConfig.getString(MutiSiteConstants.APP_CONTEXT_DOMAIN)+"page/notice/detail.html?id="+groupNotifyEx.getId()+"&type="+1,jsonObject);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 查询武威门户的通知公告
     *
     * @param spId
     * @param page
     * @return
     */
    @Override
    public List<GroupNotify> queryHomeNotify(String spId, Page page) {
        GroupNotifyExample groupNotifyExample = new GroupNotifyExample();
        groupNotifyExample.setOrderByClause("publish_time desc");
        if(StringUtils.isEmpty(spId)){
            spId = SystemConfig.getString("wwdj.sp.id");
        }
        groupNotifyExample.createCriteria().andSpIdEqualTo(spId).andPublishOrgIdEqualTo(SystemConfig.getString("wwdj.root.id"));
        PageHelper.startPage(page.getPageNo(), page.getPageSize());
        List<GroupNotify> groupNotifies= groupNotifyMapper.selectByExample(groupNotifyExample);
        PageInfo<GroupNotify> pageInfo = new PageInfo<>(groupNotifies);
        page.setResults(groupNotifies);
        page.setTotalRecord(pageInfo.getTotal());
        return groupNotifies;
    }


    /**
     * 查询公共通知公告详情
     *
     * @param commonNotifyEx
     * @return
     */
    @Override
    public GroupNotifyEx detailHomeNotify(GroupNotifyEx commonNotifyEx) {
        GroupNotifyEx groupNotifyEx = (GroupNotifyEx)groupNotifyMapper.selectByPrimaryKey(commonNotifyEx.getId());
        if(null == groupNotifyEx){
            return commonNotifyEx;
        }
        List<Attachments> attachmentses = queryAttachments(groupNotifyEx);
        groupNotifyEx.setAttachmentses(attachmentses);
        return groupNotifyEx;
    }
}
