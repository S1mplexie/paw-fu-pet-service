import request from '../utils/request'

export function aiChat(data) {
  return request({
    url: '/ai/chat',
    method: 'post',
    data
  })
}

export function getQuickQuestions() {
  return request({
    url: '/ai/quick-questions',
    method: 'get'
  })
}

export function getRemainingCount() {
  return request({
    url: '/ai/remaining-count',
    method: 'get'
  })
}
