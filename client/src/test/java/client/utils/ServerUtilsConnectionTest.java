package client.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;


import org.junit.jupiter.api.Test;




import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.*;

@WireMockTest(httpPort = 8080)
public class ServerUtilsConnectionTest {

    @Test
    void connectSuccessTest() throws Exception {
        ServerUtils server = new ServerUtils();
        String serverAddress = "localhost:8080";
        stubFor(get("/api/boards/connection-available").willReturn(
            aResponse().withHeader("Content-Type", "application/json").withBody(new ObjectMapper().writeValueAsString(serverAddress))
        ));
        assertDoesNotThrow(() -> server.connect(serverAddress));
    }

    @Test
    void connectFailTest() throws Exception {
        ServerUtils server = new ServerUtils();
        String serverAddress = "wrongAddress";
        stubFor(get("/api/boards/connection-available").willReturn(
            aResponse().withHeader("Content-Type", "application/json").withBody(new ObjectMapper().writeValueAsString(serverAddress))
        ));
        assertThrows(ExceptionInInitializerError.class, () -> {
            server.connect(serverAddress);
        });
    }

}
