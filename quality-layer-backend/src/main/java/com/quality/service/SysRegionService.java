package com.quality.service;
import com.github.pagehelper.PageInfo;
import com.quality.entity.SysRegion;

import java.util.List;

public interface SysRegionService {
    int add(SysRegion sysRegion);
    SysRegion getById(Long id);
    PageInfo<SysRegion> pageList(Integer pageNum, Integer pageSize, String name, Integer status);
    int update(SysRegion sysRegion);
    int deleteById(Long id);
    SysRegion getByName(String name);
    List<SysRegion> list();

    List<SysRegion> listEnable();
}