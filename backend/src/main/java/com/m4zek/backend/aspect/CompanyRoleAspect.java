package com.m4zek.backend.aspect;

import com.m4zek.backend.annotations.HasAnyCompanyRole;
import com.m4zek.backend.exception.AccessDeniedException;
import com.m4zek.backend.security.service.MyUserDetails;
import com.m4zek.backend.service.CompanyRoleService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;


@Aspect
@Component
public class CompanyRoleAspect {

    private final CompanyRoleService companyRoleService;

    public CompanyRoleAspect(CompanyRoleService companyRoleService) {
        this.companyRoleService = companyRoleService;
    }


    @Before("@annotation(hasAnyCompanyRole)")
    public void checkUserCompanyRole(JoinPoint joinPoint, HasAnyCompanyRole hasAnyCompanyRole) {
        MyUserDetails userDetails = (MyUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Integer userId = userDetails.getId();
        Integer companyId = extractCompanyId(joinPoint);
        List<String> requiredRoles = Arrays.stream(hasAnyCompanyRole.value()).toList();

        boolean hasAnyRole = this.companyRoleService.hasAnyRoleInCompany(companyId, userId, requiredRoles);

        if(!hasAnyRole) {
            throw new AccessDeniedException("Permission denied");
        }

        requiredRoles.forEach(role -> System.out.printf("[%d]-[%d] -> [%s]%n", userId, companyId, role));
    }

    private Integer extractCompanyId(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();

        Object[] args = joinPoint.getArgs();
        String[] paramNames = signature.getParameterNames();

        for (int i = 0; i < paramNames.length; i++) {
            if ("companyId".equals(paramNames[i])) {
                return (int) args[i];
            }
        }
        return null;
    }

}
