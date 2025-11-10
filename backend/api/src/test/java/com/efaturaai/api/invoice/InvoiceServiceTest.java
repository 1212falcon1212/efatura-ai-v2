package com.efaturaai.api.invoice;

import static org.junit.jupiter.api.Assertions.assertThrows;

import com.efaturaai.core.domain.Customer;
import com.efaturaai.core.domain.Invoice;
import com.efaturaai.core.domain.InvoiceDocumentType;
import com.efaturaai.core.domain.InvoiceStatus;
import com.efaturaai.core.domain.OutboxMessage;
import com.efaturaai.core.domain.OutboxStatus;
import com.efaturaai.core.provider.ProviderException;
import com.efaturaai.core.provider.ProviderInvoicePort;
import com.efaturaai.core.repository.CustomerRepository;
import com.efaturaai.core.repository.InvoiceLineRepository;
import com.efaturaai.core.repository.InvoiceRepository;
import com.efaturaai.core.repository.OutboxRepository;
import com.efaturaai.core.storage.StorageService;
import com.efaturaai.core.tenant.TenantContext;
import com.efaturaai.infra.messaging.OutboxPublisher;
import com.efaturaai.infra.webhook.WebhookService;
import com.efaturaai.signer.SignerService;
import com.efaturaai.ubl.UblInvoiceBuilder;
import com.efaturaai.ubl.UblValidator;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;

public class InvoiceServiceTest {

  @Test
  void send_should_throw_provider_exception_on_non_zero_code() {
    InvoiceRepository invoiceRepo = new InMemoryInvoiceRepo();
    CustomerRepository customerRepo = new InMemoryCustomerRepo();
    InvoiceLineRepository invoiceLineRepo = new InMemoryInvoiceLineRepo();
    OutboxRepository outboxRepo = new NoopOutboxRepo();
    OutboxPublisher outboxPublisher = new MockOutboxPublisher();
    UblInvoiceBuilder ublBuilder = new UblInvoiceBuilder();
    SignerService signer = new MockSignerService();
    ProviderInvoicePort eInvoiceProvider = new MockProviderInvoicePort();
    ProviderInvoicePort eArchiveProvider = new MockProviderInvoicePort();
    SimpleMeterRegistry metrics = new SimpleMeterRegistry();
    WebhookService webhook = new MockWebhookService();
    StorageService storage = new MockStorageService();
    UblValidator validator = new UblValidator();

    InvoiceService service =
        new InvoiceService(
            invoiceRepo,
            customerRepo,
            invoiceLineRepo,
            outboxRepo,
            outboxPublisher,
            ublBuilder,
            signer,
            eInvoiceProvider,
            eArchiveProvider,
            metrics,
            webhook,
            storage,
            validator,
            "urn:test:source");

    // seed
    UUID tenant = UUID.randomUUID();
    TenantContext.setTenantId(tenant);
    Customer c = new Customer();
    c.setId(UUID.randomUUID());
    c.setTenantId(tenant);
    c.setName("Acme");
    c.setCreatedAt(OffsetDateTime.now());
    customerRepo.save(c);

    Invoice inv = new Invoice();
    inv.setId(UUID.randomUUID());
    inv.setTenantId(tenant);
    inv.setInvoiceNo("INV-001");
    inv.setCustomerId(c.getId());
    inv.setIssueDate(LocalDate.now());
    inv.setCurrency("TRY");
    inv.setTotalGross(new BigDecimal("118"));
    inv.setTotalNet(new BigDecimal("100"));
    inv.setTotalVat(new BigDecimal("18"));
    inv.setStatus(InvoiceStatus.DRAFT);
    inv.setType(InvoiceDocumentType.EINVOICE);
    inv.setCreatedAt(OffsetDateTime.now());
    invoiceRepo.save(inv);

    assertThrows(ProviderException.class, () -> service.send(inv.getId(), "test@example.com", "n"));
  }

  // Mock implementations
  static class MockOutboxPublisher extends OutboxPublisher {
    public MockOutboxPublisher() {
      super(null, "test.exchange", "test.routing");
    }
    @Override
    public void publish(OutboxMessage message) {}
  }

