package com.quality.mapper;
import com.quality.entity.SysOperLog;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface SysOperLogMapper {
    int insert(SysOperLog log);
    List<SysOperLog> selectList(@Param("operator") String operator,
                                @Param("module") String module,
                                @Param("startTime") String startTime,
                                @Param("endTime") String endTime);

    SysOperLog selectById(Long id);
}