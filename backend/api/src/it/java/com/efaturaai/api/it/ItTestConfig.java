package com.efaturaai.api.it;

import com.efaturaai.core.provider.EInvoiceProviderPort;
import com.efaturaai.core.provider.ProviderCancelResponse;
import com.efaturaai.core.provider.ProviderSendResponse;
import com.efaturaai.core.provider.ProviderStatusResponse;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

@TestConfiguration
@Profile("it")
public class ItTestConfig {

  @Bean
  @Primary
  public EInvoiceProviderPort eInvoiceProviderStub() {
    return new EInvoiceProviderPort() {
      @Override
      public ProviderSendResponse sendInvoice(String ublXml) {
        ProviderSendResponse r = new ProviderSendResponse();
        r.setInvoiceUuid("it-stub-uuid");
        r.setStatus("SENT");
        r.setMessage("stub");
        return r;
      }

      @Override
      public ProviderStatusResponse getInvoiceStatus(String invoiceUuid) {
        ProviderStatusResponse r = new ProviderStatusResponse();
        r.setInvoiceUuid(invoiceUuid);
        r.setStatus("DELIVERED");
        r.setMessage("stub");
        return r;
      }

      @Override
      public ProviderCancelResponse cancelInvoice(String invoiceUuid) {
        ProviderCancelResponse r = new ProviderCancelResponse();
        r.setInvoiceUuid(invoiceUuid);
        r.setStatus("CANCELLED");
        r.setMessage("stub");
        return r;
      }
    };
  }
}
