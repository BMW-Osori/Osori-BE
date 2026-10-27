package com.bmw.osori.domain.riskzone.application.importer;

import java.nio.file.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class RiskZoneImportRunner implements ApplicationRunner {

	private static final Logger log = LoggerFactory.getLogger(RiskZoneImportRunner.class);

	private final RiskZoneImportService importService;
	private final String importFile;

	public RiskZoneImportRunner(
		RiskZoneImportService importService,
		@Value("${risk-zone-import.file:}") String importFile
	) {
		this.importService = importService;
		this.importFile = importFile;
	}

	@Override
	public void run(ApplicationArguments args) {
		if (importFile.isBlank()) {
			return;
		}
		RiskZoneImportResult result = importService.importFile(Path.of(importFile));
		log.info(
			"위험구역 적재 완료: 생성 {}, 갱신 {}, 변경 없음 {}, 실패 {}",
			result.createdCount(), result.updatedCount(), result.unchangedCount(), result.failedCount()
		);
		for (RiskZoneImportResult.RowError error : result.errors()) {
			log.warn("위험구역 적재 {}행 실패: {}", error.rowNumber(), error.message());
		}
	}
}
