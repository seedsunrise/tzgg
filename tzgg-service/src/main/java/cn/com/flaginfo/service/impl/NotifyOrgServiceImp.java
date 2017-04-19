package cn.com.flaginfo.service.impl;

import java.util.List;

import org.aspectj.weaver.ast.Not;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.com.flaginfo.dao.NotifyOrgMapper;
import cn.com.flaginfo.service.NotifyOrgService;

/**
 * 
 *<p>Title:获取消息发送目的的组织 </p>* 
 *<p>Description: </p>*
 *<p>Company:flaginfo </p> 
 * @author liming
 * @date 2016年12月8日
 */
@Service
public class NotifyOrgServiceImp implements NotifyOrgService{
	
	@Autowired
	private NotifyOrgMapper notifyOrgMapper;
	
	/**
	 * 通过消息ID反查发送给管理员的组织反查
	 */
	@Override
	public List<String> getOrgManager(String notifyId) {
		return notifyOrgMapper.getOrgForPublishManager(notifyId);
	}
	
	/**
	 * 通过消息id反查发送给组织所有人
	 */
	@Override
	public List<String> getOrgAll(String notifyId) {
		return notifyOrgMapper.getOrgForPublishAll(notifyId);
	}

}
