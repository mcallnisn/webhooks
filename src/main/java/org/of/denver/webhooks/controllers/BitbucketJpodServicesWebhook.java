package org.of.denver.webhooks.controllers;

import org.of.denver.webhooks.client.WebhookClient;
import org.of.denver.webhooks.client.apache.HttpAuthClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by b88maw on 12/12/2016.
 */
@Path(value = "/push")
public class BitbucketJpodServicesWebhook {

    private static final Logger LOGGER = LoggerFactory.getLogger(BitbucketJpodServicesWebhook.class);


    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response pullRequestCreated(final InputStream request, @QueryParam("buildUrl") String buildUrl) {

        JsonReader jsonReader = Json.createReader(request);
        JsonObject rootJsonObject = jsonReader.readObject();
        jsonReader.close();

        LOGGER.info(rootJsonObject.toString());
        JsonObject repository = rootJsonObject.getJsonObject("repository");
        JsonObject project = repository.getJsonObject("project");

        final String repositoryUrl = "ssh://mustache:7999/" + project.getString("key").toLowerCase() + "/" + repository.getString("name") + "." + repository.getString("scmId");
        LOGGER.info(repositoryUrl);

        final Set<String> branches = new HashSet<>();
        for (JsonObject refChanges : rootJsonObject.getJsonArray("refChanges").toArray(new JsonObject[]{})) {
            final String branchName = refChanges.getString("refId"); // branch name
            final String refChangesType = refChanges.getString("type"); // ADD/UPDATE/DELETE
            if (!refChangesType.equalsIgnoreCase("DELETE")) {
                branches.add(branchName);
            } else {
                LOGGER.info("Branch " + branchName + " was deleted with " + refChanges.getString("toHash"));
                branches.remove(branchName);
            }
        }
        for (String branchName : branches) {
            invokeBuild(repositoryUrl, branchName, buildUrl);
        }

        return Response.ok().build();
    }


    private static final String REPOSITORY_URL_PARAM = "repositoryURL";
    private static final String BRANCH_SPECIFIER_PARAM = "branchSpecifier";
    private static final String TOKEN_PARAM = "token";
    private static final String CAUSE_PARAM = "cause";

    private void invokeBuild(final String repository, final String branchName, final String buildUrl) {
        LOGGER.info("invokeBuild " + repository + " " + branchName + " " + buildUrl);
        Map<String, String> parameters = new HashMap<>();
        parameters.put(REPOSITORY_URL_PARAM, repository);
        parameters.put(BRANCH_SPECIFIER_PARAM, branchName);
        parameters.put(TOKEN_PARAM, "1031980531");
        parameters.put(CAUSE_PARAM, "Automated build based on SCM changes");
        WebhookClient client = new HttpAuthClient();
        client.post(buildUrl, parameters);
    }
}
