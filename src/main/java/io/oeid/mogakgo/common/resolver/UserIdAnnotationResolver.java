package io.oeid.mogakgo.common.resolver;


import io.oeid.mogakgo.common.annotation.UserId;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

public class UserIdAnnotationResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterAnnotation(UserId.class) != null;
    }

    @Override
    public Object resolveArgument(
        MethodParameter parameter, ModelAndViewContainer mavContainer,
        NativeWebRequest webRequest, WebDataBinderFactory binderFactory
    ) {
        Long userId = (Long) webRequest.getAttribute("userId", RequestAttributes.SCOPE_REQUEST);
        if (userId == null) {
            // TODO: 2024-02-17 Should Change to Custom Exception
            throw new IllegalArgumentException("AUTH_MISSING_CREDENTIALS");
        }
        return userId;
    }

}
