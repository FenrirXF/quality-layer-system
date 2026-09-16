package com.quality.service.impl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.quality.entity.ReqRequirement;
import com.quality.entity.SysRegion;
import com.quality.entity.SysUser;
import com.quality.mapper.ReqRequirementMapper;
import com.quality.mapper.SysRegionMapper;
import com.quality.mapper.SysUserMapper;
import com.quality.service.SysRegionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SysRegionServiceImpl implements SysRegionService {

    @Autowired
    private SysRegionMapper sysRegionMapper;
    @Autowired
    private SysUserMapper userMapper;
    @Autowired
    private ReqRequirementMapper reqMapper;

    @Override
    public int add(SysRegion sysRegion) {
        sysRegion.setCreateTime(LocalDateTime.now());
        return sysRegionMapper.insert(sysRegion);
    }

    @Override
    public SysRegion getById(Long id) {
        return sysRegionMapper.selectById(id);
    }

    @Override
    public PageInfo<SysRegion> pageList(Integer pageNum, Integer pageSize, String name, Integer status) {
        PageHelper.startPage(pageNum, pageSize);
        List<SysRegion> list = sysRegionMapper.selectList(name, status);
        return new PageInfo<>(list);
    }

    @Override
    public int update(SysRegion sysRegion) {
        return sysRegionMapper.update(sysRegion);
    }

    @Override
    public int deleteById(Long id) {
        SysRegion region = sysRegionMapper.selectById(id);
        List<SysUser> userList = userMapper.selectList(null, null, region.getName());
        List<ReqRequirement> reqList = reqMapper.selectList(region.getName(), null, null);
        if (!userList.isEmpty() || !reqList.isEmpty()) {
            throw new RuntimeException("该区域存在关联用户或需求，无法删除");
        }
        return sysRegionMapper.deleteById(id);
    }

    @Override
    public SysRegion getByName(String name) {
        return sysRegionMapper.selectByName(name);
    }

    @Override
    public List<SysRegion> list() {
        return sysRegionMapper.selectAll();
    }

    @Override
    public List<SysRegion> listEnable() {
        return sysRegionMapper.selectEnableRegion();
    }
}