  static class MockSignerService implements SignerService {
    @Override
    public byte[] signXadesBes(byte[] xml) {
      return xml;
    }
    @Override
    public boolean verifyXadesBes(byte[] signedXml) {
      return true;
    }
  }

  static class MockProviderInvoicePort implements ProviderInvoicePort {
    @Override
    public List<com.efaturaai.core.provider.EntResponse> sendInvoice(List<com.efaturaai.core.provider.InputDocument> documents) {
      com.efaturaai.core.provider.EntResponse r = new com.efaturaai.core.provider.EntResponse();
      r.code = "2001";
      r.explanation = "invalid document";
      r.documentUUID = documents.get(0).documentUUID;
      return List.of(r);
    }
    @Override
    public List<com.efaturaai.core.provider.EntResponse> updateInvoice(List<com.efaturaai.core.provider.InputDocument> documents) {
      return sendInvoice(documents);
    }
    @Override
    public com.efaturaai.core.provider.EntResponse cancelInvoice(String invoiceUuid, String cancelReason, String cancelDate) {
      com.efaturaai.core.provider.EntResponse r = new com.efaturaai.core.provider.EntResponse();
      r.code = "2001";
      r.explanation = "cancel failed";
      r.documentUUID = invoiceUuid;
      return r;
    }
  }

  static class MockWebhookService extends WebhookService {
    public MockWebhookService() {
      super(null, null);
    }
    @Override
    public void publish(String eventType, String jsonPayload) {}
  }

  static class MockStorageService implements StorageService {
    @Override
    public String store(UUID tenantId, String type, int year, String objectName, byte[] data, String contentType, Map<String, String> userMetadata) {
      return "stored";
    }
  }

  static class InMemoryInvoiceLineRepo implements InvoiceLineRepository {
    private Map<UUID, com.efaturaai.core.domain.InvoiceLine> map = new HashMap<>();
    
    @Override public <S extends com.efaturaai.core.domain.InvoiceLine> S save(S entity) { map.put(entity.getId(), entity); return entity; }
    @Override public Optional<com.efaturaai.core.domain.InvoiceLine> findById(UUID uuid) { return Optional.ofNullable(map.get(uuid)); }
    @Override public List<com.efaturaai.core.domain.InvoiceLine> findAll() { return new ArrayList<>(map.values()); }
    @Override public void deleteById(UUID uuid) { map.remove(uuid); }
    @Override public boolean existsById(UUID uuid) { return map.containsKey(uuid); }
    @Override public long count() { return map.size(); }
    @Override public void delete(com.efaturaai.core.domain.InvoiceLine entity) { map.remove(entity.getId()); }
    @Override public void deleteAllById(Iterable<? extends UUID> uuids) { uuids.forEach(map::remove); }
    @Override public void deleteAll(Iterable<? extends com.efaturaai.core.domain.InvoiceLine> entities) { entities.forEach(e -> map.remove(e.getId())); }
    @Override public void deleteAll() { map.clear(); }
    @Override public <S extends com.efaturaai.core.domain.InvoiceLine> List<S> saveAll(Iterable<S> entities) { 
      List<S> result = new ArrayList<>();
      entities.forEach(e -> result.add(save(e)));
      return result;
    }
    @Override public List<com.efaturaai.core.domain.InvoiceLine> findAllById(Iterable<UUID> uuids) { 
      return StreamSupport.stream(uuids.spliterator(), false)
          .map(map::get)
          .filter(java.util.Objects::nonNull)
          .collect(Collectors.toList());
    }
    @Override public void flush() {}
    @Override public <S extends com.efaturaai.core.domain.InvoiceLine> S saveAndFlush(S entity) { return save(entity); }
    @Override public void deleteAllInBatch(Iterable<com.efaturaai.core.domain.InvoiceLine> entities) { entities.forEach(e -> map.remove(e.getId())); }
    @Override public void deleteAllInBatch() { map.clear(); }
    @Override public com.efaturaai.core.domain.InvoiceLine getOne(UUID uuid) { return map.get(uuid); }
    @Override public com.efaturaai.core.domain.InvoiceLine getById(UUID uuid) { return map.get(uuid); }
    @Override public <S extends com.efaturaai.core.domain.InvoiceLine> List<S> saveAllAndFlush(Iterable<S> entities) { return saveAll(entities); }
    @Override public com.efaturaai.core.domain.InvoiceLine getReferenceById(UUID uuid) { return map.get(uuid); }
    @Override public void deleteAllByIdInBatch(Iterable<UUID> uuids) { uuids.forEach(map::remove); }
    
