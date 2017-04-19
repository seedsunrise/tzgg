package cn.com.flaginfo.service;

import java.util.List;

import cn.com.flaginfo.pojo.OrgTreePO;

public interface OrgTreeService {
	
	List<OrgTreePO> orgTree(String groupId,String spId,String contactsId);
	
	List<String> parentOrg(String groupId,String spId,String contactsId);

}
