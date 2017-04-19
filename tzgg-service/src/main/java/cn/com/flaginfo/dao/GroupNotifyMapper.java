package cn.com.flaginfo.dao;

import cn.com.flaginfo.commons.mybatis.configs.Mapper;
import cn.com.flaginfo.example.GroupNotifyExample;
import cn.com.flaginfo.pojo.GroupNotify;
import java.util.List;

import cn.com.flaginfo.pojo.ex.GroupNotifyEx;

@Mapper
public interface GroupNotifyMapper extends BaseMapper<GroupNotify, GroupNotifyExample>{

    /**
     * 查询通知公告
     * @param groupNotifyEx
     * @return
     */
    List<GroupNotifyEx> queryNotify(GroupNotifyEx groupNotifyEx);
}
