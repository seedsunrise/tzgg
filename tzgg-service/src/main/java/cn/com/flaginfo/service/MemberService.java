package cn.com.flaginfo.service;

import java.util.List;

import cn.com.flaginfo.pojo.MemberPO;

/**
 * 
 *<p>Title:人员相关service </p>* 
 *<p>Description: </p>*
 *<p>Company:flaginfo </p> 
 * @author liming
 * @date 2016年12月8日
 */
public interface MemberService {
	
	List<MemberPO> getMemberList(String groupId,String spId,String contactsId);
	
	List<String> getMemberByNotifyId(String notifyId);

}
