package com.example.api.beanConfigs;

import com.coze.openapi.service.auth.TokenAuth;
import com.coze.openapi.service.service.CozeAPI;
import okhttp3.OkHttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CozeAPIClientConfig {

    @Bean
    public CozeAPI cozeAPI() {
        String cozeAPIToken = "pat_BaMRgCo8kwVkgBkZOxOtSfMk7sWL4aUfdeb3UEoKbKH04X6gDoDsFGAUocnnfwxX";
        String baseUrl = "https://api.coze.com";
        TokenAuth cozeTokenAuthObj = new TokenAuth(cozeAPIToken);
        return new CozeAPI.Builder().client(new OkHttpClient()).auth(cozeTokenAuthObj).baseURL(baseUrl).build();
    }

}
