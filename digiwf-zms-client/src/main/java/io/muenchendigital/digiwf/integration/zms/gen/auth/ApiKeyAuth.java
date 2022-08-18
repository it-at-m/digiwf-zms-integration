package io.muenchendigital.digiwf.integration.zms.gen.auth;

import org.springframework.http.HttpHeaders;
import org.springframework.util.MultiValueMap;

public class ApiKeyAuth implements Authentication {
    private final String location;
    private final String paramName;

    private String apiKey;
    private String apiKeyPrefix;

    public ApiKeyAuth(final String location, final String paramName) {
        this.location = location;
        this.paramName = paramName;
    }

    public String getLocation() {
        return this.location;
    }

    public String getParamName() {
        return this.paramName;
    }

    public String getApiKey() {
        return this.apiKey;
    }

    public void setApiKey(final String apiKey) {
        this.apiKey = apiKey;
    }

    public String getApiKeyPrefix() {
        return this.apiKeyPrefix;
    }

    public void setApiKeyPrefix(final String apiKeyPrefix) {
        this.apiKeyPrefix = apiKeyPrefix;
    }

    @Override
    public void applyToParams(final MultiValueMap<String, String> queryParams, final HttpHeaders headerParams) {
        if (this.apiKey == null) {
            return;
        }
        final String value;
        if (this.apiKeyPrefix != null) {
            value = this.apiKeyPrefix + " " + this.apiKey;
        } else {
            value = this.apiKey;
        }
        if (this.location.equals("query")) {
            queryParams.add(this.paramName, value);
        } else if (this.location.equals("header")) {
            headerParams.add(this.paramName, value);
        }
    }
}
