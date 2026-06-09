package com.example.petadoption.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.petadoption.entity.AuditLog;

public interface AuditLogService extends IService<AuditLog> {

    void log(String operatorId, String operatorName, String operatorRole, 
             String operationType, String operationTarget, String operationDetail,
             String ipAddress, String result, String errorMessage);
    
    void logSuccess(String operatorId, String operatorName, String operatorRole,
                    String operationType, String operationTarget, String operationDetail,
                    String ipAddress);
    
    void logFailure(String operatorId, String operatorName, String operatorRole,
                    String operationType, String operationTarget, String operationDetail,
                    String ipAddress, String errorMessage);
}
