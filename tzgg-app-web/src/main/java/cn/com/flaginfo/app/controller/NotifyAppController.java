package cn.com.flaginfo.app.controller;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.com.flaginfo.logger.Logger;
import cn.com.flaginfo.pojo.NotifyAppPO;
import cn.com.flaginfo.pojo.SsoUser;
import cn.com.flaginfo.utils.HeadHelper;
import cn.com.flaginfo.utils.Page;

import com.alibaba.fastjson.JSONObject;

/**
 * 
 * <p>
 * Title:获取所用通知公告app端controller
 * </p>
 * *
 * <p>
 * Description:
 * </p>
 * *
 * <p>
 * Company:flaginfo
 * </p>
 * 
 * @author liming
 * @date 2016年12月12日
 */
@Controller
@RequestMapping("notify")
public class NotifyAppController extends BaseController {

	private static Logger logger = new Logger(NotifyAppController.class);

	/**
	 * 
	 * desc:获取个人所有通知公告
	 * 
	 * @param request
	 * @param searchString
	 * @param page
	 * @return
	 * @author:liming
	 * @date:2016年12月13日
	 */
	@RequestMapping("list")
	@ResponseBody
	public Object listAllNotify(HttpServletRequest request,
			String searchString, Page<NotifyAppPO> page) {

		SsoUser ssoUser = getSsoUser(request.getSession());

		JSONObject jsonObject = new JSONObject();

		jsonObject.put("ssoUser", ssoUser);

		jsonObject.put("searchString", searchString);

		jsonObject.put("page", page);

		logger.info("---------参数为[{}]-----------", jsonObject.toJSONString());

		// post到json端请求数据
		JSONObject result = this.post(
				this.getJsonAction(request.getServletPath()),
				jsonObject.toJSONString());

		return result;

	}

	/**
	 * 
	 * desc:查询单个notify的详情
	 * 
	 * @return
	 * @author:liming
	 * @date:2016年12月13日
	 */
	@RequestMapping("get")
	@ResponseBody
	public Object getNotifyDetail(HttpServletRequest request, String notifyId,
			Integer type) {

		if (StringUtils.isEmpty(notifyId) || type == null) {

			return HeadHelper.errResponse("参数为空");
		}
		SsoUser ssoUser = getSsoUser(request.getSession());

		JSONObject jsonObject = new JSONObject();

		jsonObject.put("notifyId", notifyId);

		jsonObject.put("type", type);
		
		jsonObject.put("userId", ssoUser.getUserInfo().getId());

		jsonObject.put("spId", ssoUser.getSpId());

		logger.info("---------参数为[{}]-----------", jsonObject.toJSONString());

		JSONObject result = this.post(
				this.getJsonAction(request.getServletPath()),
				jsonObject.toJSONString());

		return result;
	}

	/**
	 * 
	 * desc:改变通知状态为已读
	 * 
	 * @return
	 * @author:liming
	 * @date:2016年12月13日
	 */
	@RequestMapping("read")
	@ResponseBody
	public Object changeToRead(HttpServletRequest request, String notifyId,
			Integer type) {

		if (StringUtils.isEmpty(notifyId) || type == null) {

			return HeadHelper.errResponse("参数为空");
		}
		SsoUser ssoUser = getSsoUser(request.getSession());

		JSONObject jsonObject = new JSONObject();

		jsonObject.put("ssoUser", ssoUser);

		jsonObject.put("notifyId", notifyId);

		jsonObject.put("type", type);

		logger.info("---------参数为[{}]-----------", jsonObject.toJSONString());

		JSONObject result = this.post(
				this.getJsonAction(request.getServletPath()),
				jsonObject.toJSONString());

		return result;

	}

	
	/**
	 * 
	 * desc:获取file详情
	 * @param request
	 * @param fileId
	 * @return
	 * @author:liming
	 * @date:2016年12月14日
	 */
	@RequestMapping("file")
	@ResponseBody
	public Object getNotifyFile(HttpServletRequest request, String fileId) {

		if (StringUtils.isEmpty(fileId)) {

			return HeadHelper.errResponse("参数为空");
		}

		JSONObject jsonObject = new JSONObject();

		jsonObject.put("fileId", fileId);

		logger.info("---------参数为[{}]-----------", jsonObject.toJSONString());

		JSONObject result = this.post(
				this.getJsonAction(request.getServletPath()),
				jsonObject.toJSONString());

		return result;

	}

}
