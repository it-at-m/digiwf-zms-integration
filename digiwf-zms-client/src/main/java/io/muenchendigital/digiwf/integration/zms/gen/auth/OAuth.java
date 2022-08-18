package io.muenchendigital.digiwf.integration.zms.gen.auth;

import org.springframework.http.HttpHeaders;
import org.springframework.util.MultiValueMap;

public class OAuth implements Authentication {
    private String accessToken;

    public String getAccessToken() {
        return this.accessToken;
    }

    public void setAccessToken(final String accessToken) {
        this.accessToken = accessToken;
    }

    @Override
    public void applyToParams(final MultiValueMap<String, String> queryParams, final HttpHeaders headerParams) {
        if (this.accessToken != null) {
            headerParams.add(HttpHeaders.AUTHORIZATION, "Bearer " + this.accessToken);
        }
    }
}
