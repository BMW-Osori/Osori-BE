package com.bmw.osori.domain.riskzone.application.importer;

import com.bmw.osori.domain.riskzone.domain.RiskZone;
import com.bmw.osori.domain.riskzone.domain.RiskZoneRepository;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

@Service
public class RiskZoneImportService {

	private final RiskZoneRepository riskZoneRepository;
	private final JsonMapper jsonMapper;
	private final Validator validator;

	public RiskZoneImportService(RiskZoneRepository riskZoneRepository, JsonMapper jsonMapper, Validator validator) {
		this.riskZoneRepository = riskZoneRepository;
		this.jsonMapper = jsonMapper;
		this.validator = validator;
	}

	@Transactional
	public RiskZoneImportResult importFile(Path path) {
		JsonNode rows = readRows(path);
		int createdCount = 0;
		int updatedCount = 0;
		int unchangedCount = 0;
		List<RiskZoneImportResult.RowError> errors = new ArrayList<>();
		Set<String> seenKeys = new HashSet<>();

		for (int index = 0; index < rows.size(); index++) {
			int rowNumber = index + 1;
			RiskZoneImportItem item;
			try {
				item = jsonMapper.treeToValue(rows.get(index), RiskZoneImportItem.class);
			} catch (JacksonException exception) {
				errors.add(new RiskZoneImportResult.RowError(rowNumber, "필드 형식이 올바르지 않습니다: " + exception.getOriginalMessage()));
				continue;
			}
			String validationError = validate(item);
			if (validationError != null) {
				errors.add(new RiskZoneImportResult.RowError(rowNumber, validationError));
				continue;
			}
			if (!seenKeys.add(item.sourceKey())) {
				errors.add(new RiskZoneImportResult.RowError(rowNumber, "파일 내 sourceKey가 중복되었습니다: " + item.sourceKey()));
				continue;
			}

			RiskZone riskZone = riskZoneRepository.findBySourceKey(item.sourceKey()).orElse(null);
			if (riskZone == null) {
				riskZoneRepository.save(RiskZone.createImported(
					item.sourceKey(), item.name(), item.latitude(), item.longitude(), item.radius(),
					item.riskScore(), item.riskLevel(), item.accidentCount(), item.modelVersion()
				));
				createdCount++;
			} else if (riskZone.updateImported(
				item.name(), item.latitude(), item.longitude(), item.radius(), item.riskScore(),
				item.riskLevel(), item.accidentCount(), item.modelVersion()
			)) {
				updatedCount++;
			} else {
				unchangedCount++;
			}
		}
		riskZoneRepository.flush();
		return new RiskZoneImportResult(createdCount, updatedCount, unchangedCount, List.copyOf(errors));
	}

	private JsonNode readRows(Path path) {
		try {
			JsonNode rows = jsonMapper.readTree(Files.readString(path));
			if (rows == null || !rows.isArray()) {
				throw new IllegalArgumentException("위험구역 파일의 최상위 값은 JSON 배열이어야 합니다.");
			}
			return rows;
		} catch (IOException | JacksonException exception) {
			throw new IllegalArgumentException("위험구역 파일을 읽을 수 없습니다: " + path, exception);
		}
	}

	private String validate(RiskZoneImportItem item) {
		if (item == null) {
			return "위험구역 항목은 JSON 객체여야 합니다.";
		}
		Set<ConstraintViolation<RiskZoneImportItem>> violations = validator.validate(item);
		List<String> messages = new ArrayList<>(violations.stream()
			.map(violation -> violation.getPropertyPath() + " " + violation.getMessage())
			.toList());
		if (item.latitude() != null && !Double.isFinite(item.latitude())) {
			messages.add("latitude 유한한 숫자여야 합니다.");
		}
		if (item.longitude() != null && !Double.isFinite(item.longitude())) {
			messages.add("longitude 유한한 숫자여야 합니다.");
		}
		if (item.riskScore() != null && !Double.isFinite(item.riskScore())) {
			messages.add("riskScore 유한한 숫자여야 합니다.");
		}
		if (messages.isEmpty()) {
			return null;
		}
		messages.sort(String::compareTo);
		return String.join(", ", messages);
	}
}
