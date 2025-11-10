package com.efaturaai.core.repository;

import com.efaturaai.core.domain.InvoiceLine;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface InvoiceLineRepository extends JpaRepository<InvoiceLine, UUID> {
  @Query("select il from InvoiceLine il where il.invoiceId = :invoiceId")
  List<InvoiceLine> findByInvoiceId(@Param("invoiceId") UUID invoiceId);
}

