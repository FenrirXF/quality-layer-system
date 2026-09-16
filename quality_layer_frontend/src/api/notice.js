import request from '@/utils/request'

/**
 * 获取当前用户未读通知数量（右上角角标数字）
 */
export const getNoticeUnreadApi = () => {
  return request.get('/system/notice/unreadCount')
}

/**
 * 获取通知列表（铃铛弹窗内容）
 */
export const getNoticeListApi = () => {
  return request.get('/system/notice/list')
}

/**
 * 标记通知为已读
 * @param {number} id 通知ID
 */
export const markNoticeReadApi = (id) => {
  return request.put(`/system/notice/read/${id}`)
}
