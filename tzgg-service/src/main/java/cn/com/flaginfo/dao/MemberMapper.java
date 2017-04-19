package cn.com.flaginfo.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import cn.com.flaginfo.commons.mybatis.configs.Mapper;

@Mapper
public interface MemberMapper {

	List<String> ListByNotifyId(@Param("id")String notifyId);
	
}
