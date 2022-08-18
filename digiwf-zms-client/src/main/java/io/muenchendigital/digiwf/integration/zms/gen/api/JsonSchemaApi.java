package io.muenchendigital.digiwf.integration.zms.gen.api;

import io.muenchendigital.digiwf.integration.zms.gen.ApiClient;
import io.muenchendigital.digiwf.integration.zms.gen.model.JsonSchemaDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component("io.muenchendigital.digiwf.schema.registry.gen.api.JsonSchemaApi")
public class JsonSchemaApi {
    private ApiClient apiClient;

    public JsonSchemaApi() {
        this(new ApiClient());
    }

    @Autowired
    public JsonSchemaApi(final ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    public ApiClient getApiClient() {
        return this.apiClient;
    }

    public void setApiClient(final ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    /**
     * create a new json schema
     * <p><b>200</b> - OK
     *
     * @param body (required)
     * @return JsonSchemaDto
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public JsonSchemaDto createJsonSchema(final JsonSchemaDto body) throws RestClientException {
        return this.createJsonSchemaWithHttpInfo(body).getBody();
    }

    /**
     * create a new json schema
     * <p><b>200</b> - OK
     *
     * @param body (required)
     * @return ResponseEntity&lt;JsonSchemaDto&gt;
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public ResponseEntity<JsonSchemaDto> createJsonSchemaWithHttpInfo(final JsonSchemaDto body) throws RestClientException {
        final Object postBody = body;
        // verify the required parameter 'body' is set
        if (body == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'body' when calling createJsonSchema");
        }
        final String path = UriComponentsBuilder.fromPath("/jsonschema").build().toUriString();

        final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<String, String>();
        final HttpHeaders headerParams = new HttpHeaders();
        final MultiValueMap<String, Object> formParams = new LinkedMultiValueMap<String, Object>();

        final String[] accepts = {
                "*/*"
        };
        final List<MediaType> accept = this.apiClient.selectHeaderAccept(accepts);
        final String[] contentTypes = {
                "application/json"
        };
        final MediaType contentType = this.apiClient.selectHeaderContentType(contentTypes);

        final String[] authNames = new String[]{};

        final ParameterizedTypeReference<JsonSchemaDto> returnType = new ParameterizedTypeReference<JsonSchemaDto>() {
        };
        return this.apiClient.invokeAPI(path, HttpMethod.POST, queryParams, postBody, headerParams, formParams, accept, contentType, authNames, returnType);
    }

    /**
     * get json schema by key
     * <p><b>200</b> - OK
     *
     * @param key (required)
     * @return JsonSchemaDto
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public JsonSchemaDto getJsonSchema(final String key) throws RestClientException {
        return this.getJsonSchemaWithHttpInfo(key).getBody();
    }

    /**
     * get json schema by key
     * <p><b>200</b> - OK
     *
     * @param key (required)
     * @return ResponseEntity&lt;JsonSchemaDto&gt;
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public ResponseEntity<JsonSchemaDto> getJsonSchemaWithHttpInfo(final String key) throws RestClientException {
        final Object postBody = null;
        // verify the required parameter 'key' is set
        if (key == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'key' when calling getJsonSchema");
        }
        // create path and map variables
        final Map<String, Object> uriVariables = new HashMap<String, Object>();
        uriVariables.put("key", key);
        final String path = UriComponentsBuilder.fromPath("/jsonschema/{key}").buildAndExpand(uriVariables).toUriString();

        final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<String, String>();
        final HttpHeaders headerParams = new HttpHeaders();
        final MultiValueMap<String, Object> formParams = new LinkedMultiValueMap<String, Object>();

        final String[] accepts = {
                "*/*"
        };
        final List<MediaType> accept = this.apiClient.selectHeaderAccept(accepts);
        final String[] contentTypes = {};
        final MediaType contentType = this.apiClient.selectHeaderContentType(contentTypes);

        final String[] authNames = new String[]{};

        final ParameterizedTypeReference<JsonSchemaDto> returnType = new ParameterizedTypeReference<JsonSchemaDto>() {
        };
        return this.apiClient.invokeAPI(path, HttpMethod.GET, queryParams, postBody, headerParams, formParams, accept, contentType, authNames, returnType);
    }
}
