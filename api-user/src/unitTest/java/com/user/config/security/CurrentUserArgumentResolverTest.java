package com.user.config.security;

import com.storage.entity.User;
import com.user.utils.error.CommonException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.core.MethodParameter;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;

import static com.user.enums.ErrorType.LOGIN_REQUIRED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@WebMvcTest(CurrentUserArgumentResolver.class)
class CurrentUserArgumentResolverTest {

    @Mock
    private MethodParameter parameter;
    @Mock
    private ModelAndViewContainer mavContainer;
    @Mock
    private NativeWebRequest webRequest;

    @InjectMocks
    private CurrentUserArgumentResolver resolver;

    @Test
    @DisplayName("CurrentUser 어노테이션이 존재하면 파라미터를 지원한다")
    void supportsParameterWhenCurrenUserAnnotationExists() {
        given(parameter.hasParameterAnnotation(CurrentUser.class)).willReturn(true);
        assertThat(resolver.supportsParameter(parameter)).isTrue();
    }

    @Test
    @DisplayName("CurrentUser 어노테이션이 존재하지 않으면 파라미터를 지원하지 않는다.")
    void supportsParameterWhenCurrenUserAnnotationNotExists() {
        given(parameter.hasParameterAnnotation(CurrentUser.class)).willReturn(false);
        assertThat(resolver.supportsParameter(parameter)).isFalse();
    }

    @Test
    @WithMockUserPrincipal
    @DisplayName("CurrentUser required(true)일때 인증된 사용자가 존재하면 사용자를 반환한다.")
    void authenticatedUserAndAnnotationCurrenUserExists() {
        // given
        CurrentUser currentUser = mock(CurrentUser.class);
        given(parameter.getParameterAnnotation(CurrentUser.class)).willReturn(currentUser);
        given(currentUser.required()).willReturn(true);

        // when
        Object result = resolver.resolveArgument(parameter, mavContainer, webRequest, null);

        // then
        assertThat(result).isNotNull();
        assertThat(result).isInstanceOf(User.class);
    }

    @Test
    @WithAnonymousUser
    @DisplayName("CurrentUser required(true)일때 Anonymous 사용자 이면 예외가 발생된다.")
    void anonymousUserAndAnnotationCurrenUserExists() {
        // given
        CurrentUser currentUser = mock(CurrentUser.class);
        given(parameter.getParameterAnnotation(CurrentUser.class)).willReturn(currentUser);
        given(currentUser.required()).willReturn(true);

        // when && then
        assertThatThrownBy(() -> resolver.resolveArgument(parameter, mavContainer, webRequest, null))
                .isInstanceOf(CommonException.class)
                .hasMessage(LOGIN_REQUIRED.getMessage());
    }

    @Test
    @DisplayName("CurrentUser required(true)일때 인증된 사용자가 존재하지 않으면 예외를 발생시킨다.")
    void notAuthenticatedUserAndAnnotationCurrenUserExists() {
        // given
        CurrentUser currentUser = mock(CurrentUser.class);
        given(parameter.getParameterAnnotation(CurrentUser.class)).willReturn(currentUser);
        given(currentUser.required()).willReturn(true);

        // when && then
        assertThatThrownBy(() -> resolver.resolveArgument(parameter, mavContainer, webRequest, null))
                .isInstanceOf(CommonException.class)
                .hasMessage(LOGIN_REQUIRED.getMessage());
    }

    @Test
    @WithAnonymousUser
    @DisplayName("CurrentUser required(false)일때 Anonymous 사용자 이면 null을 반환한다.")
    void anonymousUserAndAnnotationCurrenUserNotExists() {
        // given
        CurrentUser currentUser = mock(CurrentUser.class);
        given(parameter.getParameterAnnotation(CurrentUser.class)).willReturn(currentUser);
        given(currentUser.required()).willReturn(false);

        // when
        Object result = resolver.resolveArgument(parameter, mavContainer, webRequest, null);

        // then
        assertThat(result).isNull();
    }

    @Test
    @WithMockUserPrincipal
    @DisplayName("CurrentUser required(false)일때 인증된 사용자가 존재하면 사용자를 반환한다.")
    void authenticatedUserAndAnnotationCurrenUserNotExists() {
        // given
        CurrentUser currentUser = mock(CurrentUser.class);
        given(parameter.getParameterAnnotation(CurrentUser.class)).willReturn(currentUser);
        given(currentUser.required()).willReturn(false);

        // when
        Object result = resolver.resolveArgument(parameter, mavContainer, webRequest, null);

        // then
        assertThat(result).isNotNull();
        assertThat(result).isInstanceOf(User.class);
    }

    @Test
    @DisplayName("CurrentUser required(false)일때 인증된 사용자가 존재하지 않으면 null을 반환한다.")
    void notAuthenticatedUserAndAnnotationCurrenUserNotExists() {
        // given
        CurrentUser currentUser = mock(CurrentUser.class);
        given(parameter.getParameterAnnotation(CurrentUser.class)).willReturn(currentUser);
        given(currentUser.required()).willReturn(false);

        // when
        Object result = resolver.resolveArgument(parameter, mavContainer, webRequest, null);

        // then
        assertThat(result).isNull();
    }
}