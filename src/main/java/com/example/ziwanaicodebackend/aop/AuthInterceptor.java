package com.example.ziwanaicodebackend.aop;

import com.example.ziwanaicodebackend.annotation.AuthCheck;
import com.example.ziwanaicodebackend.exception.BusinessException;
import com.example.ziwanaicodebackend.exception.ErrorCode;
import com.example.ziwanaicodebackend.model.entity.User;
import com.example.ziwanaicodebackend.model.enums.UserRoleEnum;
import com.example.ziwanaicodebackend.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;


@Aspect
@Component
public class AuthInterceptor {

    @Resource
    private UserService userService;


    /**
     * 执行拦截
     *
     * @param proceedingJoinPoint 切入点
     * @param authCheck           鉴权注解
     * @return 放行
     */
    @Around("@annotation(authCheck)")
    public Object doInterceptor(ProceedingJoinPoint proceedingJoinPoint, AuthCheck authCheck) throws Throwable {
        String mustRole = authCheck.mustRole();
        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
        User loginUser = userService.getLoginUser(request);
        UserRoleEnum mustRoleEnum = UserRoleEnum.getValueByRole(mustRole);
        // 不需要权限，直接放行
        if (mustRoleEnum == null) {
            return proceedingJoinPoint.proceed();
        }
        String userRole = loginUser.getUserRole();
        UserRoleEnum loginUserRoleEnum = UserRoleEnum.getValueByRole(userRole);
        if (loginUserRoleEnum == null) {
            throw new BusinessException(ErrorCode.NO_AUTO_ERROR);
        }
        if (!UserRoleEnum.ADMIN.equals(loginUserRoleEnum) && UserRoleEnum.ADMIN.equals(mustRoleEnum)) {
            throw new BusinessException(ErrorCode.NO_AUTO_ERROR);
        }

        return proceedingJoinPoint.proceed();
    }


}
