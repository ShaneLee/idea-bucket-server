package ee.shanel.ideabucket.service;

import ee.shanel.ideabucket.model.LoginRequest;
import ee.shanel.ideabucket.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class DefaultLoginServiceTest
{
    private static final String TOKEN = "TOKEN";

    private static final String EMAIL = "EMAIL";

    private static final LoginRequest REQUEST = LoginRequest.builder().email(EMAIL).build();

    private static final User USER = createUser();

    @Mock
    private UserService mockUserService;

    @Mock
    private SenderService mockSenderService;

    @Mock
    private TokenService mockTokenService;

    private DefaultLoginService subject;

    @BeforeEach
    void setUp()
    {
        subject = new DefaultLoginService(
                mockUserService,
                mockSenderService,
                mockTokenService
        );
    }

    @Test
    void itLoginsWhenTheUserAndTokenExists()
    {
        Mockito.when(mockUserService.findUserByEmail(Mockito.anyString()))
                .thenReturn(Mono.just(USER));
        Mockito.when(mockTokenService.getByEmail(Mockito.anyString()))
                .thenReturn(Mono.just(TOKEN));
        Mockito.when(mockSenderService.send(Mockito.anyString(), Mockito.anyString()))
                .thenReturn(Mono.empty());

        StepVerifier.create(subject.login(REQUEST))
                .expectNext(Boolean.TRUE)
                .verifyComplete();

        Mockito.verify(mockUserService).findUserByEmail(EMAIL);
        Mockito.verify(mockTokenService).getByEmail(EMAIL);
        Mockito.verify(mockSenderService).send(EMAIL, TOKEN);
    }

    @Test
    void itLoginsWhenTheUserExistsButTokenDoesntExist()
    {
        Mockito.when(mockUserService.findUserByEmail(Mockito.anyString()))
                .thenReturn(Mono.just(USER));
        Mockito.when(mockTokenService.getByEmail(Mockito.anyString()))
                .thenReturn(Mono.empty());
        Mockito.when(mockTokenService.create(Mockito.any()))
                .thenReturn(Mono.just(TOKEN));
        Mockito.when(mockSenderService.send(Mockito.anyString(), Mockito.anyString()))
                .thenReturn(Mono.empty());

        StepVerifier.create(subject.login(REQUEST))
                .expectNext(Boolean.TRUE)
                .verifyComplete();

        Mockito.verify(mockUserService).findUserByEmail(EMAIL);
        Mockito.verify(mockTokenService).getByEmail(EMAIL);
        Mockito.verify(mockTokenService).create(USER);
        Mockito.verify(mockSenderService).send(EMAIL, TOKEN);
    }

    @Test
    void itDoesntLoginWhenTheUserDoesntExist()
    {
        Mockito.when(mockUserService.findUserByEmail(Mockito.anyString()))
                .thenReturn(Mono.empty());

        StepVerifier.create(subject.login(REQUEST))
                .expectNext(Boolean.FALSE)
                .verifyComplete();

        Mockito.verify(mockUserService).findUserByEmail(EMAIL);
    }

    private static User createUser()
    {
        return User.builder()
                .email(EMAIL)
                .build();
    }
}
