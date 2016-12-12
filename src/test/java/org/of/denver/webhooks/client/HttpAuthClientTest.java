package org.of.denver.webhooks.client;

import org.junit.Ignore;
import org.junit.Test;
import org.of.denver.webhooks.client.apache.HttpAuthClient;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by michaelwilson on 12/9/16.
 */
public class HttpAuthClientTest {

    private final HttpAuthClient httpAuthClient = new HttpAuthClient();

    private static final String URL = "http://judcb10.judicial.local:8080/jenkins/job/COURT/job/JPOD-TEMP/job/jpodServices/buildWithParameters";
    private static final Map<String, String> parameters = new HashMap<>();
    private static final String REPOSITORY_URL_PARAM = "repositoryURL";
    private static final String BRANCH_SPECIFIER_PARAM = "branchSpecifier";
    private static final String TOKEN_PARAM = "token";

    @Test
    @Ignore
    public void test() throws Exception{
        parameters.put(REPOSITORY_URL_PARAM, "ssh://mustache:7999/jpod/jpodservices.git");
        parameters.put(BRANCH_SPECIFIER_PARAM, "develop");
        parameters.put(TOKEN_PARAM, "1031980531");
        System.out.print(httpAuthClient.post(URL, parameters));
    }

}