package org.of.denver.webhooks.client;

import java.io.IOException;
import java.util.Map;

/**
 * Created by b88maw on 12/12/2016.
 */
public interface WebhookClient {

    String post(final String urlString, final Map<String, String> parameters);

}
