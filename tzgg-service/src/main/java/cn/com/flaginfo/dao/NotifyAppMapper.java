package cn.com.flaginfo.dao;

import java.util.List;
import java.util.Set;

import org.apache.ibatis.annotations.Param;

import cn.com.flaginfo.commons.mybatis.configs.Mapper;
import cn.com.flaginfo.pojo.NotifyAppPO;
import cn.com.flaginfo.pojo.NotifyRelationPerson;

@Mapper
public interface NotifyAppMapper {

	List<NotifyAppPO> selectAllNotify(@Param("notify")NotifyAppPO notify);
	
	NotifyAppPO selectById(@Param("notifyId")String notifyId,@Param("type")Integer type,@Param("userId")String userId);
	
	void updateToRead(@Param("notifyId")String notifyId,@Param("userId")String userId);
	
	void insertToRead(NotifyRelationPerson nrp);
	
}
