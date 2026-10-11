package com.bmw.osori.domain.signal.application;

import com.bmw.osori.domain.proximity.domain.ProximityEvent;
import com.bmw.osori.domain.proximity.domain.ProximityEventRepository;
import com.bmw.osori.domain.proximity.exception.ProximityEventErrorCode;
import com.bmw.osori.domain.signal.domain.SignalSample;
import com.bmw.osori.domain.signal.domain.SignalSampleRepository;
import com.bmw.osori.domain.signal.presentation.dto.request.SignalSampleCreateRequest;
import com.bmw.osori.domain.signal.presentation.dto.response.SignalSampleCreateResponse;
import com.bmw.osori.domain.signal.presentation.dto.response.SignalSampleListResponse;
import com.bmw.osori.global.exception.BusinessException;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class SignalSampleService {

	private final SignalSampleRepository signalSampleRepository;
	private final ProximityEventRepository proximityEventRepository;

	public SignalSampleService(
		SignalSampleRepository signalSampleRepository,
		ProximityEventRepository proximityEventRepository
	) {
		this.signalSampleRepository = signalSampleRepository;
		this.proximityEventRepository = proximityEventRepository;
	}

	@Transactional
	public SignalSampleCreateResponse createSignalSamples(Long eventId, SignalSampleCreateRequest request) {
		ProximityEvent proximityEvent = findProximityEventById(eventId);
		List<SignalSample> signalSamples = request.samples()
			.stream()
			.map(sample -> SignalSample.create(
				proximityEvent,
				sample.rssi(),
				sample.estimatedDistance(),
				sample.measuredAt()
			))
			.toList();

		List<SignalSample> savedSignalSamples = signalSampleRepository.saveAll(signalSamples);

		return new SignalSampleCreateResponse(eventId, savedSignalSamples.size());
	}

	public SignalSampleListResponse getSignalSamples(Long eventId) {
		findProximityEventById(eventId);
		List<SignalSample> signalSamples = signalSampleRepository.findAllByProximityEventIdOrderByMeasuredAtAsc(eventId);

		return SignalSampleListResponse.of(eventId, signalSamples);
	}

	private ProximityEvent findProximityEventById(Long eventId) {
		return proximityEventRepository.findById(eventId)
			.orElseThrow(() -> new BusinessException(ProximityEventErrorCode.PROXIMITY_EVENT_NOT_FOUND));
	}
}
