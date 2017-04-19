package cn.com.flaginfo.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.sf.json.util.NewBeanInstanceStrategy;

import com.alibaba.druid.support.json.JSONUtils;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cn.com.flaginfo.dao.MemberMapper;
import cn.com.flaginfo.pojo.MemberPO;
import cn.com.flaginfo.service.MemberService;
import cn.com.flaginfo.utils.ApiClientUtil;
import cn.com.flaginfo.utils.MutiSiteConstants;
/**
 * 
 *<p>Title:人员相关service </p>* 
 *<p>Description: </p>*
 *<p>Company:flaginfo </p> 
 * @author liming
 * @date 2016年12月8日
 */
@Service
public class MemberServiceImp implements MemberService {
	
	@Autowired
	private MemberMapper memberMapper;

	/**
	 * 根据groupId，spId，contactsId获取人员列表
	 */
	@Override
	public List<MemberPO> getMemberList(String groupId, String spId,
			String contactsId) {
		   //添加数据到params
		    JSONArray groupJarry=new JSONArray();
		    groupJarry.add(groupId);
		    JSONArray attrJarry=new JSONArray();
		    //只获取相关属性，避免数据量过大
		    attrJarry.add("name");
		    attrJarry.add("yxtMap");
		    attrJarry.add("userMap");
		    attrJarry.add("mdn");
		    JSONObject params = new JSONObject();
	        params.put("spId",spId);
	        params.put("contactsId", contactsId);
	        params.put("groups",groupJarry);
	        params.put("attrList",attrJarry);
	        
	        JSONObject result = ApiClientUtil.post(MutiSiteConstants.CONTACTS_MEMBER_GET_BY_GROUP,params);
		     
		
		return convertJsonData(result);
	}
	
	/**
	 * 
	 * desc:格式化json数据
	 * @param obj
	 * @return
	 * @author:liming
	 * @date:2016年12月8日
	 */
	private List<MemberPO> convertJsonData(JSONObject obj){
		   
		List<MemberPO> list=new ArrayList<MemberPO>();
		
	     if(MutiSiteConstants.SUCCESS == obj.getInteger("returnCode")){
             JSONArray jsonArray = obj.getJSONArray("list");
             if(null != jsonArray && jsonArray.size() > 0){
                 int size = jsonArray.size();
                 for (int i = 0; i < size; i++) {
                	 MemberPO member=new MemberPO();
                     JSONObject jsonObject = jsonArray.getJSONObject(i);
                     String name=jsonObject.getString("name");
                     String phone=jsonObject.getString("mdn");
                     JSONObject userMap = jsonObject.getJSONObject("userMap");
                     JSONObject yxtMap = jsonObject.getJSONObject("yxtMap");
                     if(null != userMap && StringUtils.isNotEmpty(userMap.getString("userId"))){
                    	 member.setId(userMap.getString("userId"));
                     }else if(null != yxtMap && StringUtils.isNotEmpty(yxtMap.getString("userId"))){
                         member.setId(yxtMap.getString("userId"));
                     }else{
                    	 continue;
                     }
                     member.setName(name);
                     member.setPhone(phone);
                     list.add(member);
                 }
             }
         }
		
		return list;
	}

	/**
	 * 根据notifyId反查推送的人
	 */
	@Override
	public List<String> getMemberByNotifyId(String notifyId) {
		return memberMapper.ListByNotifyId(notifyId);
	}

}
