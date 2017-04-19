package cn.com.flaginfo.utils;

import org.apache.commons.lang.StringUtils;

import cn.com.flaginfo.Thread.NotifyCommand;
import cn.com.flaginfo.logger.Logger;

import com.alibaba.fastjson.JSONObject;

/**
 * 
 *<p>Title:发送消息公共方法 </p>* 
 *<p>Description: </p>*
 *<p>Company:flaginfo </p> 
 * @author liming
 * @date 2016年12月26日
 */
public class NotifySendUtils {
	
	private static  Logger logger=new Logger(NotifySendUtils.class);
	
	private static TaskManager taskManager=TaskManager.getInstance();
	
	/**
	 * 
	 * desc:发送链接消息
	 * @param userId
	 * @param text
	 * @param spId
	 * @param url
	 * @param sendObject
	 * @author:liming
	 * @throws Exception 
	 * @date:2016年12月26日
	 */
	public static void doSend(String userId,String text,String spId,String url,JSONObject sendObject) throws Exception{
		logger.info("-------------userId:[{}],text:[{}],spId:[{}],url:[{}],sendObject:[{}]",userId,text,spId,url,sendObject.toJSONString());
		//构造消息体数据
		if(StringUtils.isEmpty(userId)||StringUtils.isEmpty(text)||StringUtils.isEmpty(spId)||StringUtils.isEmpty(url)||sendObject==null){
		logger.info("-------------必要参数为空----------------");
		throw new NullPointerException("必要参数为空");
		}
		JSONObject param = new JSONObject();
		param.put("app", 1);
		String appId = SystemConfig.getString("api.user.appId");
		param.put("appId", appId);
		param.put("spId", spId);
		param.put("userId", userId);
		param.put("platform","1");
		param.put("mediaType", "html");
		param.put("type", "LINK");
		param.put("text", text);
		param.put("mediaUrl", url);
		param.put("sendObject", sendObject);
		param.put("signPosition", 1);// 企业签名 1:前置,2:后置
		param.put("sendSource", 2);// 发送来源 1:平台,2:接口
		//封装消息为线程
		NotifyCommand notifycommand=new NotifyCommand(param);
		//调用线程池异步执行发送消息
		taskManager.execute(notifycommand);
		
	}
}
