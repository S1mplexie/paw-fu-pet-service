package com.example.petadoption.service;

/**
 * 浏览量统计服务接口
 */
public interface ViewStatService {

    /**
     * 记录浏览并更新浏览量
     * @param petId 宠物ID
     * @param userId 用户ID（可为null，表示游客）
     * @param request HttpServletRequest用于获取IP和UserAgent
     * @return 是否成功记录浏览
     */
    boolean recordView(String petId, String userId, javax.servlet.http.HttpServletRequest request);

    /**
     * 检查是否已浏览
     * @param userIdentifier 用户标识
     * @param petId 宠物ID
     * @return 是否已浏览
     */
    boolean hasViewed(String userIdentifier, String petId);

    /**
     * 检查是否为可疑刷量行为
     * @param userIdentifier 用户标识
     * @return 是否可疑
     */
    boolean isSuspicious(String userIdentifier);
}