    // QueryByExampleExecutor methods
    @Override public <S extends com.efaturaai.core.domain.InvoiceLine> Optional<S> findOne(Example<S> example) { return Optional.empty(); }
    @Override public <S extends com.efaturaai.core.domain.InvoiceLine> List<S> findAll(Example<S> example) { return List.of(); }
    @Override public <S extends com.efaturaai.core.domain.InvoiceLine> List<S> findAll(Example<S> example, Sort sort) { return List.of(); }
    @Override public <S extends com.efaturaai.core.domain.InvoiceLine> Page<S> findAll(Example<S> example, Pageable pageable) { return Page.empty(); }
    @Override public <S extends com.efaturaai.core.domain.InvoiceLine> long count(Example<S> example) { return 0; }
    @Override public <S extends com.efaturaai.core.domain.InvoiceLine> boolean exists(Example<S> example) { return false; }
    @Override public <S extends com.efaturaai.core.domain.InvoiceLine, R> R findBy(Example<S> example, java.util.function.Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) { return null; }
    
    // PagingAndSortingRepository methods
    @Override public List<com.efaturaai.core.domain.InvoiceLine> findAll(Sort sort) { return findAll(); }
    @Override public Page<com.efaturaai.core.domain.InvoiceLine> findAll(Pageable pageable) { return Page.empty(); }
    
    // InvoiceLineRepository specific method
    @Override public List<com.efaturaai.core.domain.InvoiceLine> findByInvoiceId(UUID invoiceId) { return map.values().stream().filter(l -> l.getInvoiceId().equals(invoiceId)).collect(Collectors.toList()); }
  }


  // Minimal in-memory fakes
  static class InMemoryInvoiceRepo implements InvoiceRepository {
    private Map<UUID, Invoice> map = new HashMap<>();
    
    @Override public <S extends Invoice> S save(S entity) { map.put(entity.getId(), entity); return entity; }
    @Override public Optional<Invoice> findById(UUID uuid) { return Optional.ofNullable(map.get(uuid)); }
    @Override public List<Invoice> findAll() { return new ArrayList<>(map.values()); }
    @Override public void deleteById(UUID uuid) { map.remove(uuid); }
    @Override public boolean existsById(UUID uuid) { return map.containsKey(uuid); }
    @Override public long count() { return map.size(); }
    @Override public void delete(Invoice entity) { map.remove(entity.getId()); }
    @Override public void deleteAllById(Iterable<? extends UUID> uuids) { uuids.forEach(map::remove); }
    @Override public void deleteAll(Iterable<? extends Invoice> entities) { entities.forEach(e -> map.remove(e.getId())); }
    @Override public void deleteAll() { map.clear(); }
    @Override public <S extends Invoice> List<S> saveAll(Iterable<S> entities) { 
      List<S> result = new ArrayList<>();
      entities.forEach(e -> result.add(save(e)));
      return result;
    }
    @Override public List<Invoice> findAllById(Iterable<UUID> uuids) { 
      return StreamSupport.stream(uuids.spliterator(), false)
          .map(map::get)
          .filter(java.util.Objects::nonNull)
          .collect(Collectors.toList());
    }
    @Override public void flush() {}
    @Override public <S extends Invoice> S saveAndFlush(S entity) { return save(entity); }
    @Override public void deleteAllInBatch(Iterable<Invoice> entities) { entities.forEach(e -> map.remove(e.getId())); }
    @Override public void deleteAllInBatch() { map.clear(); }
    @Override public Invoice getOne(UUID uuid) { return map.get(uuid); }
    @Override public Invoice getById(UUID uuid) { return map.get(uuid); }
    @Override public <S extends Invoice> List<S> saveAllAndFlush(Iterable<S> entities) { return saveAll(entities); }
    @Override public Invoice getReferenceById(UUID uuid) { return map.get(uuid); }
    @Override public void deleteAllByIdInBatch(Iterable<UUID> uuids) { uuids.forEach(map::remove); }
    
