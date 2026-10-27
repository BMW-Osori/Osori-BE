package com.bmw.osori.domain.riskzone.application.importer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.bmw.osori.domain.riskzone.application.RiskZoneService;
import com.bmw.osori.domain.riskzone.domain.RiskLevel;
import com.bmw.osori.domain.riskzone.domain.RiskZone;
import com.bmw.osori.domain.riskzone.domain.RiskZoneRepository;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class RiskZoneImportServiceTest {

	@TempDir
	Path tempDir;

	@Autowired
	private RiskZoneImportService importService;

	@Autowired
	private RiskZoneRepository riskZoneRepository;

	@Autowired
	private RiskZoneService riskZoneService;

	@Test
	void 새_위험구역을_적재하고_조회_API에_반영한다() throws IOException {
		Path file = write("zones.json", "[" + item("zone-1", "테스트 구역", 37.5665, 87.5) + "]");

		RiskZoneImportResult result = importService.importFile(file);

		assertThat(result.createdCount()).isEqualTo(1);
		assertThat(result.updatedCount()).isZero();
		assertThat(result.failedCount()).isZero();
		RiskZone saved = riskZoneRepository.findBySourceKey("zone-1").orElseThrow();
		assertThat(saved.getRiskLevel()).isEqualTo(RiskLevel.HIGH);
		assertThat(riskZoneService.getRiskZones(37.5665, 126.978, 100).content())
			.extracting(zone -> zone.riskZoneId()).containsExactly(saved.getId());
	}

	@Test
	void 같은_파일은_중복_없이_재적재하고_변경된_값만_갱신한다() throws IOException {
		Path file = write("zones.json", "[" + item("zone-1", "테스트 구역", 37.5665, 87.5) + "]");
		importService.importFile(file);
		Long originalId = riskZoneRepository.findBySourceKey("zone-1").orElseThrow().getId();

		RiskZoneImportResult unchanged = importService.importFile(file);
		write("zones.json", "[" + item("zone-1", "갱신된 구역", 37.5667, 91.0) + "]");
		RiskZoneImportResult updated = importService.importFile(file);

		assertThat(unchanged.createdCount()).isZero();
		assertThat(unchanged.unchangedCount()).isEqualTo(1);
		assertThat(updated.updatedCount()).isEqualTo(1);
		assertThat(riskZoneRepository.count()).isEqualTo(1);
		RiskZone saved = riskZoneRepository.findBySourceKey("zone-1").orElseThrow();
		assertThat(saved.getId()).isEqualTo(originalId);
		assertThat(saved.getName()).isEqualTo("갱신된 구역");
		assertThat(saved.getRiskScore()).isEqualTo(91.0);
	}

	@Test
	void 잘못된_행과_파일_내_중복키는_건너뛰고_오류를_보고한다() throws IOException {
		Path file = write("zones.json", "["
			+ item("zone-1", "정상 구역", 37.5665, 87.5) + ","
			+ item("zone-2", "잘못된 좌표", 91.0, 87.5) + ","
			+ item("zone-1", "중복 구역", 37.5666, 90.0) + ","
			+ item("zone-3", "잘못된 단계", 37.5665, 87.5).replace("HIGH", "UNKNOWN") + "]");

		RiskZoneImportResult result = importService.importFile(file);

		assertThat(result.createdCount()).isEqualTo(1);
		assertThat(result.failedCount()).isEqualTo(3);
		assertThat(result.errors()).extracting(RiskZoneImportResult.RowError::rowNumber)
			.containsExactly(2, 3, 4);
		assertThat(riskZoneRepository.count()).isEqualTo(1);
	}

	@Test
	void JSON_배열이_아니거나_파일이_없으면_적재를_중단한다() throws IOException {
		Path invalid = write("invalid.json", "{\"sourceKey\":\"zone-1\"}");

		assertThatThrownBy(() -> importService.importFile(invalid))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessageContaining("JSON 배열");
		assertThatThrownBy(() -> importService.importFile(tempDir.resolve("missing.json")))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessageContaining("읽을 수 없습니다");
		assertThat(riskZoneRepository.count()).isZero();
	}

	private Path write(String name, String content) throws IOException {
		return Files.writeString(tempDir.resolve(name), content);
	}

	private String item(String sourceKey, String name, double latitude, double riskScore) {
		return """
			{
			  "sourceKey": "%s",
			  "name": "%s",
			  "latitude": %s,
			  "longitude": 126.978,
			  "radius": 100,
			  "riskScore": %s,
			  "riskLevel": "HIGH",
			  "accidentCount": 3,
			  "modelVersion": "sample-v1"
			}
			""".formatted(sourceKey, name, latitude, riskScore);
	}
}
