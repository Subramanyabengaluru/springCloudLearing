package com.example.commentservice.client;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.HttpClientErrorException;

/**
 * Talks to post-service over HTTP. This is the inter-service communication:
 * comment-service does not share a database with post-service, so it must ask
 * post-service whether a given post exists before creating a comment.
 */
@Component
public class PostClient {

    private final RestClient postServiceRestClient;

    public PostClient(RestClient postServiceRestClient) {
        this.postServiceRestClient = postServiceRestClient;
    }

    public boolean postExists(Long postId) {
        try {
            postServiceRestClient.get()
                    .uri("/api/posts/{id}/exists", postId)
                    .retrieve()
                    .toBodilessEntity();
            return true;
        } catch (HttpClientErrorException.NotFound e) {
            return false;
        }
    }
}
