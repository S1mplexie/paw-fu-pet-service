/**
 * 时间计算工具函数
 */

/**
 * 格式化时间差（相对时间）
 * @param {String|Date} createTime 创建时间
 * @returns {String} 时间差描述（如"2小时前"）
 */
export function formatTimeDifference(createTime) {
  if (!createTime) {
    return '时间未知'
  }
  
  const create = new Date(createTime)
  const now = new Date()
  const diff = now - create
  
  if (diff < 0) {
    return '时间异常'
  }
  
  const SECOND = 1000
  const MINUTE = 60 * SECOND
  const HOUR = 60 * MINUTE
  const DAY = 24 * HOUR
  const MONTH = 30 * DAY
  const YEAR = 365 * DAY
  
  if (diff < SECOND) {
    return '刚刚'
  } else if (diff < MINUTE) {
    return Math.floor(diff / SECOND) + '秒前'
  } else if (diff < HOUR) {
    return Math.floor(diff / MINUTE) + '分钟前'
  } else if (diff < DAY) {
    return Math.floor(diff / HOUR) + '小时前'
  } else if (diff < MONTH) {
    return Math.floor(diff / DAY) + '天前'
  } else if (diff < YEAR) {
    return Math.floor(diff / MONTH) + '个月前'
  } else {
    return Math.floor(diff / YEAR) + '年前'
  }
}

/**
 * 格式化完整时间
 * @param {String|Date} time 时间
 * @returns {String} 完整时间格式（YYYY-MM-DD HH:mm:ss）
 */
export function formatFullTime(time) {
  if (!time) {
    return ''
  }
  
  const date = new Date(time)
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  const hour = String(date.getHours()).padStart(2, '0')
  const minute = String(date.getMinutes()).padStart(2, '0')
  const second = String(date.getSeconds()).padStart(2, '0')
  
  return `${year}-${month}-${day} ${hour}:${minute}:${second}`
}
