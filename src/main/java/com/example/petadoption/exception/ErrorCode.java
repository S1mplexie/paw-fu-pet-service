package com.example.petadoption.exception;

import lombok.Getter;

/**
 * 错误码枚举
 */
@Getter
public enum ErrorCode {

    SUCCESS(200, "操作成功"),
    PARAM_ERROR(400, "参数错误"),
    UNAUTHORIZED(401, "未授权"),
    FORBIDDEN(403, "无权限"),
    NOT_FOUND(404, "资源不存在"),
    METHOD_NOT_ALLOWED(405, "方法不允许"),
    SYSTEM_ERROR(500, "系统错误"),

    USER_ALREADY_EXISTS(1001, "用户已存在"),
    USER_NOT_FOUND(1002, "用户不存在"),
    PASSWORD_ERROR(1003, "密码错误"),
    PASSWORD_FORMAT_ERROR(1004, "密码格式错误，需8-20位且包含数字和字母"),
    USERNAME_FORMAT_ERROR(1005, "用户名格式错误"),
    PHONE_FORMAT_ERROR(1006, "手机号格式错误"),
    EMAIL_FORMAT_ERROR(1007, "邮箱格式错误"),
    USER_DISABLED(1008, "用户已被禁用"),

    TOKEN_INVALID(2001, "Token无效"),
    TOKEN_EXPIRED(2002, "Token已过期"),
    TOKEN_MISSING(2003, "Token缺失"),

    PET_NOT_FOUND(3001, "领养信息不存在"),
    PET_ALREADY_ADOPTED(3002, "宠物已被领养"),
    PET_OFFLINE(3003, "领养信息已下架"),
    PET_CATEGORY_NOT_FOUND(3004, "宠物种类不存在"),
    NOT_PUBLISHER(3005, "非发布者无权操作"),
    CANNOT_CONTACT_SELF(3006, "不能联系自己发布的信息"),
    PET_ADOPTED_CANNOT_EDIT(3007, "已领养宠物不可修改"),
    PET_STATUS_CANNOT_ONLINE(3008, "宠物状态不允许上架"),

    CONTACT_ALREADY_SENT(4001, "24小时内已发送过联系"),
    CONTACT_NOT_FOUND(4002, "联系记录不存在"),

    FILE_TYPE_ERROR(5001, "文件类型不支持"),
    FILE_SIZE_ERROR(5002, "文件大小超过限制"),
    FILE_UPLOAD_ERROR(5003, "文件上传失败");

    private final Integer code;
    private final String message;

    ErrorCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
