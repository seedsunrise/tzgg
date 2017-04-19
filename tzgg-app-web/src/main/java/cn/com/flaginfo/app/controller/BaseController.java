package cn.com.flaginfo.app.controller;


import cn.com.flaginfo.app.utils.HeadHelper;
import cn.com.flaginfo.commons.utils.config.SystemConfig;
import cn.com.flaginfo.pojo.SsoUser;
import cn.com.flaginfo.utils.MutiSiteConstants;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.net.SocketException;


/**
 * @author liusm
 * 
 * @2015-12-15
 * 
 */

public abstract class BaseController {
	private static Logger logger=LoggerFactory.getLogger(BaseController.class);
	@Resource
	RestTemplate restTemplate;


	public JSONObject post(String uri, String param, Object... pathParam) {
		HttpHeaders headers = new HttpHeaders();
		MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
		headers.setContentType(type);
		headers.add("Accept", MediaType.APPLICATION_JSON.toString());
		HttpEntity<String> formEntity = new HttpEntity<String>(param, headers);
		String res="";
		try {
			res= doTryThreeTimes(uri, formEntity, String.class,1,pathParam);
		} catch (SocketException e) {
			
			logger.debug("----------json server报错，错误信息为--------------------");
			logger.debug("错误信息为：",e);
			logger.debug("----------end 错误信息-----------------------------");
			return (JSONObject)JSONObject.toJSON(HeadHelper.errResponse("连接异常"));
			
		} catch (Exception e) {
			
			logger.debug("----------json server报错，错误信息为--------------------");
			logger.debug("错误信息为：",e);
			logger.debug("----------end 错误信息-----------------------------");
			return (JSONObject)JSONObject.toJSON(HeadHelper.errResponse("网络异常,请稍后重试."));
		}
	
		return JSONObject.parseObject(res);
	}

	
	/**
	 * 
	 * desc:连接异常重试机制
	 * @param uri
	 * @param request
	 * @param responseType
	 * @param times
	 * @param pathParam
	 * @return
	 * @throws java.net.SocketException
	 * @author:liming
	 * @date:2016年11月7日
	 */
	private String doTryThreeTimes(String uri, Object request, Class<String> responseType,Integer times,Object... pathParam) throws SocketException{
		String res="";
		try {
			logger.debug("------------调用[{}],第[{}]次进入--------------",uri,times);

			res=restTemplate.postForObject(uri, request, String.class, pathParam);


			logger.debug("------------调用[{}],第[{}]次进入后执行成功--------------",uri,times);

			return res;
		} catch (Exception e) {

			if(times>3){
				logger.debug("------------调用[{}],重试[{}]次进入抛出异常--------------",uri,times);
				throw new SocketException("重试三次连接reset");
			}

			if(e instanceof org.springframework.web.client.ResourceAccessException || e instanceof SocketException){
				logger.debug("------------调用[{}],第[{}]次调用json server失败，开始重试--------------",uri,times);
				res=doTryThreeTimes(uri, request, String.class,++times,pathParam);
				
				return res;
			}else{
				
				throw e;
				
			}
			
		}
		
		
		
	}
	 
	public String getJsonAction(String jsonActionUrl){
		return SystemConfig.getString(MutiSiteConstants.JSONSERVERIP)+jsonActionUrl;
	}
	
	public SsoUser getSsoUser(HttpSession session) {
		@SuppressWarnings("unchecked")
		SsoUser ssoUser = (SsoUser) session.getAttribute("_sso_user");
		return ssoUser;
	}
}
