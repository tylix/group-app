package com.maximilianwiegmann.backend;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

import org.springframework.web.bind.annotation.RequestMethod;

import lombok.Builder;
import lombok.Getter;
import lombok.Builder.Default;

@Builder
@Getter
public class Request {
    private String url;
    private String body;
    private Map<String, String> headers;

    @Default
    private RequestMethod method = RequestMethod.POST;

    @Default
    private String contentType = "application/json";

    @Default
    private String accept = "application/json";


    public String sendRequest() throws IOException {
        URL url = new URL(this.url);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod(this.method.name());
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", this.contentType);
        connection.setRequestProperty("Accept", this.accept);

        if (headers != null)
            headers.forEach(connection::setRequestProperty);

        if (body != null)
            try (OutputStream outputStream = connection.getOutputStream()) {
                byte[] input = this.body.getBytes("utf-8");
                outputStream.write(input, 0, input.length);
            }

        StringBuilder response = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            String responseLine = null;
            while ((responseLine = reader.readLine()) != null) {
                response.append(responseLine.trim());
            }
        }

        connection.disconnect();
        return response.toString();
    }

    public enum RequestType {
        POST,
        GET,
        PUT,
        DELETE
    }
}
