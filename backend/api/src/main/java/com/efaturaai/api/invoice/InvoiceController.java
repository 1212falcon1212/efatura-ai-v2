package com.efaturaai.api.invoice;

import com.efaturaai.api.invoice.dto.InvoiceDtos;
import com.efaturaai.core.domain.Invoice;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/invoices")
public class InvoiceController {

  private final InvoiceService service;

  public InvoiceController(InvoiceService service) {
    this.service = service;
  }

  @PostMapping
  public ResponseEntity<InvoiceDtos.CreateResponse> create(
      @Valid @RequestBody InvoiceDtos.CreateRequest req) {
    Invoice i = service.createDraft(req.customerName(), req.totalGross());
    return ResponseEntity.status(201)
        .body(new InvoiceDtos.CreateResponse(i.getId(), i.getStatus().name()));
  }

  @PostMapping("/{id}/sign")
  public ResponseEntity<InvoiceDtos.ActionResponse> sign(@PathVariable UUID id) {
    Invoice i = service.sign(id);
    return ResponseEntity.ok(new InvoiceDtos.ActionResponse(i.getId(), i.getStatus().name()));
  }

  @PostMapping("/{id}/send")
  public ResponseEntity<InvoiceDtos.ActionResponse> send(@PathVariable UUID id) {
    Invoice i = service.send(id);
    return ResponseEntity.ok(new InvoiceDtos.ActionResponse(i.getId(), i.getStatus().name()));
  }

  @GetMapping
  public List<Invoice> list() {
    return service.list();
  }

  @GetMapping("/{id}")
  public ResponseEntity<Invoice> get(@PathVariable UUID id) {
    return service.get(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
  }

  @GetMapping(value = "/{id}/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
  public byte[] pdf(@PathVariable UUID id) {
    // Stub PDF
    return "%PDF-1.4\n%EOF\n".getBytes(java.nio.charset.StandardCharsets.UTF_8);
  }
}
