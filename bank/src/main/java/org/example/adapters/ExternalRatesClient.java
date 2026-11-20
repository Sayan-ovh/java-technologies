package org.example.adapters;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ExternalRatesClient {
    private final HttpClient client =
            HttpClient.newHttpClient();
    private final ObjectMapper mapper =
            new ObjectMapper();

    public String get(String url)
            throws IOException,
            InterruptedException {
        HttpRequest request =
                HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .GET()
                        .build();
        HttpResponse<String> response =
                client.send(
                        request,
                        HttpResponse.BodyHandlers
                                .ofString()
                );
        if (response.statusCode() / 100 != 2) {
            throw new IllegalStateException(
                    "HTTP error: " +
                            response.statusCode()
            );
        }
        return response.body();
    }

    // ----------этап 9 - ответ http превращаем в json, а json в dto
    public RateDto getRate(String url)
            throws IOException,
            InterruptedException {
        String body = get(url);
        RateDto rate =
                mapper.readValue(
                        body,
                        RateDto.class
                );
        return rate;
    }
}
