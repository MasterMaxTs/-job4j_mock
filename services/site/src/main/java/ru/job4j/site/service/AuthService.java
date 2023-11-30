package ru.job4j.site.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.job4j.site.dto.UserInfoDTO;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    @Value("${security.oauth2.resource.userInfoUri}")
    private String oauth2Url;
    @Value("${security.oauth2.tokenUri}")
    private String oauth2Token;
    @Value("${server.auth.ping}")
    private String authServicePing;

    private final RestAuthCall restAuthCall;

    public UserInfoDTO userInfo(String token) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        restAuthCall.setUrl("http://localhost:9900/person/current");
        return mapper.readValue(restAuthCall.get(token), UserInfoDTO.class);
    }

    public String token(Map<String, String> params) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        String result = "";
        restAuthCall.setUrl(oauth2Token);
        try {
            result = mapper.readTree(
                        restAuthCall.token(params)
                    ).get("access_token").asText();
        } catch (Exception e) {
            log.error("Get token from service Auth error: {}", e.getMessage());
        }
        return result;
    }

    /**
     * Метод проверяет доступность сервера Auth.
     *
     * @return String body
     */
    public boolean getPing() {
        var result = false;
        restAuthCall.setUrl(authServicePing);
        try {
            result = !restAuthCall.get().isEmpty();
        } catch (Exception e) {
            log.error("Get PING from API Auth error: {}", e.getMessage());
        }
        return result;
    }
}
