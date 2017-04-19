package cn.com.flaginfo.Thread;

import com.alibaba.fastjson.JSONObject;

import cn.com.flaginfo.logger.Logger;
import cn.com.flaginfo.utils.ApiClientUtil;
import cn.com.flaginfo.utils.MutiSiteConstants;

public class NotifyCommand implements Runnable{
	
	private Logger logger=new Logger(NotifyCommand.class);
	
	private JSONObject apiBody;
	
	
	public NotifyCommand(JSONObject apiBody) {
		super();
		this.apiBody = apiBody;
	}

	@Override
	public void run() {
		logger.info("-----------------[{}]开始发送消息-----------------",Thread.currentThread().getName());
		ApiClientUtil.post(MutiSiteConstants.CMC_SEND, apiBody);
	}
	
}
