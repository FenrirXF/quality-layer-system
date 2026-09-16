package com.quality.mapper;
import com.quality.entity.SysRegion;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface SysRegionMapper {
    int insert(SysRegion region);
    SysRegion selectById(Long id);
    List<SysRegion> selectList(@Param("name") String name, @Param("status") Integer status);
    int update(SysRegion region);
    int deleteById(Long id);
    SysRegion selectByName(String name);

    List<SysRegion> selectAll();

    List<SysRegion> selectEnableRegion();
}