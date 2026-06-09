package com.example.petadoption.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("audit_log")
public class AuditLog implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    @TableField("operator_id")
    private String operatorId;

    @TableField("operator_name")
    private String operatorName;

    @TableField("operator_role")
    private String operatorRole;

    @TableField("operation_type")
    private String operationType;

    @TableField("operation_target")
    private String operationTarget;

    @TableField("operation_detail")
    private String operationDetail;

    @TableField("ip_address")
    private String ipAddress;

    @TableField("operation_time")
    private LocalDateTime operationTime;

    private String result;

    @TableField("error_message")
    private String errorMessage;
}
