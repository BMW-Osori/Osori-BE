package com.bmw.osori.domain.riskzone.application.importer;

import java.util.List;

public record RiskZoneImportResult(
	int createdCount,
	int updatedCount,
	int unchangedCount,
	List<RowError> errors
) {
	public int failedCount() {
		return errors.size();
	}

	public record RowError(int rowNumber, String message) {
	}
}
