import request from '@/utils/request'

export const loginApi = (data) => request.post('/login', data)
// 用户分页
export const getUserPageApi = (params) => request.get('/user/page', { params })

// 新增用户
export const addUserApi = (data) => request.post('/user/add', data)

// 编辑用户
export const updateUserApi = (data) => request.put('/user/update', data)

// 删除用户
export const delUserApi = (id) => request.delete(`/user/${id}`)

// 修改密码
export const updatePwdApi = (data) => request.put('/user/pwd', data)

// 修改状态
export const updateStatusApi = (id, status) => request.put(`/user/status/${id}/${status}`)

// 根据角色编码查询所属用户
export const getUserByRoleApi = (role) => request.get('/user/listByRole', {
  params: {
    role
  }
})

//注册
export const registerApi = (data) => {
  return request({
    url: '/user/register',
    method: 'post',
    data
  })
}