package com.efaturaai.core.repository;

import com.efaturaai.core.domain.Invoice;
import com.efaturaai.core.domain.InvoiceDocumentType;
import com.efaturaai.core.domain.InvoiceStatus;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface InvoiceRepository extends JpaRepository<Invoice, UUID> {
  
  @Query("SELECT i FROM Invoice i WHERE i.tenantId = :tenantId AND i.status = :status AND i.type = :type AND i.issueDate >= :startDate AND i.issueDate <= :endDate ORDER BY i.issueDate DESC")
  List<Invoice> findSentInvoicesByDateRange(
      @Param("tenantId") UUID tenantId,
      @Param("status") InvoiceStatus status,
      @Param("type") InvoiceDocumentType type,
      @Param("startDate") LocalDate startDate,
      @Param("endDate") LocalDate endDate);
  
  @Query("SELECT i FROM Invoice i WHERE i.tenantId = :tenantId AND i.status = :status AND i.type = :type ORDER BY i.sentAt DESC")
  List<Invoice> findSentInvoices(
      @Param("tenantId") UUID tenantId,
      @Param("status") InvoiceStatus status,
      @Param("type") InvoiceDocumentType type);
  
  @Query("SELECT COUNT(i) FROM Invoice i WHERE i.tenantId = :tenantId")
  long countByTenantId(@Param("tenantId") UUID tenantId);
  
  @Query("SELECT COALESCE(SUM(i.totalVat), 0) FROM Invoice i WHERE i.tenantId = :tenantId")
  BigDecimal sumTotalVatByTenantId(@Param("tenantId") UUID tenantId);
  
  @Query("SELECT COALESCE(SUM(i.totalGross), 0) FROM Invoice i WHERE i.tenantId = :tenantId")
  BigDecimal sumTotalGrossByTenantId(@Param("tenantId") UUID tenantId);
  
  @Query("SELECT COUNT(i) FROM Invoice i WHERE i.tenantId = :tenantId AND i.status = :status AND i.type = :type")
  long countByTenantIdAndStatusAndType(
      @Param("tenantId") UUID tenantId,
      @Param("status") InvoiceStatus status,
      @Param("type") InvoiceDocumentType type);
}
