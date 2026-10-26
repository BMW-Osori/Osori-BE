package com.bmw.osori.domain.notification.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.bmw.osori.domain.device.domain.Device;
import com.bmw.osori.domain.device.domain.DeviceRepository;
import com.bmw.osori.domain.device.domain.DeviceType;
import com.bmw.osori.domain.device.exception.DeviceErrorCode;
import com.bmw.osori.domain.notification.domain.NotificationStatus;
import com.bmw.osori.domain.notification.presentation.dto.request.NotificationHistoryCreateRequest;
import com.bmw.osori.domain.notification.presentation.dto.response.NotificationHistoryPageResponse;
import com.bmw.osori.domain.notification.presentation.dto.response.NotificationHistoryResponse;
import com.bmw.osori.domain.proximity.domain.AlertLevel;
import com.bmw.osori.domain.proximity.domain.ProximityEvent;
import com.bmw.osori.domain.proximity.domain.ProximityEventRepository;
import com.bmw.osori.domain.proximity.exception.ProximityEventErrorCode;
import com.bmw.osori.global.exception.BusinessException;
import com.bmw.osori.global.exception.CommonErrorCode;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class NotificationHistoryServiceTest {

	@Autowired
	private NotificationHistoryService notificationHistoryService;

	@Autowired
	private DeviceRepository deviceRepository;

	@Autowired
	private ProximityEventRepository proximityEventRepository;

	@Test
	void 성공과_실패_이력을_저장하고_최신순으로_조회한다() {
		Device device = createDevice("notification-device-1");
		ProximityEvent event = createEvent(device);
		LocalDateTime sentAt = LocalDateTime.of(2026, 9, 13, 11, 30, 18);

		NotificationHistoryResponse sent = notificationHistoryService.createNotificationHistory(
			request(device.getId(), event.getId(), NotificationStatus.SENT, sentAt)
		);
		NotificationHistoryResponse failed = notificationHistoryService.createNotificationHistory(
			request(device.getId(), event.getId(), NotificationStatus.FAILED, null)
		);
		Device otherDevice = createDevice("notification-device-2");
		ProximityEvent otherEvent = createEvent(otherDevice);
		notificationHistoryService.createNotificationHistory(
			request(otherDevice.getId(), otherEvent.getId(), NotificationStatus.FAILED, null)
		);
		NotificationHistoryPageResponse firstPage = notificationHistoryService.getNotificationHistories(
			device.getId(), PageRequest.of(0, 1)
		);
		NotificationHistoryPageResponse secondPage = notificationHistoryService.getNotificationHistories(
			device.getId(), PageRequest.of(1, 1)
		);

		assertThat(sent.notificationHistoryId()).isNotNull();
		assertThat(sent.deviceId()).isEqualTo(device.getId());
		assertThat(sent.proximityEventId()).isEqualTo(event.getId());
		assertThat(sent.sentAt()).isEqualTo(sentAt);
		assertThat(sent.createdAt()).isNotNull();
		assertThat(failed.sentAt()).isNull();
		assertThat(firstPage.totalElements()).isEqualTo(2L);
		assertThat(firstPage.content()).extracting(NotificationHistoryResponse::notificationHistoryId)
			.containsExactly(failed.notificationHistoryId());
		assertThat(secondPage.content()).extracting(NotificationHistoryResponse::notificationHistoryId)
			.containsExactly(sent.notificationHistoryId());
	}

	@Test
	void 이력이_없는_디바이스는_빈_페이지를_반환한다() {
		Device device = createDevice("notification-device-empty");

		NotificationHistoryPageResponse response = notificationHistoryService.getNotificationHistories(
			device.getId(), PageRequest.of(0, 20)
		);

		assertThat(response.content()).isEmpty();
		assertThat(response.totalElements()).isZero();
	}

	@Test
	void 디바이스와_이벤트가_다르면_저장하지_않는다() {
		Device device = createDevice("notification-device-mismatch-1");
		Device otherDevice = createDevice("notification-device-mismatch-2");
		ProximityEvent event = createEvent(otherDevice);

		assertThatThrownBy(() -> notificationHistoryService.createNotificationHistory(
			request(device.getId(), event.getId(), NotificationStatus.SENT, LocalDateTime.now())
		)).isInstanceOf(BusinessException.class)
			.satisfies(exception -> assertThat(((BusinessException) exception).getErrorCode())
				.isEqualTo(CommonErrorCode.INVALID_INPUT_VALUE));
	}

	@Test
	void 발송_상태와_발송_시각이_맞지_않으면_저장하지_않는다() {
		Device device = createDevice("notification-device-invalid-time");
		ProximityEvent event = createEvent(device);

		assertThatThrownBy(() -> notificationHistoryService.createNotificationHistory(
			request(device.getId(), event.getId(), NotificationStatus.SENT, null)
		)).isInstanceOf(BusinessException.class)
			.satisfies(exception -> assertThat(((BusinessException) exception).getErrorCode())
				.isEqualTo(CommonErrorCode.INVALID_INPUT_VALUE));
		assertThatThrownBy(() -> notificationHistoryService.createNotificationHistory(
			request(device.getId(), event.getId(), NotificationStatus.FAILED, LocalDateTime.now())
		)).isInstanceOf(BusinessException.class)
			.satisfies(exception -> assertThat(((BusinessException) exception).getErrorCode())
				.isEqualTo(CommonErrorCode.INVALID_INPUT_VALUE));
	}

	@Test
	void 없는_디바이스와_이벤트는_404_예외를_반환한다() {
		Device device = createDevice("notification-device-missing");

		assertThatThrownBy(() -> notificationHistoryService.getNotificationHistories(
			Long.MAX_VALUE, PageRequest.of(0, 20)
		)).isInstanceOf(BusinessException.class)
			.satisfies(exception -> assertThat(((BusinessException) exception).getErrorCode())
				.isEqualTo(DeviceErrorCode.DEVICE_NOT_FOUND));
		assertThatThrownBy(() -> notificationHistoryService.createNotificationHistory(
			request(device.getId(), Long.MAX_VALUE, NotificationStatus.FAILED, null)
		)).isInstanceOf(BusinessException.class)
			.satisfies(exception -> assertThat(((BusinessException) exception).getErrorCode())
				.isEqualTo(ProximityEventErrorCode.PROXIMITY_EVENT_NOT_FOUND));
	}

	private Device createDevice(String uuid) {
		return deviceRepository.saveAndFlush(Device.create(uuid, "테스트 디바이스", DeviceType.WEARABLE));
	}

	private ProximityEvent createEvent(Device device) {
		LocalDateTime startedAt = LocalDateTime.of(2026, 9, 13, 11, 30, 13);
		return proximityEventRepository.saveAndFlush(ProximityEvent.create(
			device, 37.5665, 126.978, -53, 2.8, AlertLevel.DANGER, 5, true,
			startedAt, startedAt.plusSeconds(5)
		));
	}

	private NotificationHistoryCreateRequest request(
		Long deviceId, Long eventId, NotificationStatus status, LocalDateTime sentAt
	) {
		return new NotificationHistoryCreateRequest(
			deviceId, eventId, "PROXIMITY_ALERT", "차량 접근 경고",
			"근처에 차량이 접근하고 있습니다.", status, sentAt
		);
	}
}
