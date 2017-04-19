package cn.com.flaginfo.utils;

import cn.com.flaginfo.pojo.SpInfo;
import cn.com.flaginfo.pojo.SpRole;
import cn.com.flaginfo.pojo.SsoUser;
import cn.com.flaginfo.pojo.UserInfo;

import cn.com.flaginfo.service.ApiService;
import com.alibaba.fastjson.JSON;

import com.alibaba.fastjson.JSONObject;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import java.nio.channels.SeekableByteChannel;
import java.util.Map;

/**
 * PC端获取当前登录信息
 * 
 * @author Administrator
 *
 */
public class AdminSSOUserUtils {
	
	private static Logger log = Logger.getLogger(AdminSSOUserUtils.class);

	public static SsoUser getAdminSsoUser(HttpServletRequest request) {
		return getAdminSsoUser(request.getSession());
	}
	@SuppressWarnings("unchecked")
	public static SsoUser getAdminSsoUser( HttpSession session ) {
		if(session.getAttribute("ssoUser")!=null){
			return (SsoUser) session.getAttribute("ssoUser");
		}
		Map<String, Object> ssoUesrMap = (Map<String, Object>) session.getAttribute("sso_user");
		UserInfo userInfo = null;
		SsoUser ssoUser = null;
		if(ssoUesrMap!=null){
			try{
				userInfo = (UserInfo) JSON.parseObject(JSON.toJSONString(ssoUesrMap.get("userInfo")), UserInfo.class);
				SpInfo spInfo = (SpInfo)JSON.parseObject(JSON.toJSONString(ssoUesrMap.get("loginSpInfo")), SpInfo.class);
				ssoUser = new SsoUser();
				ssoUser.setUserInfo(userInfo);
				ssoUser.setLoginSpInfo(spInfo);
				Map loginSpMap = (Map)ssoUesrMap.get("loginSpInfo");
				//新增用户当前角色2016-4-28
				SpRole curRole = JSON.parseObject(JSON.toJSONString(loginSpMap.get("curRole")), SpRole.class);
				ssoUser.setContactsId(curRole.getContactsId());
				ssoUser.setSpId(spInfo.getId());
				//groupName
				curRole.setGroupName(getOrgNameById(curRole.getGroupId(),ssoUser));
				ssoUser.setCurRole(curRole);
			}catch(Exception e){
				log.error("get sso user,error msg:"+e.getMessage());
			}
		}
		session.setAttribute("ssoUser", ssoUser);
		return ssoUser;
	}

	/**
	 * 获取用户当前的角色id
	 * @param request
	 * @return
	 */
	public static String getRoleId(HttpServletRequest request){
		SsoUser ssoUser = getAdminSsoUser(request);
		return ssoUser.getCurRole().getRoleId();
	}

	/**
	 * 获取当前角色的id类型
	 * @param request
	 * @return
	 */
	public static String getRoleType(HttpServletRequest request){
		SsoUser ssoUser = getAdminSsoUser(request);
		return ssoUser.getCurRole().getRoleType();
	}

	private static String getOrgNameById(String groupId, SsoUser ssoUser){
		JSONObject jsonObject = buildCommonParams(ssoUser);
		jsonObject.put("id",groupId);
		jsonObject =  ApiClientUtil.post(MutiSiteConstants.CONTACTS_GROUP_GET_BY_ID, jsonObject);
		try {
			if(!MutiSiteConstants.SUCCESS_STR.equals(jsonObject.getString("returnCode"))){
				return null;
			}
			jsonObject = jsonObject.getJSONObject("group");
			if(null != jsonObject){
				return jsonObject.getString("name");
			}
		} catch (Exception e) {
			log.error("错误信息：",e);
			return "";
		}
		
		return "";
	}

	/**
	 * 构造公共参数
	 * @param ssoUser
	 * @return
	 */
	private static JSONObject buildCommonParams(SsoUser ssoUser){
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("spId", ssoUser.getSpId());
		jsonObject.put("contactsId", ssoUser.getContactsId());
		return  jsonObject;
	}

}
