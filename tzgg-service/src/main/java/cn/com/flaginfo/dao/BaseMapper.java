package cn.com.flaginfo.dao;

import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface BaseMapper<T,F> {

    int countByExample(F example);

    int deleteByExample(F example);

    int deleteByPrimaryKey(String id);

    int insert(T record);

    int insertSelective(T record);

    List<T> selectByExample(F example);

    T selectByPrimaryKey(String id);

    int updateByExampleSelective(@Param("record") T record, @Param("example") F example);

    int updateByExample(@Param("record") T record, @Param("example") F example);

    int updateByPrimaryKeySelective(T record);

    int updateByPrimaryKey(T record);
}