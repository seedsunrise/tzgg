package cn.com.flaginfo.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import cn.com.flaginfo.dao.NotifyAppMapper;
import cn.com.flaginfo.pojo.NotifyAppPO;
import cn.com.flaginfo.pojo.NotifyRelationPerson;
import cn.com.flaginfo.service.NotifyAppService;
import cn.com.flaginfo.utils.Page;

/**
 * 
 *<p>Title:APP端通知公告service </p>* 
 *<p>Description: </p>*
 *<p>Company:flaginfo </p> 
 * @author liming
 * @date 2016年12月13日
 */
@Service
public class NotifyAppServiceImp implements NotifyAppService {
	@Autowired
	private NotifyAppMapper notifyAppMapper;
	
	/**
	 * 获取个人所有通知公告
	 */
	@Override
	public Page<NotifyAppPO> listALlNotifySelf(Page<NotifyAppPO> page,NotifyAppPO notify) {
		
		PageHelper.startPage(page.getPageNo(), page.getPageSize());
		List<NotifyAppPO> list=notifyAppMapper.selectAllNotify(notify);
		PageInfo<NotifyAppPO> pageInfo = new PageInfo<NotifyAppPO>(list);
		page.setTotalRecord(pageInfo.getTotal());
		page.setResults(list);
		return page;
	}

	/**
	 * 获取详情
	 */
	@Override
	public NotifyAppPO selectByNotifyId(String notifyId, Integer type,String userId) {
		
		return notifyAppMapper.selectById(notifyId, type,userId);
	}
	
	/**
	 * 私人通知改变状态
	 */
	@Override
	public void personNotifyRead(String notifyId, String userId) {
		
		 notifyAppMapper.updateToRead(notifyId, userId);
	}
	
	/**
	 * 组织通知插入read记录
	 */
	@Override
	public void groupNotifyRead(NotifyRelationPerson nrp) {
		
		notifyAppMapper.insertToRead(nrp);
	}
	

}
