package cn.com.flaginfo.service;

import cn.com.flaginfo.pojo.NotifyAppPO;
import cn.com.flaginfo.pojo.NotifyRelationPerson;
import cn.com.flaginfo.utils.Page;

/**
 * 
 *<p>Title:app端通知公告service </p>* 
 *<p>Description: </p>*
 *<p>Company:flaginfo </p> 
 * @author liming
 * @date 2016年12月12日
 */
public interface NotifyAppService {
	
	Page<NotifyAppPO> listALlNotifySelf(Page<NotifyAppPO> page,NotifyAppPO notify);
	
	NotifyAppPO selectByNotifyId(String notifyId,Integer type,String userId);
	
	void personNotifyRead(String notifyId,String userId);
	
	void groupNotifyRead(NotifyRelationPerson nrp);

}
