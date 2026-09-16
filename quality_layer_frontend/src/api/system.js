import request from '@/utils/request'

// 分页查询区域
export const getRegionPageApi = (params) => request.get('/system/region/page', { params })
// 新增区域
export const addRegionApi = (data) => request.post('/system/region/add', data)
// 编辑区域
export const updateRegionApi = (data) => request.put('/system/region/update', data)
// 删除区域
export const delRegionApi = (id) => request.delete(`/system/region/${id}`)

// 获取菜单权限树形列表（角色权限配置使用）
export const getMenuTreeApi = () => request.get('/menu/tree')