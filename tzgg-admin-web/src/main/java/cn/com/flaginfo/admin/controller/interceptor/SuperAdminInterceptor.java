package cn.com.flaginfo.admin.controller.interceptor;

import cn.com.flaginfo.pojo.SpRole;
import cn.com.flaginfo.pojo.SsoUser;
import cn.com.flaginfo.utils.ApiClientUtil;
import cn.com.flaginfo.utils.HeadHelper;
import cn.com.flaginfo.utils.MutiSiteConstants;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.PrintWriter;
import java.util.Map;

/**
 * 超级管理员权限拦截器，
 * 如果超级管理员没有在通讯录节点里面，则返回提示信息
 * Created by chenzp on 2017/3/13.
 */
@Component
public class SuperAdminInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession();
        Map<String, Object> ssoUesrMap = (Map<String, Object>) session
                .getAttribute("sso_user");
        SsoUser ssoUser = JSONObject.parseObject(new JSONObject(ssoUesrMap).toJSONString(),SsoUser.class);
        SpRole curRole = JSONObject.parseObject( new JSONObject(((Map)((Map)ssoUesrMap.get("loginSpInfo")).get("curRole"))).toJSONString(),SpRole.class);
        ssoUser.setCurRole(curRole);
        if(MutiSiteConstants.USER_ROLE_SUPER_ADMIN.equals(curRole.getRoleType())){//如果是超级管理员
            JSONObject results = getUserByPhone(ssoUser, ssoUser.getUserInfo().getPersonalPhoneNo());
            JSONArray groupArr = null;
            if(null != results){
                groupArr = results.getJSONArray("groupList");
            }
            if(CollectionUtils.isEmpty(groupArr) || "1".equals(groupArr.getJSONObject(0).getString("id"))){
                JSONObject responseJSONObject = new JSONObject(HeadHelper.errResponse("此账号无组织节点"));
                response.setCharacterEncoding("UTF-8");
                response.setContentType("application/json; charset=utf-8");
                try {
                    PrintWriter out = response.getWriter();
                    out.append(responseJSONObject.toString());
                    return false;
                }catch (Exception e){
                    return false;
                }
            }
        }
       return true;
    }

    /**
     * 根据用户手机获取用户信息
     * @param ssoUser
     * @param phone
     * @return
     */
    public static JSONObject getUserByPhone(SsoUser ssoUser, String phone){
        JSONObject params = buildCommonParams(ssoUser);
        params.put("userId", ssoUser.getUserInfo().getId());
        params.put("mdnList",new String[]{phone});
        JSONObject results = ApiClientUtil.post("contacts_member_get_by_mdn", params);
        JSONArray jsonArray = results.getJSONArray("list");
        if(CollectionUtils.isNotEmpty(jsonArray)){
            return jsonArray.getJSONObject(0);
        }
        return null;
    }

    /**
     * 构造公共参数
     *
     * @param ssoUser
     * @return
     */
    private static JSONObject buildCommonParams(SsoUser ssoUser) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("spId", ssoUser.getLoginSpInfo().getId());
        jsonObject.put("contactsId", ssoUser.getCurRole().getContactsId());
        return jsonObject;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}
