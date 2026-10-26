package com.bmw.osori.domain.notification.application;

import com.bmw.osori.domain.device.domain.Device;
import com.bmw.osori.domain.device.domain.DeviceRepository;
import com.bmw.osori.domain.device.exception.DeviceErrorCode;
import com.bmw.osori.domain.notification.domain.NotificationHistory;
import com.bmw.osori.domain.notification.domain.NotificationHistoryRepository;
import com.bmw.osori.domain.notification.domain.NotificationStatus;
import com.bmw.osori.domain.notification.presentation.dto.request.NotificationHistoryCreateRequest;
import com.bmw.osori.domain.notification.presentation.dto.response.NotificationHistoryPageResponse;
import com.bmw.osori.domain.notification.presentation.dto.response.NotificationHistoryResponse;
import com.bmw.osori.domain.proximity.domain.ProximityEvent;
import com.bmw.osori.domain.proximity.domain.ProximityEventRepository;
import com.bmw.osori.domain.proximity.exception.ProximityEventErrorCode;
import com.bmw.osori.global.exception.BusinessException;
import com.bmw.osori.global.exception.CommonErrorCode;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class NotificationHistoryService {

	private final NotificationHistoryRepository notificationHistoryRepository;
	private final DeviceRepository deviceRepository;
	private final ProximityEventRepository proximityEventRepository;

	public NotificationHistoryService(
		NotificationHistoryRepository notificationHistoryRepository,
		DeviceRepository deviceRepository,
		ProximityEventRepository proximityEventRepository
	) {
		this.notificationHistoryRepository = notificationHistoryRepository;
		this.deviceRepository = deviceRepository;
		this.proximityEventRepository = proximityEventRepository;
	}

	@Transactional
	public NotificationHistoryResponse createNotificationHistory(NotificationHistoryCreateRequest request) {
		validateSentAt(request);
		Device device = findDeviceById(request.deviceId());
		ProximityEvent event = proximityEventRepository.findById(request.proximityEventId())
			.orElseThrow(() -> new BusinessException(ProximityEventErrorCode.PROXIMITY_EVENT_NOT_FOUND));
		if (!event.getDevice().getId().equals(device.getId())) {
			throw new BusinessException(CommonErrorCode.INVALID_INPUT_VALUE, "차량 접근 이벤트가 해당 디바이스에 속하지 않습니다.");
		}

		NotificationHistory history = NotificationHistory.create(
			device,
			event,
			request.notificationType(),
			request.title(),
			request.message(),
			request.status(),
			request.sentAt()
		);
		return NotificationHistoryResponse.from(notificationHistoryRepository.saveAndFlush(history));
	}

	public NotificationHistoryPageResponse getNotificationHistories(Long deviceId, Pageable pageable) {
		findDeviceById(deviceId);
		return NotificationHistoryPageResponse.from(
			notificationHistoryRepository.findAllByDevice_IdOrderByCreatedAtDescIdDesc(deviceId, pageable)
				.map(NotificationHistoryResponse::from)
		);
	}

	private Device findDeviceById(Long deviceId) {
		return deviceRepository.findById(deviceId)
			.orElseThrow(() -> new BusinessException(DeviceErrorCode.DEVICE_NOT_FOUND));
	}

	private void validateSentAt(NotificationHistoryCreateRequest request) {
		if (request.status() == NotificationStatus.SENT && request.sentAt() == null) {
			throw new BusinessException(CommonErrorCode.INVALID_INPUT_VALUE, "발송 성공 이력에는 발송 시각이 필요합니다.");
		}
		if (request.status() == NotificationStatus.FAILED && request.sentAt() != null) {
			throw new BusinessException(CommonErrorCode.INVALID_INPUT_VALUE, "발송 실패 이력에는 발송 시각을 지정할 수 없습니다.");
		}
	}
}
