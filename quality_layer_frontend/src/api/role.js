import request from '@/utils/request'

// 角色分页列表
export const getRoleListApi = (params) => request.get('/role/page', { params })

// 角色详情
export const getRoleDetailApi = (id) => request.get(`/role/${id}`)

// 新增角色
export const addRoleApi = (data) => request.post('/role/add', data)

// 编辑角色
export const updateRoleApi = (data) => request.put('/role/update', data)

// 删除角色
export const delRoleApi = (id) => request.delete(`/role/${id}`)

// 获取角色已分配权限ID数组
export const getRolePermIdsApi = (roleId) => request.get(`/role/permIds/${roleId}`)

// 分配权限（保存角色权限）
export const assignPermissionApi = (data) => request.post('/role/savePerm', null, {
  params: { roleId: data.roleId },
  data: data.permIdList
})