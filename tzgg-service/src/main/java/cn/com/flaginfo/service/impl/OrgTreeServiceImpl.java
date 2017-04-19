package cn.com.flaginfo.service.impl;


import java.util.LinkedList;
import java.util.List;

import org.springframework.stereotype.Service;

import cn.com.flaginfo.logger.Logger;
import cn.com.flaginfo.pojo.OrgTreePO;
import cn.com.flaginfo.service.OrgTreeService;
import cn.com.flaginfo.user.auth.common.StringUtil;
import cn.com.flaginfo.utils.ApiClientUtil;
import cn.com.flaginfo.utils.MutiSiteConstants;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * 
 *<p>Title:组织树形Service </p>* 
 *<p>Description: </p>*
 *<p>Company:flaginfo </p> 
 * @author liming
 * @date 2016年11月30日
 */
@Service
public class OrgTreeServiceImpl implements OrgTreeService{
	
	private Logger logger=new Logger(OrgTreeServiceImpl.class);

	@Override
	public List<OrgTreePO> orgTree(String groupId,String spId,String contactsId) {

		logger.info("--获取组织结构：-groupId：[{}]-------spId:[{}]---------contactsId:[{}]--------------",groupId,spId,contactsId);
		JSONObject params = new JSONObject();
		params.put("groupId", groupId);
		params.put("spId", spId);
		params.put("contactsId", contactsId);
		
		if(StringUtil.isEmpty(spId)||StringUtil.isEmpty(contactsId)||StringUtil.isEmpty(groupId)){
			logger.error("必要参数为空");
			return null;
		}
		
		//发送请求
		JSONObject obj=ApiClientUtil.post(MutiSiteConstants.CONTACTS_GROUP_CHILDREN_LIST, params);
		
		//组装数据
		return convertDataToOrgTree(obj);
	}
	
	
	/**
	 * 
	 * desc:组装解析数据
	 * @param obj
	 * @return
	 * @author:liming
	 * @date:2016年12月7日
	 */
	private List<OrgTreePO> convertDataToOrgTree(JSONObject obj){
		
		JSONArray jaDepts = obj.getJSONArray("list");
		List<OrgTreePO> deptsRtn = 
			new LinkedList<OrgTreePO>();

		if (jaDepts != null && !jaDepts.isEmpty()) {
			for (Object objDept : jaDepts) {
				JSONObject joDept = (JSONObject) objDept;
				String deptId = joDept.getString("id");
				String name = joDept.getString("name");
				if (StringUtil.isEmpty(deptId) || StringUtil.isEmpty(name)) {
					logger.error("id为空不合法.", joDept);
					continue;
				}
				OrgTreePO mapDept = new OrgTreePO();
				mapDept.setId(deptId);
				mapDept.setName(name);
				String parentId = joDept.getString("pid");
				mapDept.setpId(parentId);
				Integer sort = joDept.getInteger("seq");
				if (sort == null) {
					sort = 0;
				}
				Integer groupLevel=joDept.getInteger("groupLevel");
				mapDept.setSort(sort);
				mapDept.setGroupLevel(groupLevel);
				deptsRtn.add(mapDept);
			}
		}
		/*Map<String, Object> map=new HashMap<String, Object>();
		map.put("name", "安康市");
		map.put("id", "0");
		map.put("pid", "");
		deptsRtn.add(map);*/
		return deptsRtn;
		
	}


	@Override
	public List<String> parentOrg(String groupId, String spId,
			String contactsId) {
		
		logger.info("--获取组织结构：-groupId：[{}]-------spId:[{}]---------contactsId:[{}]--------------",groupId,spId,contactsId);
		JSONObject params = new JSONObject();
		params.put("groupId", groupId);
		params.put("spId", spId);
		params.put("contactsId", contactsId);
		
		if(StringUtil.isEmpty(spId)||StringUtil.isEmpty(contactsId)||StringUtil.isEmpty(groupId)){
			logger.error("必要参数为空");
			return null;
		}
		
		//发送请求
		JSONObject obj=ApiClientUtil.post(MutiSiteConstants.CONTACTS_GROUP_PARENTS_LIST, params);
		//添加本身
		List<String> list =convertToListString(obj);
		list.add(groupId);
		return list;
	}
	  
	private List<String> convertToListString(JSONObject obj){
		
		JSONArray jaDepts = obj.getJSONArray("list");
		List<String> deptsRtn = 
			new LinkedList<String>();

		if (jaDepts != null && !jaDepts.isEmpty()) {
			for (Object objDept : jaDepts) {
				JSONObject joDept = (JSONObject) objDept;
				String deptId = joDept.getString("id");
				deptsRtn.add(deptId);
			}
		}
		
		return deptsRtn;
		
	}
	

}