    // QueryByExampleExecutor methods
    @Override public <S extends Invoice> Optional<S> findOne(Example<S> example) { return Optional.empty(); }
    @Override public <S extends Invoice> List<S> findAll(Example<S> example) { return List.of(); }
    @Override public <S extends Invoice> List<S> findAll(Example<S> example, Sort sort) { return List.of(); }
    @Override public <S extends Invoice> Page<S> findAll(Example<S> example, Pageable pageable) { return Page.empty(); }
    @Override public <S extends Invoice> long count(Example<S> example) { return 0; }
    @Override public <S extends Invoice> boolean exists(Example<S> example) { return false; }
    @Override public <S extends Invoice, R> R findBy(Example<S> example, java.util.function.Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) { return null; }
    
    // PagingAndSortingRepository methods
    @Override public List<Invoice> findAll(Sort sort) { return findAll(); }
    @Override public Page<Invoice> findAll(Pageable pageable) { return Page.empty(); }
    
    // InvoiceRepository specific methods
    @Override public List<Invoice> findSentInvoices(UUID tenantId, InvoiceStatus status, com.efaturaai.core.domain.InvoiceDocumentType type) { return List.of(); }
    @Override public List<Invoice> findSentInvoicesByDateRange(UUID tenantId, InvoiceStatus status, com.efaturaai.core.domain.InvoiceDocumentType type, LocalDate start, LocalDate end) { return List.of(); }
    @Override public long countByTenantId(UUID tenantId) { return map.values().stream().filter(i -> i.getTenantId().equals(tenantId)).count(); }
    @Override public BigDecimal sumTotalGrossByTenantId(UUID tenantId) { return BigDecimal.ZERO; }
    @Override public BigDecimal sumTotalVatByTenantId(UUID tenantId) { return BigDecimal.ZERO; }
    @Override public long countByTenantIdAndStatusAndType(UUID tenantId, InvoiceStatus status, com.efaturaai.core.domain.InvoiceDocumentType type) { return 0; }
  }

  static class InMemoryCustomerRepo implements CustomerRepository {
    private Map<UUID, Customer> map = new HashMap<>();
    
    @Override public Optional<Customer> findById(UUID uuid) { return Optional.ofNullable(map.get(uuid)); }
    @Override public Optional<Customer> findByNameAndTenant(String name, UUID tenantId) { return map.values().stream().filter(c -> name.equals(c.getName()) && tenantId.equals(c.getTenantId())).findFirst(); }
    @Override public <S extends Customer> S save(S entity) { map.put(entity.getId(), entity); return entity; }
    @Override public List<Customer> findAll() { return new ArrayList<>(map.values()); }
    @Override public void deleteById(UUID uuid) { map.remove(uuid); }
    @Override public boolean existsById(UUID uuid) { return map.containsKey(uuid); }
    @Override public long count() { return map.size(); }
    @Override public void delete(Customer entity) { map.remove(entity.getId()); }
    @Override public void deleteAllById(Iterable<? extends UUID> uuids) { uuids.forEach(map::remove); }
    @Override public void deleteAll(Iterable<? extends Customer> entities) { entities.forEach(e -> map.remove(e.getId())); }
    @Override public void deleteAll() { map.clear(); }
    @Override public <S extends Customer> List<S> saveAll(Iterable<S> entities) { 
      List<S> result = new ArrayList<>();
      entities.forEach(e -> result.add(save(e)));
      return result;
    }
    @Override public List<Customer> findAllById(Iterable<UUID> uuids) { 
      return StreamSupport.stream(uuids.spliterator(), false)
          .map(map::get)
          .filter(java.util.Objects::nonNull)
          .collect(Collectors.toList());
    }
    @Override public void flush() {}
    @Override public <S extends Customer> S saveAndFlush(S entity) { return save(entity); }
    @Override public void deleteAllInBatch(Iterable<Customer> entities) { entities.forEach(e -> map.remove(e.getId())); }
    @Override public void deleteAllInBatch() { map.clear(); }
    @Override public Customer getOne(UUID uuid) { return map.get(uuid); }
    @Override public Customer getById(UUID uuid) { return map.get(uuid); }
    @Override public <S extends Customer> List<S> saveAllAndFlush(Iterable<S> entities) { return saveAll(entities); }
    @Override public Customer getReferenceById(UUID uuid) { return map.get(uuid); }
    @Override public void deleteAllByIdInBatch(Iterable<UUID> uuids) { uuids.forEach(map::remove); }
    
