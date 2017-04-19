package cn.com.flaginfo.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import cn.com.flaginfo.commons.mybatis.configs.Mapper;

@Mapper
public interface NotifyOrgMapper {
	
	List<String> getOrgForPublishManager(@Param("id")String notifyId);
	
	List<String> getOrgForPublishAll(@Param("id")String notifyId);

}
