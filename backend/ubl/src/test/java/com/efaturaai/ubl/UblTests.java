package com.efaturaai.ubl;

import static org.junit.jupiter.api.Assertions.*;

import com.efaturaai.ubl.dto.InvoiceDto;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

public class UblTests {

  @Test
  void build_and_validate_invoice() throws Exception {
    InvoiceDto dto = new InvoiceDto();
    dto.setId("INV-1");
    dto.setIssueDate(LocalDate.now());
    dto.setSupplierName("Tedarikçi AŞ");
    dto.setCustomerName("Müşteri Ltd");
    dto.setLineExtensionAmount(new BigDecimal("100"));
    dto.setTaxAmount(new BigDecimal("18"));
    dto.setPayableAmount(new BigDecimal("118"));

    UblInvoiceBuilder builder = new UblInvoiceBuilder();
    String xml = builder.buildXml(dto);
    assertTrue(xml.contains("Invoice"));

    UblValidator validator = new UblValidator();
    assertDoesNotThrow(
        () -> {
          try {
            validator.validate(xml);
          } catch (SAXException e) {
            throw new RuntimeException(e);
          }
        });
  }
}
