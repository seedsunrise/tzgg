package cn.com.flaginfo.service;

import cn.com.flaginfo.pojo.SsoUser;
import cn.com.flaginfo.utils.ApiClientUtil;
import cn.com.flaginfo.utils.MutiSiteConstants;
import cn.com.flaginfo.utils.NotifyException;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

/**
 * Created by lenovo on 2016/11/30.
 */
@Service("apiService")
public class ApiService {

    /**
     * 根据节点id查询节点基础信息
     * @param groupId
     * @param ssoUser
     * @return
     */
    public JSONObject queryGroupById(String groupId, SsoUser ssoUser){
        JSONObject jsonObject = buildCommonParams(ssoUser);
        jsonObject.put("id",groupId);
        jsonObject =  ApiClientUtil.post(MutiSiteConstants.CONTACTS_GROUP_GET_BY_ID, jsonObject);
        if(!MutiSiteConstants.SUCCESS_STR.equals(jsonObject.getString("returnCode"))){
            return null;
        }
        return jsonObject.getJSONObject("group");
    }

    /**
     * 根据节点查询节点管理员
     * @param groupId
     * @param ssoUser
     * @return
     */
    public JSONObject queryManagerByGroupId(String groupId, SsoUser ssoUser) throws NotifyException{
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("spId", ssoUser.getSpId());
        jsonObject.put("groupId", groupId);
        String[] types = new String[]{"1"};
        jsonObject.put("types", types);
        jsonObject =  ApiClientUtil.post(MutiSiteConstants.ROLE_LIST, jsonObject);
        if(!MutiSiteConstants.SUCCESS_STR.equals(jsonObject.getString("returnCode"))){
            throw new NotifyException("获取支部管理员失败,请重试");
        }
        JSONArray jsonArray = jsonObject.getJSONArray("list");
        if(CollectionUtils.isEmpty(jsonArray)){
            return null;
        }
        return jsonArray.getJSONObject(0);
    }

    /**
     * 根据角色id查询符合这个角色的成员
     * @param roleId
     * @param ssoUser
     * @return
     */
    public JSONArray getMembersByRole(String roleId, SsoUser ssoUser) throws NotifyException{
        JSONObject param = new JSONObject();
        param.put("spId", ssoUser.getSpId());
        param.put("contactsId", ssoUser.getContactsId());
        JSONArray jsonArray = new JSONArray();
        JSONObject userMap = new JSONObject();
        JSONObject user = new JSONObject();
        String[] roleIds = new String[]{roleId};
        user.put("roleId",roleIds);
        userMap.put("userMap",user);
        jsonArray.add(userMap);
        param.put("list", jsonArray);
        JSONObject jsonObject= ApiClientUtil.post("contacts_member_list_all",param);
        if(!MutiSiteConstants.SUCCESS_STR.equals(jsonObject.getString("returnCode"))){
            throw new NotifyException("获取支部管理员失败,请重试");
        }
        jsonArray = jsonObject.getJSONArray("list");
        if(CollectionUtils.isEmpty(jsonArray)){
            return null;
        }
        return jsonArray;
    }

    /**
     * 获取节点下的所有节点
     * @param groupId
     * @param ssoUser
     * @return
     * @throws NotifyException
     */
    public JSONArray getChildrenListByGroup(String groupId, SsoUser ssoUser) throws NotifyException{
        JSONObject params = buildCommonParams(ssoUser);
        params.put("userId,",ssoUser.getUserInfo().getId());
        params.put("groupId",groupId);
        params.put("tagAttr","1");
        params.put("type","1");
        JSONObject jsonObject= ApiClientUtil.post(MutiSiteConstants.CONTACTS_GROUP_CHILDREN_LIST,params);
        if(!MutiSiteConstants.SUCCESS_STR.equals(jsonObject.getString("returnCode"))){
            throw new NotifyException("获取组织失败，请重试");
        }
        JSONArray jsonArray = jsonObject.getJSONArray("list");
        if(CollectionUtils.isEmpty(jsonArray)){
            return null;
        }
        return jsonArray;
    }

    /**
     * 构造公共参数
     * @param ssoUser
     * @return
     */
    private JSONObject buildCommonParams(SsoUser ssoUser){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("spId", ssoUser.getSpId());
        jsonObject.put("contactsId", ssoUser.getContactsId());
        return  jsonObject;
    }
}
