package io.muenchendigital.digiwf.integration.zms.gen;

import io.muenchendigital.digiwf.integration.zms.gen.auth.ApiKeyAuth;
import io.muenchendigital.digiwf.integration.zms.gen.auth.Authentication;
import io.muenchendigital.digiwf.integration.zms.gen.auth.HttpBasicAuth;
import io.muenchendigital.digiwf.integration.zms.gen.auth.OAuth;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.http.RequestEntity.BodyBuilder;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.*;
import java.util.Map.Entry;

@Component("io.muenchendigital.digiwf.schema.registry.gen.ApiClient")
public class ApiClient {
    public enum CollectionFormat {
        CSV(","), TSV("\t"), SSV(" "), PIPES("|"), MULTI(null);

        private final String separator;

        private CollectionFormat(final String separator) {
            this.separator = separator;
        }

        private String collectionToString(final Collection<? extends CharSequence> collection) {
            return StringUtils.collectionToDelimitedString(collection, this.separator);
        }
    }

    private boolean debugging = false;

    private final HttpHeaders defaultHeaders = new HttpHeaders();

    private String basePath = "http://localhost:8090";

    private final RestTemplate restTemplate;

    private Map<String, Authentication> authentications;

    private DateFormat dateFormat;

    public ApiClient() {
        this.restTemplate = this.buildRestTemplate();
        this.init();
    }

    @Autowired
    public ApiClient(final RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        this.init();
    }

    protected void init() {
        // Use RFC3339 format for date and datetime.
        // See http://xml2rfc.ietf.org/public/rfc/html/rfc3339.html#anchor14
        this.dateFormat = new RFC3339DateFormat();

        // Use UTC as the default time zone.
        this.dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        // Set default User-Agent.
        this.setUserAgent("Java-SDK");

        // Setup authentications (key: authentication name, value: authentication).
        this.authentications = new HashMap<String, Authentication>();
        // Prevent the authentications from being modified.
        this.authentications = Collections.unmodifiableMap(this.authentications);
    }

    /**
     * Get the current base path
     *
     * @return String the base path
     */
    public String getBasePath() {
        return this.basePath;
    }

    /**
     * Set the base path, which should include the host
     *
     * @param basePath the base path
     * @return ApiClient this client
     */
    public ApiClient setBasePath(final String basePath) {
        this.basePath = basePath;
        return this;
    }

    /**
     * Get authentications (key: authentication name, value: authentication).
     *
     * @return Map the currently configured authentication types
     */
    public Map<String, Authentication> getAuthentications() {
        return this.authentications;
    }

    /**
     * Get authentication for the given name.
     *
     * @param authName The authentication name
     * @return The authentication, null if not found
     */
    public Authentication getAuthentication(final String authName) {
        return this.authentications.get(authName);
    }

    /**
     * Helper method to set username for the first HTTP basic authentication.
     *
     * @param username the username
     */
    public void setUsername(final String username) {
        for (final Authentication auth : this.authentications.values()) {
            if (auth instanceof HttpBasicAuth) {
                ((HttpBasicAuth) auth).setUsername(username);
                return;
            }
        }
        throw new RuntimeException("No HTTP basic authentication configured!");
    }

    /**
     * Helper method to set password for the first HTTP basic authentication.
     *
     * @param password the password
     */
    public void setPassword(final String password) {
        for (final Authentication auth : this.authentications.values()) {
            if (auth instanceof HttpBasicAuth) {
                ((HttpBasicAuth) auth).setPassword(password);
                return;
            }
        }
        throw new RuntimeException("No HTTP basic authentication configured!");
    }

    /**
     * Helper method to set API key value for the first API key authentication.
     *
     * @param apiKey the API key
     */
    public void setApiKey(final String apiKey) {
        for (final Authentication auth : this.authentications.values()) {
            if (auth instanceof ApiKeyAuth) {
                ((ApiKeyAuth) auth).setApiKey(apiKey);
                return;
            }
        }
        throw new RuntimeException("No API key authentication configured!");
    }

    /**
     * Helper method to set API key prefix for the first API key authentication.
     *
     * @param apiKeyPrefix the API key prefix
     */
    public void setApiKeyPrefix(final String apiKeyPrefix) {
        for (final Authentication auth : this.authentications.values()) {
            if (auth instanceof ApiKeyAuth) {
                ((ApiKeyAuth) auth).setApiKeyPrefix(apiKeyPrefix);
                return;
            }
        }
        throw new RuntimeException("No API key authentication configured!");
    }

