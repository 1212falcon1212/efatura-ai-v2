package com.efaturaai.ubl;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import org.xml.sax.SAXException;

public class UblValidator {

  private final Validator validator;

  public UblValidator() {
    try {
      SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
      Schema schema =
          factory.newSchema(
              new StreamSource(
                  UblValidator.class
                      .getClassLoader()
                      .getResourceAsStream("ubl/xsd/UBL-Invoice-2.1.xsd")));
      this.validator = schema.newValidator();
    } catch (Exception e) {
      throw new IllegalStateException("UBL schema yüklenemedi", e);
    }
  }

  public void validate(String xml) throws SAXException {
    try {
      validator.validate(
          new StreamSource(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8))));
    } catch (SAXException e) {
      throw e;
    } catch (Exception e) {
      throw new RuntimeException("UBL doğrulama başarısız", e);
    }
  }
}
