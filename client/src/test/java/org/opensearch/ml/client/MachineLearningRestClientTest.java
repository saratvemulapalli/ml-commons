package org.opensearch.ml.client;

import org.apache.http.HttpEntity;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.opensearch.action.delete.DeleteResponse;
import org.opensearch.client.Request;
import org.opensearch.client.Response;
import org.opensearch.client.RestClient;
import org.opensearch.core.action.ActionListener;
import org.opensearch.ml.common.input.MLInput;
import org.opensearch.ml.common.output.MLOutput;
import org.apache.http.HttpHost;

import java.io.IOException;

public class MachineLearningRestClientTest {
    private static final Logger logger = LogManager.getLogger(MachineLearningRestClientTest.class);
    private RestClient restClient;
    private RestClient createClient() {
        final HttpHost host = new HttpHost("localhost", 9200);
        return RestClient.builder(host).build();
    }

    @Before
    public void setUp() throws Exception {
        restClient = createClient();
    }

    @After
    public void tearDown() throws IOException {
        restClient.close();
    }
    @Test
    public void testRestClient() throws IOException {
    MachineLearningRestClient client = new MachineLearningRestClient(restClient);
    ActionListener listener = new ActionListener<DeleteResponse>() {
            @Override
            public void onResponse(DeleteResponse deleteResponse) {
                System.out.println("deleted agent successfully");
                logger.info("delete agent successfully");
            }

            @Override
            public void onFailure(Exception e) {
                System.out.println("delete agent failed" + e);
                logger.info("delete agent failed {}", e);
            }
        };
    client.deleteAgent("123", listener);
    ActionListener mlOutputListener = new ActionListener<Response>() {
            @Override
            public void onResponse(Response mlOutput) {
                System.out.println("predict successful " + mlOutput);
                HttpEntity entity =mlOutput.getEntity();
                try {
                    String responseBody = EntityUtils.toString(entity);
                    System.out.println(responseBody);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void onFailure(Exception e) {
                System.out.println("predict failed" + e);
                logger.info("predict failed {}", e);
            }
        };
    Request request = new Request("GET", "/_cat/indices");
    String json = "{\n" +
            "    \"text_docs\": [ \"today is sunny\" ],\n" +
            "    \"return_number\": true,\n" +
            "    \"target_response\": [ \"sentence_embedding\" ]\n" +
            "}'";
    client.predict("eHL5cZAB9HiEw2PaFJ2B", json, mlOutputListener);
    }
}
