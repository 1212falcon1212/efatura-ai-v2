package com.efaturaai.soap;

import static org.junit.jupiter.api.Assertions.*;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class PingClientTest {

  private static WireMockServer server;

  @BeforeAll
  static void setup() {
    server = new WireMockServer(WireMockConfiguration.wireMockConfig().dynamicPort());
    server.start();
    WireMock.configureFor("localhost", server.port());

    String response =
        "<PingResponse xmlns=\"urn:efaturaai:ping\"><message>OK</message></PingResponse>";
    server.stubFor(
        WireMock.post(WireMock.urlPathEqualTo("/ws"))
            .willReturn(
                WireMock.aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "text/xml")
                    .withBody(response)));
  }

  @AfterAll
  static void teardown() {
    server.stop();
  }

  @Test
  void ping_should_return_response() {
    PingClient client = new PingClient();
    String result = client.ping("http://localhost:" + server.port() + "/ws", "HELLO");
    assertNotNull(result);
  }
}
