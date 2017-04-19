package cn.com.flaginfo.service;

import cn.com.flaginfo.pojo.Attachments;
import cn.com.flaginfo.pojo.GroupNotify;
import cn.com.flaginfo.pojo.SsoUser;
import cn.com.flaginfo.pojo.ex.GroupNotifyEx;
import cn.com.flaginfo.utils.NotifyException;
import cn.com.flaginfo.utils.Page;

import java.util.List;

/**
 * Created by lenovo on 2016/11/29.
 */
public interface GroupNotifyService {

    /**
     * 保存通知公告
     * @param commonNotifyEx
     * @param ssoUser
     */
    void saveNotify(GroupNotifyEx commonNotifyEx, SsoUser ssoUser) throws NotifyException;

    /**
     * 查询通知公告
     * @param commonNotifyEx
     * @param page
     * @return
     */
    List<GroupNotifyEx> queryNotify(GroupNotifyEx commonNotifyEx,Page page);

    /**
     * 查询通知公告详情
     * @param commonNotifyEx
     * @param ssoUser
     * @return
     */
    GroupNotifyEx detailNotify(GroupNotifyEx commonNotifyEx, SsoUser ssoUser) throws NotifyException;

    /**
     * 通知公告关闭
     * @param commonNotifyEx
     * @throws NotifyException
     */
    void closeNotify(GroupNotifyEx commonNotifyEx, SsoUser ssoUser) throws NotifyException;

    /**
     * 通知公告列表发布
     * @param commonNotifyEx
     * @param ssoUser
     * @throws NotifyException
     */
    void pubListNotify(GroupNotifyEx commonNotifyEx, SsoUser ssoUser) throws NotifyException;

    /**
     * 通知公告删除
     * @param commonNotifyEx
     * @param ssoUser
     * @throws NotifyException
     */
    void delNotify(GroupNotifyEx commonNotifyEx, SsoUser ssoUser) throws NotifyException;

    /**
     * 新建页面通知公告发布
     * @param commonNotifyEx
     * @param ssoUser
     * @throws NotifyException
     */
    void pubNewNotify(GroupNotifyEx commonNotifyEx, SsoUser ssoUser) throws NotifyException;
    
    
    List<Attachments> queryAttachments(GroupNotifyEx commonNotifyEx);

    /**
     * 查询武威门户的通知公告
     * @param spId
     * @param page
     * @return
     */
    List<GroupNotify> queryHomeNotify(String spId, Page page);


    /**
     * 查询公共通知公告详情
     * @param commonNotifyEx
     * @return
     */
    GroupNotifyEx detailHomeNotify(GroupNotifyEx commonNotifyEx);
}
