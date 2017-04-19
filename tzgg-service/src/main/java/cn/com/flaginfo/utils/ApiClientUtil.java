package cn.com.flaginfo.utils;


import cn.com.flaginfo.ApiClient;
import cn.com.flaginfo.logger.Logger;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.springframework.util.Assert;

/**
 * *<p>Title:api 公共类 </p>* 
	<p>Description: </p>* 
     @author liming
     @date 2016年8月11日
 */
public class ApiClientUtil {
	private static final Logger log =
			new Logger(ApiClientUtil.class);
	
	private static final String CODE_SUCCESS="200";
	
	private static final String KEY_RETURN_CODE="returnCode";
	/**
	 * 
	 * desc:传入api名称，参数直接返回json
	 * @param api
	 * @param body
	 * @return
	 * @author:liming
	 * @date:2016年8月11日
	 */
	public static  JSONObject post(String api, JSON body) {
		log.info("Post API [{}] With Body [{}].", api, body);
		long before=System.currentTimeMillis();
		JSONObject joResult = JSON.parseObject(
			request(api,"").setJsonBody(body.toString()).postAsJson());
		log.info("Post API [{}] Result [{}].", api, joResult);
		log.info("--------api[{}]耗时[{}]秒----------", api,(System.currentTimeMillis()-before)/1000);
		return joResult;
	}
	
	public static  JSONObject post(String api, JSON body,String version) {
		log.info("Post API [{}] With Body [{}].", api, body);
		long before=System.currentTimeMillis();
		JSONObject joResult = JSON.parseObject(
			request(api,version).setJsonBody(body.toString()).postAsJson());
		log.info("Post API [{}] Result [{}].", api, joResult);
		log.info("--------api[{}]耗时[{}]秒----------", api,(System.currentTimeMillis()-before)/1000);
		return joResult;
	}
	
	
	
	private static ApiClient request(String api,String version) {
		String apiVersion=version;
		if(StringUtils.isEmpty(apiVersion)){
			apiVersion = SystemConfig.getString(MutiSiteConstants.API_USER_VERSION);
		}
		String apiKey=SystemConfig.getString(MutiSiteConstants.API_USER_KEY);
		log.info("Request API [{}] With Version [{}].", api, apiVersion);
		return new ApiClient(api, apiKey, apiVersion);
	}
	
	
	public static void assertSuccess(JSONObject joResp) {
		Assert.state(CODE_SUCCESS.equals(
			joResp.get(KEY_RETURN_CODE)), joResp.toString());
	}
	

}
