package cn.com.flaginfo.service.impl;

import cn.com.flaginfo.dao.AttachmentsMapper;
import cn.com.flaginfo.pojo.Attachments;
import cn.com.flaginfo.service.AttachmentsService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by lenovo on 2016/11/29.
 */
@Service("attachmentsService")
@Transactional
public class AttachmentServiceImpl implements AttachmentsService{
	
	 @Autowired
     private AttachmentsMapper attachmentsMapper;

	@Override
	public Attachments getAttaDetail(String id) {
		
		return attachmentsMapper.selectByPrimaryKey(id);
	}

}
