package org.example.expert.config.aop;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

//@Aspect
//@Component
//@Slf4j
//public class AdminAuthAspect {
//    @Around("execution(* org.example.expert.domain.comment.controller.CommentAdminController.deleteComment(..)) || " +
//            "execution(* org.example.expert.domain.user.controller.UserAdminController.changeUserRole(..))")
//    public Object LoginAdminAuthAspect(ProceedingJoinPoint joinPoint) throws Throwable {
//        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();//프레임 워크에서 관리하는 정보를 담은 상자
//        if (attributes == null) {// 비정상적인 접근을 막음
//            throw new IllegalStateException("유효하지 않은 요청입니다.");
//        }
//
//        HttpServletRequest request =  attributes.getRequest();//ServletRequestAttributes라는 상자에서 정보를 가져온다
//        Long userId = (Long) request.getAttribute("userId");
//        String url =  request.getRequestURL().toString();
//        //어떻게 시간과 리퀘스트와 리스폰을 받아올건가? 반복문?
//
//
//    }
//
//
//}
