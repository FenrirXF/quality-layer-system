import request from '@/utils/request'

// 分页查询
export const getReqPageApi = (params) => request.get('/requirement/page', { params })

// 需求详情
export const getReqDetailApi = (id) => request.get(`/requirement/${id}`)

// 新增需求
export const addReqApi = (data) => request.post('/requirement/add', data)

// 编辑需求
export const updateReqApi = (data) => request.put('/requirement/update', data)

// 删除需求
export const delReqApi = (id) => request.delete(`/requirement/${id}`)

// Excel批量导入【修复】
export const batchAddReqApi = (data) => request.post('/requirement/batchImport', data)

// Excel导出
export const exportReqApi = (params) => request.get('/requirement/export', {
  params,
  responseType: 'blob'
})

// 按区域统计
export const statRegionApi = () => request.get('/requirement/stat/region')

// 按月统计
export const statMonthApi = (params) => request.get('/requirement/stat/month', { params })

// 根据id查询需求详情
export function getRequirementDetailApi(id) {
  return request({
    url: `/requirement/${id}`,
    method: 'get'
  })
}

// 首页汇总统计（修正路径）
export function statTotalApi() {
  return request.get('/requirement/stat/total')
}

// 获取首页最近需求
export const getRecentReqApi = (pageSize) => {
  return request({
    url: '/requirement/recent',
    method: 'get',
    params: { pageSize }
  })
}

// 统计分析多维度分组查询
export const statGroupApi = (params) => {
  return request.get('/requirement/stat/group', { params })
}

// 获取全部区域下拉选项
export const getRegionListApi = () => {
  return request.get('/requirement/region/list')
}

// 获取所有启用区域（新增需求、统计分析下拉专用）
export const getEnableRegionApi = () => {
  return request.get('/system/region/enableList')
}

// ✅ 删除旧的 getHomeStatApi，统一使用 statTotalApi
// export const getHomeStatApi = () => request.get('/system/requirement/homeStat')
