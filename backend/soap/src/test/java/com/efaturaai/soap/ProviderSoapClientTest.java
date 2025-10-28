package com.efaturaai.soap;

import static org.junit.jupiter.api.Assertions.*;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class ProviderSoapClientTest {

  private static WireMockServer server;

  @BeforeAll
  static void setup() {
    server = new WireMockServer(WireMockConfiguration.wireMockConfig().dynamicPort());
    server.start();
    WireMock.configureFor("localhost", server.port());

    server.stubFor(
        WireMock.post(WireMock.urlPathEqualTo("/ws"))
            .willReturn(
                WireMock.aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "text/xml")
                    .withBody("<Response>OK</Response>")));
  }

  @AfterAll
  static void teardown() {
    server.stop();
  }

  @Test
  void send_status_cancel_should_return_values() {
    ProviderSoapClient client = new ProviderSoapClient("http://localhost:" + server.port() + "/ws");
    var send = client.sendInvoice("<Invoice/>");
    assertEquals("SENT", send.getStatus());

    var status = client.getInvoiceStatus(send.getInvoiceUuid());
    assertEquals("DELIVERED", status.getStatus());

    var cancel = client.cancelInvoice(send.getInvoiceUuid());
    assertEquals("CANCELLED", cancel.getStatus());
  }
}
