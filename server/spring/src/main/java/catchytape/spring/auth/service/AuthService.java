package catchytape.spring.auth.service;

import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import catchytape.spring.auth.controller.dto.UserAuthResponse;
import catchytape.spring.auth.service.dto.GoogleTokenResponse;
import catchytape.spring.common.exception.CatchyException;
import catchytape.spring.user.User;
import catchytape.spring.user.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import java.util.UUID;

@Slf4j
@Service
@AllArgsConstructor
@Transactional
public class AuthService {
    private final RedisService redisService;
    private final JwtService jwtService;
    private final UserRepository userRepository;

    public String getUserEmail(String googleIdToken) throws CatchyException {
        String googleApiUrl = "https://oauth2.googleapis.com/tokeninfo?id_token="+googleIdToken;

        try {
            WebClient client = WebClient.create();
            GoogleTokenResponse response = client.get()
                .uri(googleApiUrl)
                .retrieve()
                .bodyToMono(GoogleTokenResponse.class)
                .block();
        
            if(response.email() == null) {
                throw new CatchyException("UNAUTHORIZED", "EXPIRED_TOKEN");
            }

            return response.email();

        } catch(Exception e) {
            if(e instanceof CatchyException) {
                throw e;
            }

            throw new CatchyException("INTERNAL_SERVER_ERROR", "SERVER_ERROR");
        }
    }

    public UserAuthResponse login(String userEmail) throws CatchyException {
        try {
            User user = this.userRepository.findByUserEmail(userEmail);

            if(user == null) {
                throw new CatchyException("UNAUTHORIZED", "NOT_EXIST_USER");
            }

            String userId = user.getUserId();
            String refreshId = UUID.randomUUID().toString();
            this.redisService.setValue(refreshId, userId);

            return this.jwtService.generateJwtToken(userId, refreshId);
        } catch(Exception e) {
            if(e instanceof CatchyException) {
                throw e;
            }

            throw new CatchyException("INTERNAL_SERVER_ERROR", "SERVER_ERROR");
        }
    }

    public UserAuthResponse signup(String googleIdToken, String nickname) throws CatchyException {
        try {
            String userEmail = this.getUserEmail(googleIdToken);

            if(this.userRepository.findByUserEmail(userEmail) != null) {
                throw new CatchyException("BAD_REQUEST", "ALREADY_EXIST_EMAIL");
            }

            String userId = UUID.randomUUID().toString();
            User newUser = new User(userId, nickname, userEmail);
            this.userRepository.save(newUser);

            return this.login(userEmail);
        } catch(Exception e) {
            if(e instanceof CatchyException) {
                throw e;
            }

            throw new CatchyException("INTERNAL_SERVER_ERROR", "SERVER_ERROR");
        }
    }

    public UserAuthResponse refreshToken(String refreshToken) throws CatchyException {
        try {
            this.jwtService.isValidToken(refreshToken);
            
            String refreshId = this.jwtService.decodeToken("refresh_id", refreshToken);

            String userId = this.redisService.getValue(refreshId);

            if(userId == null) {
                throw new CatchyException("UNAUTHORIZED", "NOT_EXIST_USER");
            }

            String newRefreshId = UUID.randomUUID().toString();
            this.redisService.deleteValue(refreshId);
            this.redisService.setValue(newRefreshId, userId);

            return this.jwtService.generateJwtToken(userId, newRefreshId);
        } catch(Exception e) {
            if(e instanceof CatchyException) {
                throw e;
            }

            throw new CatchyException("INTERNAL_SERVER_ERROR", "SERVER_ERROR");
        }
    }

    public User test() {
        User user = this.userRepository.findByUserEmail("sugamypapa@gmail.com");
        return user;
    }
}
