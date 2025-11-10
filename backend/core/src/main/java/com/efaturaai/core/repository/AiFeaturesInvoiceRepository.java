package com.efaturaai.core.repository;

import com.efaturaai.core.domain.AiFeaturesInvoice;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AiFeaturesInvoiceRepository extends JpaRepository<AiFeaturesInvoice, UUID> {
  Optional<AiFeaturesInvoice> findByInvoiceId(UUID invoiceId);
}
