package com.efaturaai.core.repository;

import com.efaturaai.core.domain.Invoice;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvoiceRepository extends JpaRepository<Invoice, UUID> {}
