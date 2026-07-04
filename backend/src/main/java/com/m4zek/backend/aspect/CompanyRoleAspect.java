package com.m4zek.backend.aspect;

import com.m4zek.backend.annotations.HasAnyCompanyRole;
import com.m4zek.backend.exception.AccessDeniedException;
import com.m4zek.backend.security.service.MyUserDetails;
import com.m4zek.backend.service.UserCompanyService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;


@Aspect
@Component
public class CompanyRoleAspect {

    private final static Logger logger = LoggerFactory.getLogger(CompanyRoleAspect.class);

    private final UserCompanyService userCompanyService;

    public CompanyRoleAspect(UserCompanyService userCompanyService) {
        this.userCompanyService = userCompanyService;
    }


    @Before("@annotation(hasAnyCompanyRole)")
    public void checkUserCompanyRole(JoinPoint joinPoint, HasAnyCompanyRole hasAnyCompanyRole) {
        MyUserDetails userDetails = (MyUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Integer userId = userDetails.getId();
        Integer companyId = extractCompanyId(joinPoint);
        List<String> requiredRoles = Arrays.stream(hasAnyCompanyRole.value()).toList();

        boolean hasAnyRole = this.userCompanyService.hasAnyRoleInCompany(companyId, userId, requiredRoles);

        if(!hasAnyRole) {
            Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();

            RequestMapping mapping =
                    AnnotatedElementUtils.findMergedAnnotation(
                            method,
                            RequestMapping.class
                    );

            if(mapping != null)
                logger.info("[PERMISSION DENIED] User [{}] cannot invoke endpoint {}",
                    userDetails.getEmail(),
                        Arrays.toString(mapping.path())
                );
            throw new AccessDeniedException("Permission denied");
        }
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
