package mate.academy.springboot.rickandmortyapp.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class HttpClient {
    private final CloseableHttpClient httpClient;
    private ObjectMapper objectMapper = new ObjectMapper();

    public <T> T get(String url,Class<T> clazz) {
        HttpGet request = new HttpGet(url);
        try (CloseableHttpResponse response = httpClient.execute(request)) {
            return objectMapper.readValue(response.getEntity().getContent(), clazz);
        } catch (IOException e) {
            throw new RuntimeException("Can't fetch info from URL: " + url, e);
        }
    }
}
