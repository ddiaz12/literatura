package com.alura.literatura.service;

import com.alura.literatura.model.Author;
import com.alura.literatura.model.Book;
import com.alura.literatura.model.GutendexResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
public class GutendexClient {

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public GutendexClient() {
        this.httpClient = HttpClient.newBuilder().build();
        this.objectMapper = new ObjectMapper();
    }

    public Book searchBookByTitle(String title) throws IOException, InterruptedException {
        String url = "https://gutendex.com/books/?search=" + title.replace(" ", "%20");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.body().isEmpty()) {
            // Manejar el caso de respuesta vacía, por ejemplo lanzar una excepción
            throw new IOException("La respuesta de la API de Gutendex está vacía");
        }
        GutendexResponse gutendexResponse = objectMapper.readValue(response.body(), GutendexResponse.class);
        if (gutendexResponse.getResults().length > 0) {
            Book book = gutendexResponse.getResults()[0];
            if (book.getLanguages() != null && book.getLanguages().length > 0) {
                book.setLanguage(book.getLanguages()[0]);
            }
            return book;
        } else {
            return null;
        }
    }


}
