package com.efaturaai.ubl;

import com.efaturaai.ubl.dto.InvoiceDto;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.Marshaller;
import java.io.StringWriter;
import javax.xml.namespace.QName;
import org.springframework.stereotype.Component;

@Component
public class UblInvoiceBuilder {

  public String buildXml(InvoiceDto dto) {
    try {
      JAXBContext context = JAXBContext.newInstance(InvoiceDto.class);
      JAXBElement<InvoiceDto> root =
          new JAXBElement<>(
              new QName("urn:oasis:names:specification:ubl:schema:xsd:Invoice-2", "Invoice"),
              InvoiceDto.class,
              dto);
      StringWriter writer = new StringWriter();
      Marshaller marshaller = context.createMarshaller();
      marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
      marshaller.marshal(root, writer);
      return writer.toString();
    } catch (Exception e) {
      throw new RuntimeException("UBL build failed", e);
    }
  }
}
