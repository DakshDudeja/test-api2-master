package com.klipvr.backend.security.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;

@Service
public class SecurityService {
    @Autowired
    HttpServletRequest httpServletRequest;

//    @Autowired
 //   CookieUtils cookieUtils;

  //  @Autowired
 //   SecurityProperties securityProps;
    public UserDetailsImpl getUser()
    {
        UserDetailsImpl userDtoPrincipal = null;
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Object principal = securityContext.getAuthentication().getPrincipal();
        if (principal instanceof UserDetailsImpl)
        {
            userDtoPrincipal = ((UserDetailsImpl) principal);
        }
        return userDtoPrincipal;
    }
    public String getBearerToken(HttpServletRequest request)
    {
        String bearerToken = null;
        String authorization = request.getHeader("Authorization");
        if (StringUtils.hasText(authorization) && authorization.startsWith("Bearer "))
        {
            bearerToken = authorization.substring(7);
        }
        return bearerToken;
    }
}
