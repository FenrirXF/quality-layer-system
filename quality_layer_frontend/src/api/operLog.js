import request from '@/utils/request'

// 操作日志分页查询
export const getLogPageApi = (params) => {
  return request.get('/operLog/page', { params })
}
// 操作日志详情
export const getLogDetailApi = (id) => {
  return request.get(`/operLog/${id}`)
}