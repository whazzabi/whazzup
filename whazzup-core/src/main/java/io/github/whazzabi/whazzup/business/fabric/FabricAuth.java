package io.github.whazzabi.whazzup.business.fabric;

import org.springframework.http.HttpHeaders;

public class FabricAuth {

    private final HttpHeaders httpHeaders;

    public FabricAuth(HttpHeaders httpHeaders) {
        this.httpHeaders = httpHeaders;
    }

    public HttpHeaders getHeaders() {
        return httpHeaders;
    }
}
