package cn.com.flaginfo.service;

import java.util.List;

public interface NotifyOrgService {
	
	List<String> getOrgManager(String notifyId);
	
	List<String> getOrgAll(String notifyId);
}
