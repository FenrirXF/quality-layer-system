package com.quality.service;

import com.quality.common.vo.ReqRegisterVO;
import com.quality.entity.SysUser;
import com.github.pagehelper.PageInfo;

import java.util.List;

public interface SysUserService {
    /** 新增用户 */
    int add(SysUser sysUser);

    /** 根据id查询 */
    SysUser getById(Long id);

    /** 分页查询用户列表 */
    PageInfo<SysUser> pageList(Integer pageNum, Integer pageSize, String username, String role, String region);

    /** 修改用户 */
    int update(SysUser sysUser);

    /** 删除用户（校验关联需求） */
    int deleteById(Long id);

    /** 根据用户名查询（登录用） */
    SysUser getByUsername(String username);

    /** 根据角色查询（登录用） */
    List<SysUser> listByRoleCode(String role);

    /** 注册 */
    void register(ReqRegisterVO vo);

    void updateStatus(Long id, Integer status);
}