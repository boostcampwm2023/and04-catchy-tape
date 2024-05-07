package catchytape.spring.auth.controller.dto;

import lombok.Builder;

@Builder
public record UserAuthResponse(String accessToken, String refreshToken) {
    
}
