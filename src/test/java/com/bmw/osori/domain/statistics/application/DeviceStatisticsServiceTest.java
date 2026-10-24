package com.bmw.osori.domain.statistics.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.bmw.osori.domain.device.domain.Device;
import com.bmw.osori.domain.device.domain.DeviceRepository;
import com.bmw.osori.domain.device.domain.DeviceType;
import com.bmw.osori.domain.device.exception.DeviceErrorCode;
import com.bmw.osori.domain.proximity.domain.AlertLevel;
import com.bmw.osori.domain.proximity.domain.ProximityEvent;
import com.bmw.osori.domain.proximity.domain.ProximityEventRepository;
import com.bmw.osori.domain.statistics.presentation.dto.response.DeviceStatisticsResponse;
import com.bmw.osori.global.exception.BusinessException;
import com.bmw.osori.global.exception.CommonErrorCode;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class DeviceStatisticsServiceTest {

	@Autowired
	private DeviceStatisticsService deviceStatisticsService;

	@Autowired
	private DeviceRepository deviceRepository;

	@Autowired
	private ProximityEventRepository proximityEventRepository;

	@Test
	void 디바이스와_기간별_감지_통계를_집계한다() {
		Device device = deviceRepository.save(Device.create("stats-device-1", "테스트 디바이스", DeviceType.WEARABLE));
		Device otherDevice = deviceRepository.save(Device.create("stats-device-2", "다른 디바이스", DeviceType.BAG));
		LocalDateTime from = LocalDateTime.of(2026, 9, 1, 0, 0);
		LocalDateTime to = LocalDateTime.of(2026, 9, 30, 23, 59, 59);
		saveEvent(device, AlertLevel.CAUTION, false, from);
		saveEvent(device, AlertLevel.DANGER, true, to);
		saveEvent(device, AlertLevel.DANGER, true, from.minusSeconds(1));
		saveEvent(device, AlertLevel.SAFE, false, to.plusSeconds(1));
		saveEvent(otherDevice, AlertLevel.DANGER, true, from.plusDays(1));
		proximityEventRepository.flush();

		DeviceStatisticsResponse response = deviceStatisticsService.getDeviceStatistics(device.getId(), from, to);

		assertThat(response.deviceId()).isEqualTo(device.getId());
		assertThat(response.from()).isEqualTo(from);
		assertThat(response.to()).isEqualTo(to);
		assertThat(response.totalCount()).isEqualTo(2L);
		assertThat(response.vibrationTriggeredCount()).isEqualTo(1L);
		assertThat(response.alertLevelCounts()).containsEntry(AlertLevel.CAUTION, 1L)
			.containsEntry(AlertLevel.DANGER, 1L)
			.containsEntry(AlertLevel.SAFE, 0L)
			.containsEntry(AlertLevel.RELEASED, 0L);
	}

	@Test
	void 기간을_생략하면_전체_이력을_집계한다() {
		Device device = deviceRepository.save(Device.create("stats-device-all", "전체 이력", DeviceType.WEARABLE));
		saveEvent(device, AlertLevel.DANGER, true, LocalDateTime.of(2026, 1, 1, 0, 0));
		proximityEventRepository.flush();

		DeviceStatisticsResponse response = deviceStatisticsService.getDeviceStatistics(device.getId(), null, null);

		assertThat(response.totalCount()).isEqualTo(1L);
		assertThat(response.vibrationTriggeredCount()).isEqualTo(1L);
		assertThat(response.alertLevelCounts()).containsEntry(AlertLevel.DANGER, 1L);
	}

	@Test
	void 기간의_한쪽_경계만_지정할_수_있다() {
		Device device = deviceRepository.save(Device.create("stats-device-one-bound", "한쪽 경계", DeviceType.WEARABLE));
		LocalDateTime boundary = LocalDateTime.of(2026, 9, 1, 0, 0);
		saveEvent(device, AlertLevel.CAUTION, false, boundary.minusSeconds(1));
		saveEvent(device, AlertLevel.DANGER, true, boundary);
		proximityEventRepository.flush();

		DeviceStatisticsResponse after = deviceStatisticsService.getDeviceStatistics(device.getId(), boundary, null);
		DeviceStatisticsResponse before = deviceStatisticsService.getDeviceStatistics(device.getId(), null, boundary);

		assertThat(after.totalCount()).isEqualTo(1L);
		assertThat(after.alertLevelCounts()).containsEntry(AlertLevel.DANGER, 1L);
		assertThat(before.totalCount()).isEqualTo(2L);
		assertThat(before.alertLevelCounts()).containsEntry(AlertLevel.CAUTION, 1L);
	}

	@Test
	void 이벤트가_없으면_모든_횟수가_0이다() {
		Device device = deviceRepository.save(Device.create("stats-device-empty", "빈 이력", DeviceType.WEARABLE));

		DeviceStatisticsResponse response = deviceStatisticsService.getDeviceStatistics(device.getId(), null, null);

		assertThat(response.totalCount()).isZero();
		assertThat(response.vibrationTriggeredCount()).isZero();
		assertThat(response.alertLevelCounts()).containsOnlyKeys(AlertLevel.values())
			.allSatisfy((level, count) -> assertThat(count).isZero());
	}

	@Test
	void 존재하지_않는_디바이스는_404_예외를_반환한다() {
		assertThatThrownBy(() -> deviceStatisticsService.getDeviceStatistics(Long.MAX_VALUE, null, null))
			.isInstanceOf(BusinessException.class)
			.satisfies(exception -> assertThat(((BusinessException) exception).getErrorCode())
				.isEqualTo(DeviceErrorCode.DEVICE_NOT_FOUND));
	}

	@Test
	void 조회_시작이_종료보다_늦으면_400_예외를_반환한다() {
		LocalDateTime from = LocalDateTime.of(2026, 10, 1, 0, 0);
		LocalDateTime to = LocalDateTime.of(2026, 9, 1, 0, 0);

		assertThatThrownBy(() -> deviceStatisticsService.getDeviceStatistics(1L, from, to))
			.isInstanceOf(BusinessException.class)
			.satisfies(exception -> assertThat(((BusinessException) exception).getErrorCode())
				.isEqualTo(CommonErrorCode.INVALID_INPUT_VALUE));
	}

	private void saveEvent(Device device, AlertLevel alertLevel, boolean vibrationTriggered, LocalDateTime startedAt) {
		proximityEventRepository.save(ProximityEvent.create(
			device, 37.5665, 126.978, -53, 2.8, alertLevel, 5, vibrationTriggered,
			startedAt, startedAt.plusSeconds(5)
		));
	}
}