    // QueryByExampleExecutor methods
    @Override public <S extends Customer> Optional<S> findOne(Example<S> example) { return Optional.empty(); }
    @Override public <S extends Customer> List<S> findAll(Example<S> example) { return List.of(); }
    @Override public <S extends Customer> List<S> findAll(Example<S> example, Sort sort) { return List.of(); }
    @Override public <S extends Customer> Page<S> findAll(Example<S> example, Pageable pageable) { return Page.empty(); }
    @Override public <S extends Customer> long count(Example<S> example) { return 0; }
    @Override public <S extends Customer> boolean exists(Example<S> example) { return false; }
    @Override public <S extends Customer, R> R findBy(Example<S> example, java.util.function.Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) { return null; }
    
    // PagingAndSortingRepository methods
    @Override public List<Customer> findAll(Sort sort) { return findAll(); }
    @Override public Page<Customer> findAll(Pageable pageable) { return Page.empty(); }
  }

  static class NoopOutboxRepo implements OutboxRepository {
    @Override public <S extends OutboxMessage> S save(S entity) { return entity; }
    @Override public Optional<OutboxMessage> findById(UUID uuid) { return Optional.empty(); }
    @Override public List<OutboxMessage> findAll() { return List.of(); }
    @Override public void deleteById(UUID uuid) {}
    @Override public boolean existsById(UUID uuid) { return false; }
    @Override public long count() { return 0; }
    @Override public void delete(OutboxMessage entity) {}
    @Override public void deleteAllById(Iterable<? extends UUID> uuids) {}
    @Override public void deleteAll(Iterable<? extends OutboxMessage> entities) {}
    @Override public void deleteAll() {}
    @Override public <S extends OutboxMessage> List<S> saveAll(Iterable<S> entities) { return List.of(); }
    @Override public List<OutboxMessage> findAllById(Iterable<UUID> uuids) { return List.of(); }
    @Override public void flush() {}
    @Override public <S extends OutboxMessage> S saveAndFlush(S entity) { return entity; }
    @Override public void deleteAllInBatch(Iterable<OutboxMessage> entities) {}
    @Override public void deleteAllInBatch() {}
    @Override public OutboxMessage getOne(UUID uuid) { return null; }
    @Override public OutboxMessage getById(UUID uuid) { return null; }
    @Override public <S extends OutboxMessage> List<S> saveAllAndFlush(Iterable<S> entities) { return List.of(); }
    @Override public OutboxMessage getReferenceById(UUID uuid) { return null; }
    @Override public void deleteAllByIdInBatch(Iterable<UUID> uuids) {}
    
    // QueryByExampleExecutor methods
    @Override public <S extends OutboxMessage> Optional<S> findOne(Example<S> example) { return Optional.empty(); }
    @Override public <S extends OutboxMessage> List<S> findAll(Example<S> example) { return List.of(); }
    @Override public <S extends OutboxMessage> List<S> findAll(Example<S> example, Sort sort) { return List.of(); }
    @Override public <S extends OutboxMessage> Page<S> findAll(Example<S> example, Pageable pageable) { return Page.empty(); }
    @Override public <S extends OutboxMessage> long count(Example<S> example) { return 0; }
    @Override public <S extends OutboxMessage> boolean exists(Example<S> example) { return false; }
    @Override public <S extends OutboxMessage, R> R findBy(Example<S> example, java.util.function.Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) { return null; }
    
    // PagingAndSortingRepository methods
    @Override public List<OutboxMessage> findAll(Sort sort) { return findAll(); }
    @Override public Page<OutboxMessage> findAll(Pageable pageable) { return Page.empty(); }
    
    // OutboxRepository specific methods
    @Override public List<OutboxMessage> findReady(OutboxStatus status, OffsetDateTime now, UUID tenantId) { return List.of(); }
    @Override public int updateStatus(UUID id, OutboxStatus status, int retry) { return 0; }
    @Override public int markProcessed(UUID id) { return 0; }
    @Override public int updateFailure(UUID id, OutboxStatus status, int retry, OffsetDateTime next) { return 0; }
  }
}
