package com.example.petadoption.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.petadoption.entity.AuditLog;
import com.example.petadoption.mapper.AuditLogMapper;
import com.example.petadoption.service.AuditLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
public class AuditLogServiceImpl extends ServiceImpl<AuditLogMapper, AuditLog> implements AuditLogService {

    @Override
    public void log(String operatorId, String operatorName, String operatorRole,
                    String operationType, String operationTarget, String operationDetail,
                    String ipAddress, String result, String errorMessage) {
        AuditLog auditLog = new AuditLog();
        auditLog.setId(IdUtil.fastSimpleUUID());
        auditLog.setOperatorId(operatorId);
        auditLog.setOperatorName(operatorName);
        auditLog.setOperatorRole(operatorRole);
        auditLog.setOperationType(operationType);
        auditLog.setOperationTarget(operationTarget);
        auditLog.setOperationDetail(operationDetail);
        auditLog.setIpAddress(ipAddress);
        auditLog.setOperationTime(LocalDateTime.now());
        auditLog.setResult(result);
        auditLog.setErrorMessage(errorMessage);
        
        this.save(auditLog);
        log.debug("审计日志记录成功: {}", operationType);
    }

    @Override
    public void logSuccess(String operatorId, String operatorName, String operatorRole,
                           String operationType, String operationTarget, String operationDetail,
                           String ipAddress) {
        log(operatorId, operatorName, operatorRole, operationType, operationTarget, 
            operationDetail, ipAddress, "SUCCESS", null);
    }

    @Override
    public void logFailure(String operatorId, String operatorName, String operatorRole,
                           String operationType, String operationTarget, String operationDetail,
                           String ipAddress, String errorMessage) {
        log(operatorId, operatorName, operatorRole, operationType, operationTarget, 
            operationDetail, ipAddress, "FAILURE", errorMessage);
    }
}
