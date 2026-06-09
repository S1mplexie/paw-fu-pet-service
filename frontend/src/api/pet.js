import request from '../utils/request'

export function getPetList(params) {
  return request({
    url: '/pet/list',
    method: 'get',
    params
  })
}

export function getPetDetail(id) {
  return request({
    url: `/pet/detail/${id}`,
    method: 'get'
  })
}

export function publishPet(data) {
  return request({
    url: '/pet/publish',
    method: 'post',
    data
  })
}

export function updatePet(id, data) {
  return request({
    url: `/pet/update/${id}`,
    method: 'put',
    data
  })
}

export function getMyPets(params) {
  return request({
    url: '/pet/my-pets',
    method: 'get',
    params
  })
}

export function offlinePet(id) {
  return request({
    url: `/pet/offline/${id}`,
    method: 'put'
  })
}

export function onlinePet(id) {
  return request({
    url: `/pet/online/${id}`,
    method: 'put'
  })
}

export function deletePet(id) {
  return request({
    url: `/pet/${id}`,
    method: 'delete'
  })
}
