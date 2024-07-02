package org.opensearch.ml.client;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opensearch.action.delete.DeleteResponse;
import org.opensearch.action.search.SearchRequest;
import org.opensearch.action.search.SearchResponse;
import org.opensearch.client.Request;
import org.opensearch.client.Response;
import org.opensearch.client.RestClient;
import org.opensearch.core.action.ActionListener;
import org.opensearch.ml.common.input.MLInput;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class MachineLearningRestClient {
    private static final Logger logger = LogManager.getLogger(MachineLearningRestClient.class);
    private final RestClient client;
    public void predict(String modelId, String jsonBody, ActionListener<Response> listener) {
        logger.info("predict is called");
        Request predictRequest = new Request("POST", "/_plugins/_ml/models/" + modelId + "/_predict");
        predictRequest.setJsonEntity(jsonBody);
        try {
            Response response = client.performRequest(predictRequest);
            listener.onResponse(response);
        } catch(Exception e) {
            logger.error("Failed to predict", e);
            listener.onFailure(e);
        }
    }

    public void deleteAgent(String agentId, ActionListener<DeleteResponse> listener) {
        unsupportedMethod(listener);
    }

    private void unsupportedMethod(ActionListener listener) {
        listener.onFailure(new UnsupportedOperationException("This method is not supported"));
    }
}
