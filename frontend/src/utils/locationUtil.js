/**
 * 位置信息格式化工具
 */

/**
 * 格式化位置信息展示
 * @param {String} province 省份
 * @param {String} city 城市
 * @returns {String} 格式化后的位置文本
 */
export function formatLocation(province, city) {
  if (province && city) {
    return `📍 ${province} ${city}`
  } else if (province) {
    return `📍 ${province}`
  } else {
    return '📍 暂无信息'
  }
}

/**
 * 检查位置信息是否完整
 * @param {String} province 省份
 * @param {String} city 城市
 * @returns {Boolean} 是否完整
 */
export function isLocationComplete(province, city) {
  return !!(province && city)
}
