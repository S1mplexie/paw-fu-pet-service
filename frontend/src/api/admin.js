import request from '../utils/request'

export function adminLogin(data) {
  return request({
    url: '/admin/login',
    method: 'post',
    data
  })
}

export function adminLogout() {
  return request({
    url: '/admin/logout',
    method: 'post'
  })
}

export function getAdminInfo() {
  return request({
    url: '/admin/info',
    method: 'get'
  })
}

export function getDashboardStats() {
  return request({
    url: '/admin/dashboard/stats',
    method: 'get'
  })
}

export function getUserList(params) {
  return request({
    url: '/admin/users',
    method: 'get',
    params
  })
}

export function getUserDetail(userId) {
  return request({
    url: `/admin/users/${userId}`,
    method: 'get'
  })
}

export function disableUser(userId) {
  return request({
    url: `/admin/users/${userId}/disable`,
    method: 'put'
  })
}

export function enableUser(userId) {
  return request({
    url: `/admin/users/${userId}/enable`,
    method: 'put'
  })
}

export function deleteUser(userId) {
  return request({
    url: `/admin/users/${userId}`,
    method: 'delete'
  })
}

export function getPetList(params) {
  return request({
    url: '/admin/pets',
    method: 'get',
    params
  })
}

export function getPetDetail(petId) {
  return request({
    url: `/admin/pets/${petId}`,
    method: 'get'
  })
}

export function offlinePet(petId) {
  return request({
    url: `/admin/pets/${petId}/offline`,
    method: 'put'
  })
}

export function deletePet(petId) {
  return request({
    url: `/admin/pets/${petId}`,
    method: 'delete'
  })
}
