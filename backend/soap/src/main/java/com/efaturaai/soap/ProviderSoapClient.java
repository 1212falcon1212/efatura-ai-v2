package com.efaturaai.soap;

import com.efaturaai.core.provider.EInvoiceProviderPort;
import com.efaturaai.core.provider.ProviderCancelResponse;
import com.efaturaai.core.provider.ProviderSendResponse;
import com.efaturaai.core.provider.ProviderStatusResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.xml.transform.StringSource;

@Component
public class ProviderSoapClient implements EInvoiceProviderPort {

  private final WebServiceTemplate template = new WebServiceTemplate();
  private final String endpoint;

  public ProviderSoapClient(
      @Value("${provider.soap.endpoint:http://localhost:8080/ws}") String endpoint) {
    this.endpoint = endpoint;
  }

  @Override
  public ProviderSendResponse sendInvoice(String ublXml) {
    String body =
        "<SendInvoiceRequest xmlns=\"urn:provider:invoice\"><UBL>"
            + escape(ublXml)
            + "</UBL></SendInvoiceRequest>";
    Object resp = template.sendSourceAndReceive(endpoint, new StringSource(body), m -> {}, r -> r);
    ProviderSendResponse pr = new ProviderSendResponse();
    pr.setInvoiceUuid("mock-uuid");
    pr.setStatus("SENT");
    pr.setMessage(resp.toString());
    return pr;
  }

  @Override
  public ProviderStatusResponse getInvoiceStatus(String invoiceUuid) {
    String body =
        "<GetStatusRequest xmlns=\"urn:provider:invoice\"><UUID>"
            + invoiceUuid
            + "</UUID></GetStatusRequest>";
    Object resp = template.sendSourceAndReceive(endpoint, new StringSource(body), m -> {}, r -> r);
    ProviderStatusResponse pr = new ProviderStatusResponse();
    pr.setInvoiceUuid(invoiceUuid);
    pr.setStatus("DELIVERED");
    pr.setMessage(resp.toString());
    return pr;
  }

  @Override
  public ProviderCancelResponse cancelInvoice(String invoiceUuid) {
    String body =
        "<CancelRequest xmlns=\"urn:provider:invoice\"><UUID>"
            + invoiceUuid
            + "</UUID></CancelRequest>";
    Object resp = template.sendSourceAndReceive(endpoint, new StringSource(body), m -> {}, r -> r);
    ProviderCancelResponse pr = new ProviderCancelResponse();
    pr.setInvoiceUuid(invoiceUuid);
    pr.setStatus("CANCELLED");
    pr.setMessage(resp.toString());
    return pr;
  }

  private String escape(String s) {
    return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
  }
}
