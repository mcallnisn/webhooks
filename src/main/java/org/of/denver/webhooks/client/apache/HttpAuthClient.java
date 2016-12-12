package org.of.denver.webhooks.client.apache;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.of.denver.webhooks.client.WebhookClient;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.Base64;
import java.util.Map;

/**
 * Created by b88maw on 12/12/2016.
 */
public class HttpAuthClient implements WebhookClient {

    public String post(final String urlString, final Map<String, String> parameters) {
        try {
            URI uri = URI.create(buildUrlWithQueryParameters(urlString, parameters));
            HttpHost host = new HttpHost(uri.getHost(), uri.getPort(), uri.getScheme());
            CredentialsProvider credsProvider = new BasicCredentialsProvider();
            // Create AuthCache instance
            AuthCache authCache = new BasicAuthCache();
            // Generate BASIC scheme object and add it to the local auth cache
            BasicScheme basicAuth = new BasicScheme();
            authCache.put(host, basicAuth);
            CloseableHttpClient httpClient = HttpClients.custom().setDefaultCredentialsProvider(credsProvider).build();
            HttpPost httpPost = new HttpPost(uri);
            // Add AuthCache to the execution context
            HttpClientContext localContext = HttpClientContext.create();
            localContext.setAuthCache(authCache);

            HttpResponse response = httpClient.execute(host, httpPost, localContext);
            return EntityUtils.toString(response.getEntity());
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private String buildUrlWithQueryParameters(final String urlString, final Map<String, String> parameters) {
        StringBuffer urlWithParamters = new StringBuffer(urlString);
        urlWithParamters.append("?");
        for (Map.Entry<String, String> parameter: parameters.entrySet()) {
            urlWithParamters.append(parameter.getKey());
            urlWithParamters.append("=");
            urlWithParamters.append(parameter.getValue());
            urlWithParamters.append("&");
        }
        return urlWithParamters.toString();
    }
}
