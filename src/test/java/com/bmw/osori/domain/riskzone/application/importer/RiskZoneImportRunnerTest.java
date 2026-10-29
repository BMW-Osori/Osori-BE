package com.bmw.osori.domain.riskzone.application.importer;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.boot.ApplicationArguments;

class RiskZoneImportRunnerTest {

	private final RiskZoneImportService importService = mock(RiskZoneImportService.class);
	private final ApplicationArguments arguments = mock(ApplicationArguments.class);

	@Test
	void 파일_옵션이_없으면_적재하지_않는다() {
		new RiskZoneImportRunner(importService, "").run(arguments);

		verifyNoInteractions(importService);
	}

	@Test
	void 파일_옵션이_있으면_해당_파일을_적재한다() {
		Path file = Path.of("/tmp/risk-zones.json");
		given(importService.importFile(any(Path.class)))
			.willReturn(new RiskZoneImportResult(1, 0, 0, List.of()));

		new RiskZoneImportRunner(importService, file.toString()).run(arguments);

		verify(importService).importFile(file);
	}
}
