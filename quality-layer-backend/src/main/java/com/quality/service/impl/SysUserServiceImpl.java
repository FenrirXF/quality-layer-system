package com.quality.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.quality.common.vo.ReqRegisterVO;
import com.quality.entity.ReqRequirement;
import com.quality.entity.SysUser;
import com.quality.mapper.ReqRequirementMapper;
import com.quality.mapper.SysUserMapper;
import com.quality.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SysUserServiceImpl implements SysUserService {

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private ReqRequirementMapper reqRequirementMapper;

    @Autowired
    private org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder passwordEncoder;

    @Override
    public int add(SysUser sysUser) {
        // 新增：自动赋值创建时间
        sysUser.setCreateTime(LocalDateTime.now());
        return sysUserMapper.insert(sysUser);
    }

    @Override
    public SysUser getById(Long id) {
        return sysUserMapper.selectById(id);
    }

    @Override
    public PageInfo<SysUser> pageList(Integer pageNum, Integer pageSize, String username, String role, String region) {
        PageHelper.startPage(pageNum, pageSize);
        List<SysUser> list = sysUserMapper.selectList(username, role, region);
        return new PageInfo<>(list);
    }

    @Override
    public int update(SysUser sysUser) {
        return sysUserMapper.update(sysUser);
    }

    @Override
    public int deleteById(Long id) {
        SysUser user = sysUserMapper.selectById(id);
        // 校验该用户是否创建过需求，存在则禁止删除
        List<ReqRequirement> reqList = reqRequirementMapper.selectList(null, null, user.getName());
        if (reqList != null && !reqList.isEmpty()) {
            throw new RuntimeException("该用户存在关联需求，无法删除");
        }
        return sysUserMapper.deleteById(id);
    }

    @Override
    public SysUser getByUsername(String username) {
        return sysUserMapper.selectByUsername(username);
    }

    @Override
    public List<SysUser> listByRoleCode(String role) {
        return sysUserMapper.selectByRoleCode(role);
    }

    @Override
    public void register(ReqRegisterVO vo) {
        // 校验用户名唯一
        SysUser exist = sysUserMapper.selectByUsername(vo.getUsername());
        if(exist != null){
            throw new RuntimeException("用户名已经存在");
        }
        SysUser user = new SysUser();
        user.setUsername(vo.getUsername());
        user.setName(vo.getName());
        user.setPassword(passwordEncoder.encode(vo.getPassword()));
        user.setRole("regional");
        user.setRegion(vo.getRegion());
        user.setStatus(1);
        user.setCreateTime(LocalDateTime.now());
        sysUserMapper.insert(user);
    }

    @Override
    public void updateStatus(Long id, Integer status) {
        sysUserMapper.updateStatus(id, status);
    }
}