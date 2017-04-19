package cn.com.flaginfo.app.filter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import cn.com.flaginfo.commons.utils.config.SystemConfig;
import cn.com.flaginfo.pojo.SpInfo;
import cn.com.flaginfo.pojo.SpRole;
import cn.com.flaginfo.pojo.SsoUser;
import cn.com.flaginfo.pojo.UserInfo;
import cn.com.flaginfo.umsapp.oauth.OAuthUtils;
import cn.com.flaginfo.utils.ApiClientUtil;
import cn.com.flaginfo.utils.MutiSiteConstants;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class AppSSOFilter implements Filter {

	private Logger log = Logger.getLogger(AppSSOFilter.class);
	private static final String USER_LOGIN_KEY = "user_login_key_info";

	private static Map<String,JSONArray> sp_user_cache = new ConcurrentHashMap<String,JSONArray>(10);

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {

	}

	/**
	 * 用户登录过滤，如果获取到用户信息，则放入session{_sso_user=SsoUser},有无获取到用户信息流程都会继续进行
	 */
	@Override
	@SuppressWarnings("unchecked")
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest)request;
		log.debug("======请求的地址(qing  qiu de  di zhi) :======="+ req.getServletPath());
		checkSession(req);
		HttpSession session = req.getSession();
		SsoUser ssoUser = (SsoUser)(session==null?null:session.getAttribute("_sso_user"));

		if(ssoUser == null){//可能是第一次进入
			String userJson = null;
			try{
				log.info("-----------开始调用OAuthUtils.getOAuthInfo接口----------");
				userJson = OAuthUtils.getOAuthInfo(req);
				log.info("-----------结束调用OAuthUtils.getOAuthInfo接口----------");
				//userJson="{\"name\":\"陈志攀\",\"gender\":\"M\",\"memberId\":\"57d2264a8e82411ae99b8c06\",\"avatar\":\"http://echat-sit.oss-cn-hangzhou.aliyuncs.com/echat_sit/avatar/57d2264a8e82411ae99b8c06?_d=1474425728000\",\"corp\":{\"icon\":\"http://echat-sit.oss-cn-hangzhou.aliyuncs.com/echat_sit/corp-icon/57ce8fe38e82417b9b5466e2\",\"spId\":\"16090611415110000319\",\"shortName\":\"党建业务测试账号1\",\"corpId\":\"57ce8fe38e82417b9b5466e2\",\"name\":\"党建业务测试账号1\"},\"success\":true,\"ext\":[],\"errcode\":0,\"mobile\":\"15982066774\"}";
				if(log.isInfoEnabled()){
					System.out.println("//==================用户信息============================//");
					System.out.println("*");
					System.out.println("*");
					System.out.println("*");
					System.out.println("*");
					System.out.println("*");
					System.out.println("*");
					System.out.println(userJson);
					System.out.println("*");
					System.out.println("*");
					System.out.println("*");
					System.out.println("*");
					System.out.println("*");
					System.out.println("*");
					System.out.println("*");
					System.out.println("//==================用户信息============================//");
					log.info("oauth result:"+userJson);
				}
			}catch(Exception e){
				e.printStackTrace();
				log.error(e.getMessage(), e);
			}
			if(userJson != null){
				try{
					//获取用户，并把他放入session中
					log.info("--------------开始调用getSsoUser-------------");
					getSsoUser(userJson,req);
					log.info("--------------结束调用getSsoUser-------------");
				}catch(Exception e){
					log.error(e.getMessage(), e);
				}
			}else{
				log.error("OAuth error,no user information found!");
			}

		}

		chain.doFilter(request, response);
	}

	@Override
	public void destroy() {
		if(sp_user_cache != null){
			sp_user_cache.clear();
			sp_user_cache = null;
		}
	}

	//用来处理客户端切换登录用户后，session还是原来的session的问题
	private void checkSession(HttpServletRequest request){
		//登录参数
		String userLoginKey = getUserLoginKey(request);
		if(userLoginKey != null){
			HttpSession session = request.getSession();
			if(session != null){
				String loginKey = (String)session.getAttribute(USER_LOGIN_KEY);
				if(loginKey != null && !userLoginKey.equals(loginKey)){
					session.removeAttribute("_sso_user");
					session.invalidate();
				}
			}
		}
	}

	private String getUserLoginKey(HttpServletRequest request){
		String key = request.getParameter("__mId");
		String host = request.getParameter("__h");
		if(key != null || host != null){
			StringBuilder strBuf = new StringBuilder();
			strBuf.append("__mId:");
			strBuf.append(key);
			strBuf.append(";");
			strBuf.append("__h");
			strBuf.append(host);
			return strBuf.toString();
		}
		return null;
	}

	private void getUserInfo(String phone,SpInfo sp,String contactsId,SsoUser ssoUser){
		UserInfo userInfo = null;
		StringBuffer sb=null;
		if(phone == null){
			return;
		}
		JSONArray userArray = getUserInfoByPhone(phone,sp.getId(),contactsId);
		
		if(userArray!=null && userArray.size() > 0){
			JSONObject userObject = userArray.getJSONObject(userArray.size()-1);
			userInfo = new UserInfo();
			String userId = "";
			userInfo.setName(userObject.getString("name"));
			JSONObject userMap = userObject.getJSONObject("userMap");
			userId = userMap.getString("userId");
			if(StringUtils.isEmpty(userId)){
				JSONObject yxtMap = userObject.getJSONObject("yxtMap");
				userId = yxtMap.getString("userId");
			}
			userInfo.setId(userId);
			userInfo.setPersonalPhoneNo(phone);
			userInfo.setMemberId(userObject.getString("id"));
			/**旧代码两次调用contacts_member_get_by_mdn接口，合并后代码如下**/
			//获取权限信息
			JSONArray jsonarry = (JSONArray)userMap.get("roleId");
			sb = new StringBuffer();
			if (jsonarry != null && jsonarry.size() > 0) {
				for (int i = 0; i < jsonarry.size(); i++) {
					sb.append(jsonarry.get(i));
					if (i < jsonarry.size() - 1) {
						sb.append(",");
					}
				}
			}
		}else{
			log.error("no user found for sp["+sp.getId()+"]");
		}
		/**modify by liming 重复调用了接口，修改代码为一次调用**/
		if(CollectionUtils.isEmpty(userArray)){
			return;
		}
		JSONArray groupArray = userArray.getJSONObject(0).getJSONArray("groupList");
		if(CollectionUtils.isNotEmpty(groupArray)){
			List<String> groupList = new ArrayList<String>();
			int size = groupArray.size();
			for (int i = 0; i < size; i++){
				groupList.add(groupArray.getJSONObject(i).getString("id"));
			}
			ssoUser.setGroupList(groupList);
		}
		/**end**/
		ssoUser.setUserInfo(userInfo);
		SpRole sr = new SpRole();
		sr.setRoleId(sb.toString());
		ssoUser.setCurRole(sr);
	}


	public JSONArray getUserInfoByPhone(String phone,String spId, String contactsId) {
		String apiVersion = SystemConfig.getString(MutiSiteConstants.API_USER_VERSION);
		JSONObject param = new JSONObject();
		param.put("spId", spId);
		param.put("contactsId", contactsId);
		param.put("mdnList", JSONArray.parseArray("[\""+phone+"\"]"));
		//根据用户手机号获取用户信息、
		log.info("-------开始调用contacts_member_get_by_mdn接口----------");
		JSONObject resultCon = ApiClientUtil.post("contacts_member_get_by_mdn", param, apiVersion);
		log.info("-------结束调用contacts_member_get_by_mdn接口----------");
		
		if(200 != resultCon.getIntValue("returnCode")){
			return null;
		}
		JSONArray jsonArray = resultCon.getJSONArray("list");
		return jsonArray;
	}

	private void getGroupList(String userPhone,String spId,String contactsId,SsoUser ssoUser){
		String apiVersion = SystemConfig.getString(MutiSiteConstants.API_USER_VERSION);
		JSONObject param = new JSONObject();
		param.put("spId", spId);
		param.put("contactsId", contactsId);
		param.put("mdnList", JSONArray.parseArray("[\""+userPhone+"\"]"));
		//根据用户手机号获取用户信息contacts_member_get_by_mdn
		JSONObject resultCon = ApiClientUtil.post("contacts_member_get_by_mdn", param, apiVersion);
		if(!MutiSiteConstants.SUCCESS_STR.equals(resultCon.getString("returnCode"))){
			return;
		}
		JSONArray jsonArray = resultCon.getJSONArray("list");
		if(CollectionUtils.isEmpty(jsonArray) || jsonArray.size() != 1){
			return;
		}
		jsonArray = jsonArray.getJSONObject(0).getJSONArray("groupList");
		if(CollectionUtils.isNotEmpty(jsonArray)){
			List<String> groupList = new ArrayList<String>();
			int size = jsonArray.size();
			for (int i = 0; i < size; i++){
				groupList.add(jsonArray.getJSONObject(i).getString("id"));
			}
			ssoUser.setGroupList(groupList);
		}
	}


	private SsoUser getSsoUser(String userJson,HttpServletRequest request){
		SsoUser ssoUser = new SsoUser();
		Map<String,Object> userMap  =JSON.parseObject(userJson,  HashMap.class);
		//设置loginSpInfo
		SpInfo sp = new SpInfo();
		String userPhone = (String)userMap.get("mobile");
		JSONObject corp = (JSONObject) JSONObject.toJSON(userMap.get("corp"));
		sp.setId(corp.getString("spId"));
		sp.setSpName(corp.getString("name"));
		String contactsId = getContactsIdBySpId(sp.getId());
		ssoUser.setLoginSpInfo(sp);
		getUserInfo(userPhone, sp, contactsId,ssoUser);
		HttpSession session = request.getSession(true);
		//getGroupList(userPhone,sp.getId(),contactsId,ssoUser);
		/*HttpSession session = request.getSession(true);
		String mapStr = "{\"loginSpInfo\":{\"id\":\"16090611415110000319\",\"spName\":\"党建业务测试账号1\",\"platform\":null,\"point\":6,\"amount\":null},\"userInfo\":{\"id\":\"16061813190710025758\",\"name\":\"陈志攀\",\"personalPhoneNo\":\"15982066774\",\"memberId\":\"57d1b4b972f620334d6615e6\"},\"curRole\":{\"roleType\":null,\"roleName\":null,\"roleId\":\"16090902585010018976,16091408042410027087,16092102194610093420\",\"groupId\":null},\"roles\":[{\"id\":\"16090902585010018976\",\"status\":\"1\",\"name\":\"热电厂管理员\",\"type\":\"1\"},{\"id\":\"16091408042410027087\",\"status\":\"1\",\"name\":\"热电厂成员\",\"type\":\"2\"},{\"id\":\"16092102194610093420\",\"status\":\"1\",\"name\":\"热点燃气集团党委管理员\",\"type\":\"1\"}]}";
		ssoUser = com.alibaba.fastjson.JSONObject.parseObject(mapStr,SsoUser.class);*/
		ssoUser.setContactsId(contactsId);
		ssoUser.setSpId(corp.getString("spId"));
		session.setAttribute("_sso_user", ssoUser);
		session.setAttribute("_user_map", userMap);
		session.setAttribute(USER_LOGIN_KEY, getUserLoginKey(request));
		return ssoUser;
	}



	public String getContactsIdBySpId(String spId) {
		JSONObject param = new JSONObject();
		param.put("spId", spId);
		param.put("type", "3");
		String apiVersion = SystemConfig.getString(MutiSiteConstants.API_USER_VERSION);
		JSONObject result  =ApiClientUtil.post("contacts_conf_list", param, apiVersion);
		JSONArray jsonArray = result.getJSONArray("list");
		JSONObject jo = jsonArray.getJSONObject(0);
		return jo.getString("contactsId");
	}

}
