package cn.com.flaginfo.json.controller;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;

import javax.servlet.http.HttpServletRequest;












import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.com.flaginfo.logger.Logger;
import cn.com.flaginfo.pojo.Attachments;
import cn.com.flaginfo.pojo.NotifyAppPO;
import cn.com.flaginfo.pojo.NotifyRelationPerson;
import cn.com.flaginfo.pojo.SsoUser;
import cn.com.flaginfo.pojo.ex.GroupNotifyEx;
import cn.com.flaginfo.service.AttachmentsService;
import cn.com.flaginfo.service.GroupNotifyService;
import cn.com.flaginfo.service.NotifyAppService;
import cn.com.flaginfo.service.OrgTreeService;
import cn.com.flaginfo.utils.HeadHelper;
import cn.com.flaginfo.utils.Page;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

/**
 * 
 *<p>Title:通知公告json端controller </p>* 
 *<p>Description: </p>*
 *<p>Company:flaginfo </p> 
 * @author liming
 * @date 2016年12月12日
 */
@Controller
@RequestMapping("notify")
public class NotifyJsonController {
	
	private static Logger logger=new Logger(NotifyJsonController.class);
	
	@Autowired
	private OrgTreeService orgTreeService;
	
	@Autowired
	private NotifyAppService notifyAppService;
	
	@Autowired
	private GroupNotifyService groupNotifyService;
	
	@Autowired
	private AttachmentsService attachmentService;
	
	/**
	 * desc:获取所有通知列表
	 * @param request
	 * @param jsonObject
	 * @return
	 * @author:liming
	 * @date:2016年12月12日
	 */
	@RequestMapping("list")
	@ResponseBody
	public Object listAllNotify(HttpServletRequest request,@RequestBody JSONObject jsonObject){
		
		Page<NotifyAppPO> newpage=null;
		try {
			
			SsoUser ssoUser =JSON.parseObject(JSON.toJSONString(jsonObject.get("ssoUser")), SsoUser.class);
			
			Page<NotifyAppPO> page=JSON.parseObject(JSON.toJSONString(jsonObject.get("page")), Page.class);

			String searchString=jsonObject.getString("searchString");
			
			if(ssoUser==null){
				return HeadHelper.errResponse("参数为空");
			}
			//获取基础数据
			String spId=ssoUser.getSpId();
			
			String contactsId=ssoUser.getContactsId();
			
			String userId=ssoUser.getUserInfo().getId();
			
			List<String> groupList=ssoUser.getGroupList();
			
	        HashSet<String> allParentGroup=new HashSet<String>();
	        //通过登录人所有role去获取父级节点并排重
			for(String group:groupList){
				List<String> list=orgTreeService.parentOrg(group, spId, contactsId);
				allParentGroup.addAll(list);
			}
			
			//组装搜索数据
			NotifyAppPO notify=new NotifyAppPO();
			notify.setUserId(userId);
			notify.setAllParentGroup(allParentGroup);
			notify.setGroupList(groupList);
			notify.setSearchString(searchString);
			notify.setSpId(ssoUser.getSpId());
			if(StringUtils.isEmpty(userId)||StringUtils.isEmpty(ssoUser.getSpId())){
				
				HeadHelper.response("");
			}
			newpage=notifyAppService.listALlNotifySelf(page,notify);
			
		} catch (Exception e) {
			
			logger.error("报错信息", e);
			return HeadHelper.errResponse("后台报错。");
		}
		
		
		
		return HeadHelper.response(newpage);
	}
	
	/**
	 * 
	 * desc:获取消息通知详情
	 * @param jsonObject
	 * @return
	 * @author:liming
	 * @date:2016年12月13日
	 */
	@RequestMapping("get")
	@ResponseBody
	public Object getNotifyJson(@RequestBody JSONObject jsonObject){
		
		NotifyAppPO np=null;
		try {
			
			String notifyId=jsonObject.getString("notifyId");
			
			String spId=jsonObject.getString("spId");
			
			Integer type=jsonObject.getInteger("type");
			
			String userId=jsonObject.getString("userId");
			
			np=notifyAppService.selectByNotifyId(notifyId, type,userId);
			
			if(np==null){
				
				HeadHelper.errResponse("该消息已被管理员删除");
			}
			
			//设置文件查询参数
			GroupNotifyEx gx=new GroupNotifyEx();
			gx.setId(np.getId());
			gx.setSpId(spId);
			List<Attachments> list=groupNotifyService.queryAttachments(gx);
			if(!CollectionUtils.isEmpty(list)){
				for(Attachments attr:list){
					changSize(attr);
				}
				np.setAttachmentList(list);
			}
			
		} catch (Exception e) {
			logger.error("报错信息为",e);
			return HeadHelper.errResponse("服务器报错");
			
		}
		
		return HeadHelper.response(np);
	}
	
	
	
	/**
	 * 
	 * desc:改变状态为read
	 * @param jsonObject
	 * @return
	 * @author:liming
	 * @date:2016年12月13日
	 */
	@RequestMapping("read")
	@ResponseBody
	public Object read(@RequestBody JSONObject jsonObject){
		
		SsoUser ssoUser =JSON.parseObject(JSON.toJSONString(jsonObject.get("ssoUser")), SsoUser.class);
		
		String notifyId=jsonObject.getString("notifyId");
		
		Integer type=jsonObject.getInteger("type");
		
		try {
			
			if(type==3){
				
				notifyAppService.personNotifyRead(notifyId, ssoUser.getUserInfo().getId());
				
			}else{
				NotifyRelationPerson nrp=new NotifyRelationPerson();
				nrp.setNotifyId(notifyId);
				nrp.setPhone(ssoUser.getUserInfo().getPersonalPhoneNo());
				nrp.setSpId(ssoUser.getSpId());
				nrp.setStatus("read");
				nrp.setUserId(ssoUser.getUserInfo().getId());
				nrp.setUserName(ssoUser.getUserInfo().getName());
				notifyAppService.groupNotifyRead(nrp);
			}
			
		} catch (Exception e) {
			logger.error("报错信息为",e);
			return HeadHelper.errResponse("服务器报错");
			
		}
		
		return HeadHelper.response("");
	}
	
	@RequestMapping("file")
	@ResponseBody
	public Object getFileDetail(@RequestBody JSONObject jsonObject){
		
		String fileId=jsonObject.getString("fileId");
		Attachments attr=null;
		try {
			attr= attachmentService.getAttaDetail(fileId);
			changSize(attr);
		} catch (Exception e) {
			logger.error("报错信息为",e);
			return HeadHelper.errResponse("服务器报错");
		}
	
		return HeadHelper.response(attr);
				
	}
	
	private void changSize(Attachments attr){
		
		if(attr!=null){
			//尺寸保留两位小数
			String size=attr.getSize();
			if(StringUtils.isNotEmpty(size)){
				Double si=Double.valueOf(size);
				BigDecimal b=new BigDecimal(si);  
				double newSize =b.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue(); 
				attr.setSize(String.valueOf(newSize));
			}
		}
		
	}

}
