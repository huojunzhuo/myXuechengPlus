package com.xuecheng.media.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xuecheng.media.model.po.MediaProcess;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author itcast
 */
@Mapper
public interface MediaProcessMapper extends BaseMapper<MediaProcess> {

    /**
     * 查询符合分片规则的任务列表
     * @param shardTotal 分片数量
     * @param shardIndex 分片序号
     * @param count 一次获取任务数量
     * @return
     */
    @Select("select * from media_process  t where t.id%#{shardTotal} = #{shardIndex} and (status = '1' or status = '3') and t.fail_count<3 limit #{count} ")
    public List<MediaProcess> selectListByShardIndex(@Param("shardTotal") int shardTotal, @Param("shardIndex") int shardIndex, @Param("count") int count);

    /**
     * 更新status字段获取分布式锁
     * @param id 任务id
     * @return 更新结果
     */
    @Update("update media_process m set m.status = '4' where m.id = #{id} and m.fail_count<3 and (m.status = '1' or m.status = '3')")
    int startTask(@Param("id") long id);


}
