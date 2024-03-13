package catchytape.spring.auth.controller;

import org.springframework.web.bind.annotation.RestController;

import catchytape.spring.auth.controller.dto.UserSignupRequest;
import catchytape.spring.auth.controller.dto.UserAuthResponse;
import catchytape.spring.auth.controller.dto.UserLoginRequest;
import catchytape.spring.auth.controller.dto.UserRefreshRequest;
import catchytape.spring.auth.service.AuthService;
import catchytape.spring.auth.service.RedisService;
import catchytape.spring.common.exception.CatchyException;
import catchytape.spring.recentPlayed.RecentPlayed;
import catchytape.spring.user.User;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/users")
public class AuthController {
    
    private final AuthService authService;

    @PostMapping(value="/signup", consumes="application/json;charset=UTF-8")
    public ResponseEntity<UserAuthResponse> signup(@RequestBody UserSignupRequest request) throws CatchyException{
        log.info("POST /users/signup - body = nickname: " + request.nickname());
        
        return ResponseEntity.ok(authService.signup(request.idToken(), request.nickname()));
    }

    @PostMapping(value="/login", consumes="application/json;charset=UTF-8")
    public ResponseEntity<UserAuthResponse> login(@RequestBody UserLoginRequest request) throws CatchyException {
        log.info("POST /users/signup - body = idToken: ");

        return ResponseEntity.ok(authService.login(request.idToken()));
    }

    @PostMapping(value="/refresh", consumes="application/json;charset=UTF-8")
    public ResponseEntity<UserAuthResponse> refresh(@RequestBody UserRefreshRequest request) throws CatchyException {
        log.info("POST /users/refresh - body = refreshToken: ", request.refreshToken());

        return ResponseEntity.ok(this.authService.refreshToken(request.refreshToken()));
    }

    @GetMapping("/test")
    public ResponseEntity<User> test() throws CatchyException {
        return ResponseEntity.ok(this.authService.test());
    }
}
