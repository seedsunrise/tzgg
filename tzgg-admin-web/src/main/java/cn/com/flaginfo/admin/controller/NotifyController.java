package cn.com.flaginfo.admin.controller;

import cn.com.flaginfo.pojo.SsoUser;
import cn.com.flaginfo.pojo.ex.GroupNotifyEx;
import cn.com.flaginfo.service.GroupNotifyService;
import cn.com.flaginfo.utils.AdminSSOUserUtils;
import cn.com.flaginfo.utils.HeadHelper;
import cn.com.flaginfo.utils.NotifyException;
import cn.com.flaginfo.utils.Page;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by chenzp on 2016/11/29.
 * 通知公告
 */
@Controller
@RequestMapping("notify")
public class NotifyController {

    @Autowired
    private GroupNotifyService groupNotifyService;

    /**
     * 查询通知公告
     * @param commonNotifyEx
     * @param page
     * @param request
     * @return
     */
    @RequestMapping("queryNotify")
    @ResponseBody
    public Object queryNotify(GroupNotifyEx commonNotifyEx, Page page, HttpServletRequest request){
        SsoUser ssoUser = AdminSSOUserUtils.getAdminSsoUser(request);
        commonNotifyEx.setSpId(ssoUser.getSpId());
        commonNotifyEx.setPublishOrgId(ssoUser.getCurRole().getGroupId());
        commonNotifyEx.setRoleType(ssoUser.getCurRole().getRoleType());
        List<GroupNotifyEx> commonNotifyExes = groupNotifyService.queryNotify(commonNotifyEx, page);
        page.setResults(commonNotifyExes);
        return HeadHelper.response(page);
    }

    /**
     * 保存通知公告
     * @param groupNotifyEx
     * @param request
     * @return
     */
    @RequestMapping("saveNotify")
    @ResponseBody
    public Object saveNotify(@RequestBody GroupNotifyEx groupNotifyEx, HttpServletRequest request){
        try {
            SsoUser ssoUser = AdminSSOUserUtils.getAdminSsoUser(request);
            groupNotifyEx.setOperator(GroupNotifyEx.statusType.SAVE.code);
            groupNotifyService.saveNotify(groupNotifyEx, ssoUser);
            return HeadHelper.response("");
        } catch (NotifyException e) {
            return HeadHelper.errResponse(e.getMessage());
        }
    }

    /**
     * 通知公共详情
     * @param commonNotifyEx
     * @param request
     * @return
     */
    @RequestMapping("detailNotify")
    @ResponseBody
    public Object detailNotify(GroupNotifyEx commonNotifyEx, HttpServletRequest request){
        try {
            SsoUser ssoUser = AdminSSOUserUtils.getAdminSsoUser(request);
            commonNotifyEx.setSpId(ssoUser.getSpId());
            commonNotifyEx = groupNotifyService.detailNotify(commonNotifyEx, ssoUser);
            return HeadHelper.response(commonNotifyEx);
        } catch (Exception e) {
            return HeadHelper.errResponse(e.getMessage());
        }
    }

    /**
     * 通知公告编辑页
     * @param commonNotifyEx
     * @param request
     * @return
     */
    @RequestMapping("editNotify")
    @ResponseBody
    public Object editNotify(GroupNotifyEx commonNotifyEx, HttpServletRequest request){
        try {
            SsoUser ssoUser = AdminSSOUserUtils.getAdminSsoUser(request);
            commonNotifyEx.setSpId(ssoUser.getSpId());
            commonNotifyEx.setOperator(GroupNotifyEx.operatorType.EDIT.code);
            commonNotifyEx = groupNotifyService.detailNotify(commonNotifyEx,ssoUser);
            return HeadHelper.response(commonNotifyEx);
        } catch (Exception e) {
            return HeadHelper.errResponse(e.getMessage());
        }
    }

    /**
     * 通知公告关闭
     * @param commonNotifyEx
     * @param request
     * @return
     */
    @RequestMapping("closeNotify")
    @ResponseBody
    public Object closeNotify(GroupNotifyEx commonNotifyEx, HttpServletRequest request){
        try {
            SsoUser ssoUser = AdminSSOUserUtils.getAdminSsoUser(request);
            groupNotifyService.closeNotify(commonNotifyEx, ssoUser);
            return HeadHelper.response("");
        } catch (NotifyException e) {
            return HeadHelper.errResponse(e.getMessage());
        }
    }

    /**
     * 列表上的通知公告发布
     * @param commonNotifyEx
     * @param request
     * @return
     */
    @RequestMapping("pubListNotify")
    @ResponseBody
    public Object pubListNotify(GroupNotifyEx commonNotifyEx, HttpServletRequest request){
        try {
            SsoUser ssoUser = AdminSSOUserUtils.getAdminSsoUser(request);
            groupNotifyService.pubListNotify(commonNotifyEx, ssoUser);
            return HeadHelper.response("");
        } catch (NotifyException e) {
            return HeadHelper.errResponse(e.getMessage());
        }
    }

    /**
     * 删除通知公告
     * @param commonNotifyEx
     * @param request
     * @return
     */
    @RequestMapping("delNotify")
    @ResponseBody
   public Object delNotify(@RequestBody GroupNotifyEx commonNotifyEx, HttpServletRequest request){
       try {
           SsoUser ssoUser = AdminSSOUserUtils.getAdminSsoUser(request);
           groupNotifyService.delNotify(commonNotifyEx, ssoUser);
           return HeadHelper.response("");
       } catch (NotifyException e) {
           return HeadHelper.errResponse(e.getMessage());
       }
   }

    /**
     * 新建页面通知公告发布
     * @param commonNotifyEx
     * @param request
     * @return
     */
    @RequestMapping("pubNewNotify")
    @ResponseBody
    public Object pubNewNotify(@RequestBody GroupNotifyEx commonNotifyEx, HttpServletRequest request){
        try {
            SsoUser ssoUser = AdminSSOUserUtils.getAdminSsoUser(request);
            commonNotifyEx.setOperator(GroupNotifyEx.statusType.PUBLISH.code);
            groupNotifyService.pubNewNotify(commonNotifyEx, ssoUser);
            return HeadHelper.response("");
        } catch (NotifyException e) {
            return HeadHelper.errResponse(e.getMessage());
        }
    }

    /**
     * 判断当前用户的角色类型
     * @param request
     * @return
     */
    @RequestMapping("isManager")
    @ResponseBody
    public Object isManager(HttpServletRequest request){
        SsoUser ssoUser = AdminSSOUserUtils.getAdminSsoUser(request);
        Map map = new HashMap();
        map.put("roleType",ssoUser.getCurRole().getRoleType());
        return HeadHelper.response(map);
    }

    /**
     * 查询武威党建门户通知公告
     * @param spId
     * @param page
     * @return
     */
    @RequestMapping("queryHomeNotify")
    @ResponseBody
    public Object queryHomeNotify(String spId, Page page){
        groupNotifyService.queryHomeNotify(spId, page);
        return HeadHelper.response(page);
    }

    @RequestMapping("queryHomeNotifyPage")
    @ResponseBody
    public Object queryHomeNotifyPage(Page page){
        groupNotifyService.queryHomeNotify(null, page);
        return HeadHelper.response(page);
    }

    /**
     * 查询公告详情
     * @param commonNotifyEx
     * @return
     */
    @RequestMapping("detailHomeNotify")
    @ResponseBody
    public Object detailHomeNotify(GroupNotifyEx commonNotifyEx){
        commonNotifyEx = groupNotifyService.detailHomeNotify(commonNotifyEx);
        return HeadHelper.response(commonNotifyEx);
    }

}
