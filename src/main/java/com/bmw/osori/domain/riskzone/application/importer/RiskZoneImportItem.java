package com.bmw.osori.domain.riskzone.application.importer;

import com.bmw.osori.domain.riskzone.domain.RiskLevel;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record RiskZoneImportItem(
	@NotBlank @Size(max = 100) String sourceKey,
	@NotBlank @Size(max = 100) String name,
	@NotNull @DecimalMin("-90") @DecimalMax("90") Double latitude,
	@NotNull @DecimalMin("-180") @DecimalMax("180") Double longitude,
	@NotNull @Positive Integer radius,
	@NotNull @PositiveOrZero Double riskScore,
	@NotNull RiskLevel riskLevel,
	@NotNull @PositiveOrZero Integer accidentCount,
	@NotBlank @Size(max = 50) String modelVersion
) {
}
