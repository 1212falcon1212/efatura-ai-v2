package com.efaturaai.soap;

import org.springframework.stereotype.Component;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.xml.transform.StringSource;

@Component
public class PingClient {
  private final WebServiceTemplate template = new WebServiceTemplate();

  public String ping(String uri, String message) {
    String request =
        "<PingRequest xmlns=\"urn:efaturaai:ping\"><message>"
            + message
            + "</message></PingRequest>";
    StringSource source = new StringSource(request);
    Object resp =
        template.sendSourceAndReceive(uri, source, message1 -> {}, (response) -> response);
    return resp.toString();
  }
}