    /**
     * Helper method to set access token for the first OAuth2 authentication.
     *
     * @param accessToken the access token
     */
    public void setAccessToken(final String accessToken) {
        for (final Authentication auth : this.authentications.values()) {
            if (auth instanceof OAuth) {
                ((OAuth) auth).setAccessToken(accessToken);
                return;
            }
        }
        throw new RuntimeException("No OAuth2 authentication configured!");
    }

    /**
     * Set the User-Agent header's value (by adding to the default header map).
     *
     * @param userAgent the user agent string
     * @return ApiClient this client
     */
    public ApiClient setUserAgent(final String userAgent) {
        this.addDefaultHeader("User-Agent", userAgent);
        return this;
    }

    /**
     * Add a default header.
     *
     * @param name  The header's name
     * @param value The header's value
     * @return ApiClient this client
     */
    public ApiClient addDefaultHeader(final String name, final String value) {
        this.defaultHeaders.add(name, value);
        return this;
    }

    public void setDebugging(final boolean debugging) {
        List<ClientHttpRequestInterceptor> currentInterceptors = this.restTemplate.getInterceptors();
        if (debugging) {
            if (currentInterceptors == null) {
                currentInterceptors = new ArrayList<ClientHttpRequestInterceptor>();
            }
            final ClientHttpRequestInterceptor interceptor = new ApiClientHttpRequestInterceptor();
            currentInterceptors.add(interceptor);
            this.restTemplate.setInterceptors(currentInterceptors);
        } else {
            if (currentInterceptors != null && !currentInterceptors.isEmpty()) {
                final Iterator<ClientHttpRequestInterceptor> iter = currentInterceptors.iterator();
                while (iter.hasNext()) {
                    final ClientHttpRequestInterceptor interceptor = iter.next();
                    if (interceptor instanceof ApiClientHttpRequestInterceptor) {
                        iter.remove();
                    }
                }
                this.restTemplate.setInterceptors(currentInterceptors);
            }
        }
        this.debugging = debugging;
    }

    /**
     * Check that whether debugging is enabled for this API client.
     *
     * @return boolean true if this client is enabled for debugging, false otherwise
     */
    public boolean isDebugging() {
        return this.debugging;
    }

    /**
     * Get the date format used to parse/format date parameters.
     *
     * @return DateFormat format
     */
    public DateFormat getDateFormat() {
        return this.dateFormat;
    }

    /**
     * Set the date format used to parse/format date parameters.
     *
     * @param dateFormat Date format
     * @return API client
     */
    public ApiClient setDateFormat(final DateFormat dateFormat) {
        this.dateFormat = dateFormat;
        return this;
    }

