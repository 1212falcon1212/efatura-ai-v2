package com.efaturaai.api.invoice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.UUID;

public class InvoiceDtos {
  public record CreateRequest(@NotBlank String customerName, @NotNull BigDecimal totalGross) {}

  public record CreateResponse(UUID id, String status) {}

  public record ActionResponse(UUID id, String status) {}
}
