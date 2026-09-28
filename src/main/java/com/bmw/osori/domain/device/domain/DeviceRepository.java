package com.bmw.osori.domain.device.domain;

import org.springframework.data.jpa.repository.JpaRepository;

public interface DeviceRepository extends JpaRepository<Device, Long> {

	boolean existsByDeviceUuid(String deviceUuid);
}
