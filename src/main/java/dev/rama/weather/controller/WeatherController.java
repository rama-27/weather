package dev.rama.weather.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

@RestController
@CrossOrigin(value = "*")
public class WeatherController {

    @Value("${openweathermap.api.key}")
    private String apiKey;

    @Value("${openweathermap.api.url}")
    private String apiUrl;

    private final OkHttpClient client = new OkHttpClient();

    @Cacheable(value = "weather", key = "#city")
    @GetMapping("/weather")
    public ResponseEntity<String> getWeather(@RequestParam String city) throws IOException {
        String url = apiUrl + "?q=" + city + "&appid=" + apiKey + "&units=metric";
        Request request = new Request.Builder().url(url).build();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }
//            System.out.println("Fetching weather data for city: " + city);
            return ResponseEntity.ok(response.body().string());
        }
    }

    @Cacheable(value = "weather", key = "#lat + #lon")
    @GetMapping("/weather/coords")
    public ResponseEntity<String> getWeatherByCoords(@RequestParam double lat, @RequestParam double lon) throws IOException {
        String url = apiUrl + "?lat=" + lat + "&lon=" + lon + "&appid=" + apiKey + "&units=metric";
        Request request = new Request.Builder().url(url).build();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }
            return ResponseEntity.ok(response.body().string());
        }
    }
}