    /**
     * Parse the given string into Date object.
     */
    public Date parseDate(final String str) {
        try {
            return this.dateFormat.parse(str);
        } catch (final ParseException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Format the given Date object into string.
     */
    public String formatDate(final Date date) {
        return this.dateFormat.format(date);
    }

    /**
     * Format the given parameter object into string.
     *
     * @param param the object to convert
     * @return String the parameter represented as a String
     */
    public String parameterToString(final Object param) {
        if (param == null) {
            return "";
        } else if (param instanceof Date) {
            return this.formatDate((Date) param);
        } else if (param instanceof Collection) {
            final StringBuilder b = new StringBuilder();
            for (final Object o : (Collection<?>) param) {
                if (b.length() > 0) {
                    b.append(",");
                }
                b.append(String.valueOf(o));
            }
            return b.toString();
        } else {
            return String.valueOf(param);
        }
    }

    /**
     * Converts a parameter to a {@link MultiValueMap} for use in REST requests
     *
     * @param collectionFormat The format to convert to
     * @param name             The name of the parameter
     * @param value            The parameter's value
     * @return a Map containing the String value(s) of the input parameter
     */
    public MultiValueMap<String, String> parameterToMultiValueMap(CollectionFormat collectionFormat, final String name, final Object value) {
        final MultiValueMap<String, String> params = new LinkedMultiValueMap<String, String>();

        if (name == null || name.isEmpty() || value == null) {
            return params;
        }

        if (collectionFormat == null) {
            collectionFormat = CollectionFormat.CSV;
        }

        Collection<?> valueCollection = null;
        if (value instanceof Collection) {
            valueCollection = (Collection<?>) value;
        } else {
            params.add(name, this.parameterToString(value));
            return params;
        }

        if (valueCollection.isEmpty()) {
            return params;
        }

        if (collectionFormat.equals(CollectionFormat.MULTI)) {
            for (final Object item : valueCollection) {
                params.add(name, this.parameterToString(item));
            }
            return params;
        }

        final List<String> values = new ArrayList<String>();
        for (final Object o : valueCollection) {
            values.add(this.parameterToString(o));
        }
        params.add(name, collectionFormat.collectionToString(values));

        return params;
    }

    /**
     * Check if the given {@code String} is a JSON MIME.
     *
     * @param mediaType the input MediaType
     * @return boolean true if the MediaType represents JSON, false otherwise
     */
    public boolean isJsonMime(final String mediaType) {
        // "* / *" is default to JSON
        if ("*/*".equals(mediaType)) {
            return true;
        }

        try {
            return this.isJsonMime(MediaType.parseMediaType(mediaType));
        } catch (final InvalidMediaTypeException e) {
        }
        return false;
    }

    /**
     * Check if the given MIME is a JSON MIME.
     * JSON MIME examples:
     * application/json
     * application/json; charset=UTF8
     * APPLICATION/JSON
     *
     * @param mediaType the input MediaType
     * @return boolean true if the MediaType represents JSON, false otherwise
     */
    public boolean isJsonMime(final MediaType mediaType) {
        return mediaType != null && (MediaType.APPLICATION_JSON.isCompatibleWith(mediaType) || mediaType.getSubtype().matches("^.*\\+json[;]?\\s*$"));
    }

    /**
     * Select the Accept header's value from the given accepts array:
     * if JSON exists in the given array, use it;
     * otherwise use all of them (joining into a string)
     *
     * @param accepts The accepts array to select from
     * @return List The list of MediaTypes to use for the Accept header
     */
    public List<MediaType> selectHeaderAccept(final String[] accepts) {
        if (accepts.length == 0) {
            return null;
        }
        for (final String accept : accepts) {
            final MediaType mediaType = MediaType.parseMediaType(accept);
            if (this.isJsonMime(mediaType)) {
                return Collections.singletonList(mediaType);
            }
        }
        return MediaType.parseMediaTypes(StringUtils.arrayToCommaDelimitedString(accepts));
    }

    /**
     * Select the Content-Type header's value from the given array:
     * if JSON exists in the given array, use it;
     * otherwise use the first one of the array.
     *
     * @param contentTypes The Content-Type array to select from
     * @return MediaType The Content-Type header to use. If the given array is empty, JSON will be used.
     */
    public MediaType selectHeaderContentType(final String[] contentTypes) {
        if (contentTypes.length == 0) {
            return MediaType.APPLICATION_JSON;
        }
        for (final String contentType : contentTypes) {
            final MediaType mediaType = MediaType.parseMediaType(contentType);
            if (this.isJsonMime(mediaType)) {
                return mediaType;
            }
        }
        return MediaType.parseMediaType(contentTypes[0]);
    }

    /**
     * Select the body to use for the request
     *
     * @param obj         the body object
     * @param formParams  the form parameters
     * @param contentType the content type of the request
     * @return Object the selected body
     */
    protected Object selectBody(final Object obj, final MultiValueMap<String, Object> formParams, final MediaType contentType) {
        final boolean isForm = MediaType.MULTIPART_FORM_DATA.isCompatibleWith(contentType) || MediaType.APPLICATION_FORM_URLENCODED.isCompatibleWith(contentType);
        return isForm ? formParams : obj;
    }

    /**
     * Invoke API by sending HTTP request with the given options.
     *
     * @param <T>          the return type to use
     * @param path         The sub-path of the HTTP URL
     * @param method       The request method
     * @param queryParams  The query parameters
     * @param body         The request body object
     * @param headerParams The header parameters
     * @param formParams   The form parameters
     * @param accept       The request's Accept header
     * @param contentType  The request's Content-Type header
     * @param authNames    The authentications to apply
     * @param returnType   The return type into which to deserialize the response
     * @return ResponseEntity&lt;T&gt; The response of the chosen type
     */
    public <T> ResponseEntity<T> invokeAPI(final String path, final HttpMethod method, final MultiValueMap<String, String> queryParams, final Object body, final HttpHeaders headerParams, final MultiValueMap<String, Object> formParams, final List<MediaType> accept, final MediaType contentType, final String[] authNames, final ParameterizedTypeReference<T> returnType) throws RestClientException {
        this.updateParamsForAuth(authNames, queryParams, headerParams);

        final UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(this.basePath).path(path);
        if (queryParams != null) {
            builder.queryParams(queryParams);
        }

        final BodyBuilder requestBuilder = RequestEntity.method(method, builder.build().toUri());
        if (accept != null) {
            requestBuilder.accept(accept.toArray(new MediaType[accept.size()]));
        }
        if (contentType != null) {
            requestBuilder.contentType(contentType);
        }

        this.addHeadersToRequest(headerParams, requestBuilder);
        this.addHeadersToRequest(this.defaultHeaders, requestBuilder);

        final RequestEntity<Object> requestEntity = requestBuilder.body(this.selectBody(body, formParams, contentType));

        final ResponseEntity<T> responseEntity = this.restTemplate.exchange(requestEntity, returnType);

        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            return responseEntity;
        } else {
            // The error handler built into the RestTemplate should handle 400 and 500 series errors.
            throw new RestClientException("API returned " + responseEntity.getStatusCode() + " and it wasn't handled by the RestTemplate error handler");
        }
    }

    /**
     * Add headers to the request that is being built
     *
     * @param headers        The headers to add
     * @param requestBuilder The current request
     */
    protected void addHeadersToRequest(final HttpHeaders headers, final BodyBuilder requestBuilder) {
        for (final Entry<String, List<String>> entry : headers.entrySet()) {
            final List<String> values = entry.getValue();
            for (final String value : values) {
                if (value != null) {
                    requestBuilder.header(entry.getKey(), value);
                }
            }
        }
    }

    /**
     * Build the RestTemplate used to make HTTP requests.
     *
     * @return RestTemplate
     */
    protected RestTemplate buildRestTemplate() {
        final RestTemplate restTemplate = new RestTemplate();
        // This allows us to read the response more than once - Necessary for debugging.
        restTemplate.setRequestFactory(new BufferingClientHttpRequestFactory(restTemplate.getRequestFactory()));
        return restTemplate;
    }

    /**
     * Update query and header parameters based on authentication settings.
     *
     * @param authNames    The authentications to apply
     * @param queryParams  The query parameters
     * @param headerParams The header parameters
     */
    private void updateParamsForAuth(final String[] authNames, final MultiValueMap<String, String> queryParams, final HttpHeaders headerParams) {
        for (final String authName : authNames) {
            final Authentication auth = this.authentications.get(authName);
            if (auth == null) {
                throw new RestClientException("Authentication undefined: " + authName);
            }
            auth.applyToParams(queryParams, headerParams);
        }
    }

    private class ApiClientHttpRequestInterceptor implements ClientHttpRequestInterceptor {
        private final Log log = LogFactory.getLog(ApiClientHttpRequestInterceptor.class);

        @Override
        public ClientHttpResponse intercept(final HttpRequest request, final byte[] body, final ClientHttpRequestExecution execution) throws IOException {
            this.logRequest(request, body);
            final ClientHttpResponse response = execution.execute(request, body);
            this.logResponse(response);
            return response;
        }

        private void logRequest(final HttpRequest request, final byte[] body) throws UnsupportedEncodingException {
            this.log.info("URI: " + request.getURI());
            this.log.info("HTTP Method: " + request.getMethod());
            this.log.info("HTTP Headers: " + this.headersToString(request.getHeaders()));
            this.log.info("Request Body: " + new String(body, StandardCharsets.UTF_8));
        }

        private void logResponse(final ClientHttpResponse response) throws IOException {
            this.log.info("HTTP Status Code: " + response.getRawStatusCode());
            this.log.info("Status Text: " + response.getStatusText());
            this.log.info("HTTP Headers: " + this.headersToString(response.getHeaders()));
            this.log.info("Response Body: " + this.bodyToString(response.getBody()));
        }

        private String headersToString(final HttpHeaders headers) {
            final StringBuilder builder = new StringBuilder();
            for (final Entry<String, List<String>> entry : headers.entrySet()) {
                builder.append(entry.getKey()).append("=[");
                for (final String value : entry.getValue()) {
                    builder.append(value).append(",");
                }
                builder.setLength(builder.length() - 1); // Get rid of trailing comma
                builder.append("],");
            }
            builder.setLength(builder.length() - 1); // Get rid of trailing comma
            return builder.toString();
        }

        private String bodyToString(final InputStream body) throws IOException {
            final StringBuilder builder = new StringBuilder();
            final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(body, StandardCharsets.UTF_8));
            String line = bufferedReader.readLine();
            while (line != null) {
                builder.append(line).append(System.lineSeparator());
                line = bufferedReader.readLine();
            }
            bufferedReader.close();
            return builder.toString();
        }
    }
}
