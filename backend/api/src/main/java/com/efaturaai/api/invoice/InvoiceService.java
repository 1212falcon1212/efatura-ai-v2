package com.efaturaai.api.invoice;

import com.efaturaai.core.domain.*;
import com.efaturaai.core.provider.EInvoiceProviderPort;
import com.efaturaai.core.repository.CustomerRepository;
import com.efaturaai.core.repository.InvoiceRepository;
import com.efaturaai.core.repository.OutboxRepository;
import com.efaturaai.core.tenant.TenantContext;
import com.efaturaai.infra.messaging.OutboxPublisher;
import com.efaturaai.signer.SignerService;
import com.efaturaai.ubl.UblInvoiceBuilder;
import io.micrometer.core.instrument.MeterRegistry;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InvoiceService {
  private static final Logger log = LoggerFactory.getLogger(InvoiceService.class);
  private final InvoiceRepository invoiceRepository;
  private final CustomerRepository customerRepository;
  private final OutboxRepository outboxRepository;
  private final OutboxPublisher outboxPublisher;
  private final UblInvoiceBuilder ublInvoiceBuilder;
  private final SignerService signerService;
  private final EInvoiceProviderPort providerClient;
  private final MeterRegistry meterRegistry;

  public InvoiceService(
      InvoiceRepository invoiceRepository,
      CustomerRepository customerRepository,
      OutboxRepository outboxRepository,
      OutboxPublisher outboxPublisher,
      UblInvoiceBuilder ublInvoiceBuilder,
      SignerService signerService,
      EInvoiceProviderPort providerClient,
      MeterRegistry meterRegistry) {
    this.invoiceRepository = invoiceRepository;
    this.customerRepository = customerRepository;
    this.outboxRepository = outboxRepository;
    this.outboxPublisher = outboxPublisher;
    this.ublInvoiceBuilder = ublInvoiceBuilder;
    this.signerService = signerService;
    this.providerClient = providerClient;
    this.meterRegistry = meterRegistry;
  }

  @Transactional
  public Invoice createDraft(String customerName, BigDecimal totalGross) {
    UUID tenantId = TenantContext.getTenantId().orElseThrow();
    Customer customer =
        customerRepository
            .findByNameAndTenant(customerName, tenantId)
            .orElseGet(
                () -> {
                  Customer c = new Customer();
                  c.setId(UUID.randomUUID());
                  c.setTenantId(tenantId);
                  c.setName(customerName);
                  c.setCreatedAt(OffsetDateTime.now());
                  return customerRepository.save(c);
                });

    Invoice inv = new Invoice();
    inv.setId(UUID.randomUUID());
    inv.setTenantId(tenantId);
    inv.setInvoiceNo("INV-" + inv.getId().toString().substring(0, 8));
    inv.setCustomerId(customer.getId());
    inv.setIssueDate(LocalDate.now());
    inv.setCurrency("TRY");
    inv.setTotalGross(totalGross);
    inv.setTotalNet(totalGross.divide(new BigDecimal("1.18"), 2, java.math.RoundingMode.HALF_UP));
    inv.setTotalVat(inv.getTotalGross().subtract(inv.getTotalNet()));
    inv.setStatus(InvoiceStatus.DRAFT);
    inv.setCreatedAt(OffsetDateTime.now());
    meterRegistry.counter("invoices.created.count").increment();
    Invoice saved = invoiceRepository.save(inv);
    log.info("Invoice created: {}", saved.getId());
    return saved;
  }

  @Transactional
  public Invoice sign(UUID id) {
    Invoice inv = invoiceRepository.findById(id).orElseThrow();
    String xml = ublInvoiceBuilder.buildXml(new com.efaturaai.ubl.dto.InvoiceDto());
    byte[] signed =
        signerService.signXadesBes(xml.getBytes(java.nio.charset.StandardCharsets.UTF_8));
    inv.setStatus(InvoiceStatus.SIGNED);
    return inv;
  }

  @Transactional
  public Invoice send(UUID id) {
    Invoice inv = invoiceRepository.findById(id).orElseThrow();
    String xml = "<Invoice id='" + inv.getId() + "'/>";
    try {
      providerClient.sendInvoice(xml);
      inv.setStatus(InvoiceStatus.SENT);
      OutboxMessage msg = new OutboxMessage();
      msg.setId(UUID.randomUUID());
      msg.setTenantId(inv.getTenantId());
      msg.setAggregateType("Invoice");
      msg.setAggregateId(inv.getId().toString());
      msg.setEventType("InvoiceSent");
      msg.setPayload("{\"invoiceId\":\"" + inv.getId() + "\"}");
      msg.setStatus(OutboxStatus.NEW);
      msg.setRetryCount(0);
      msg.setCreatedAt(OffsetDateTime.now());
      outboxRepository.save(msg);
      outboxPublisher.publish(msg);
      return inv;
    } catch (RuntimeException ex) {
      // Provider başarısız: retry için outbox kaydı oluştur, publish etme ve 500 döndür
      OutboxMessage msg = new OutboxMessage();
      msg.setId(UUID.randomUUID());
      msg.setTenantId(inv.getTenantId());
      msg.setAggregateType("Invoice");
      msg.setAggregateId(inv.getId().toString());
      msg.setEventType("InvoiceSendFailed");
      msg.setPayload("{\"invoiceId\":\"" + inv.getId() + "\"}");
      msg.setStatus(OutboxStatus.NEW);
      msg.setRetryCount(0);
      msg.setCreatedAt(OffsetDateTime.now());
      outboxRepository.save(msg);
      throw ex;
    }
  }

  @Transactional(readOnly = true)
  public Optional<Invoice> get(UUID id) {
    return invoiceRepository.findById(id);
  }

  @Transactional(readOnly = true)
  public List<Invoice> list() {
    return invoiceRepository.findAll();
  }
}
