package com.geoxus.core.common.exception;

import cn.hutool.http.HttpStatus;
import com.geoxus.core.common.util.GXResultUtils;
import com.geoxus.core.common.vo.GXResultCode;
import com.mysql.jdbc.exceptions.MySQLIntegrityConstraintViolationException;
import com.mysql.jdbc.exceptions.jdbc4.MySQLSyntaxErrorException;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.AuthorizationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.ValidationException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * 异常处理器
 */
@RestControllerAdvice
@Slf4j
public class GXExceptionHandler {
    /**
     * 处理自定义异常
     */
    @ExceptionHandler(GXException.class)
    public GXResultUtils handleRRException(GXException e) {
        log.error(e.getMessage(), e);
        return GXResultUtils.error(e.getCode(), e.getMsg());
    }

    @ExceptionHandler(GXBeanValidateException.class)
    public GXResultUtils handleRRBeanValidateException(GXBeanValidateException e) {
        log.error(e.getMessage(), e);
        return GXResultUtils.error(e.getCode(), e.getMsg());
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public GXResultUtils handlerNoFoundException(Exception e) {
        log.error(e.getMessage(), e);
        return GXResultUtils.error(404, "路径不存在，请检查路径是否正确");
    }

    @ExceptionHandler(DuplicateKeyException.class)
    public GXResultUtils handleDuplicateKeyException(DuplicateKeyException e) {
        log.error(e.getMessage(), e);
        return GXResultUtils.error("数据库中已存在该记录");
    }

    @ExceptionHandler(Exception.class)
    public GXResultUtils handleException(Exception e) {
        log.error(e.getMessage(), e);
        return GXResultUtils.error().putData("系统错误");
    }

    @ExceptionHandler(BindException.class)
    public GXResultUtils handleBindException(BindException e) {
        log.error(e.getMessage(), e);
        Map<String, Object> errors = new HashMap<>();
        for (FieldError error : e.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }
        return GXResultUtils.error(GXResultCode.COMMON_ERROR).putData(errors);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public GXResultUtils handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        Map<String, Object> errors = new HashMap<>();
        for (FieldError error : e.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }
        log.error(e.getMessage(), e);
        return GXResultUtils.error(GXResultCode.COMMON_ERROR).putData(errors);
    }

    @ExceptionHandler(ValidationException.class)
    public GXResultUtils handleValidationException(ValidationException e) {
        log.error(e.getMessage(), e);
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("msg", e.getCause().getMessage());
        return GXResultUtils.error(GXResultCode.COMMON_ERROR).putData(hashMap);
    }

    /**
     * 处理MySQL语法错误异常,主要解决的是调用自定义函数时发生的错误
     *
     * @param e
     */
    @ExceptionHandler(value = {MySQLSyntaxErrorException.class, MySQLIntegrityConstraintViolationException.class})
    public GXResultUtils handleMysqlSyntaxError(Exception e) {
        log.error(e.getMessage(), e);
        return GXResultUtils.error().putData(e.getMessage());
    }

    @ExceptionHandler(AuthorizationException.class)
    public GXResultUtils handleException(AuthorizationException e) {
        log.error(e.getMessage(), e);
        return GXResultUtils.error(403, "没有权限进行此操作");
    }

    @ExceptionHandler(MultipartException.class)
    public GXResultUtils handleMultipartException(MultipartException e, RedirectAttributes redirectAttributes) {
        log.error(e.getMessage(), e);
        return GXResultUtils.error(HttpStatus.HTTP_INTERNAL_ERROR, e.getCause().getMessage());
    }

    @ExceptionHandler(java.net.BindException.class)
    public GXResultUtils handleBindException(MultipartException e, RedirectAttributes redirectAttributes) {
        log.error(e.getMessage(), e);
        return GXResultUtils.error(HttpStatus.HTTP_INTERNAL_ERROR, e.getMessage());
    }

    @ExceptionHandler(GXTokenEmptyException.class)
    public GXResultUtils handleGXTokenEmptyException(GXTokenEmptyException e, RedirectAttributes redirectAttributes) {
        log.error(e.getMessage(), e);
        return GXResultUtils.error(HttpStatus.HTTP_INTERNAL_ERROR, e.getMessage());
    }

    @ExceptionHandler(SQLException.class)
    public GXResultUtils handleSQLException(SQLException e, RedirectAttributes redirectAttributes) {
        log.error(e.getMessage(), e);
        return GXResultUtils.error(HttpStatus.HTTP_INTERNAL_ERROR, e.getMessage());
    }

}
