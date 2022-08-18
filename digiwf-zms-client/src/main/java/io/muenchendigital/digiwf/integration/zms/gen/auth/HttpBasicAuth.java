package io.muenchendigital.digiwf.integration.zms.gen.auth;

import org.springframework.http.HttpHeaders;
import org.springframework.util.Base64Utils;
import org.springframework.util.MultiValueMap;

import java.nio.charset.StandardCharsets;

public class HttpBasicAuth implements Authentication {
    private String username;
    private String password;

    public String getUsername() {
        return this.username;
    }

    public void setUsername(final String username) {
        this.username = username;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(final String password) {
        this.password = password;
    }

    @Override
    public void applyToParams(final MultiValueMap<String, String> queryParams, final HttpHeaders headerParams) {
        if (this.username == null && this.password == null) {
            return;
        }
        final String str = (this.username == null ? "" : this.username) + ":" + (this.password == null ? "" : this.password);
        headerParams.add(HttpHeaders.AUTHORIZATION, "Basic " + Base64Utils.encodeToString(str.getBytes(StandardCharsets.UTF_8)));
    }
}
