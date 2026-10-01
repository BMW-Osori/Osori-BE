package com.bmw.osori.domain.device.domain;

import com.bmw.osori.global.entity.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
	name = "device",
	uniqueConstraints = {
		@UniqueConstraint(name = "uk_device_uuid", columnNames = "device_uuid")
	}
)
public class Device extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "device_uuid", nullable = false, unique = true, length = 100)
	private String deviceUuid;

	@Column(nullable = false, length = 100)
	private String name;

	@Enumerated(EnumType.STRING)
	@Column(name = "device_type", nullable = false, length = 30)
	private DeviceType deviceType;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private DeviceStatus status;

	protected Device() {
	}

	private Device(String deviceUuid, String name, DeviceType deviceType, DeviceStatus status) {
		this.deviceUuid = deviceUuid;
		this.name = name;
		this.deviceType = deviceType;
		this.status = status;
	}

	public static Device create(String deviceUuid, String name, DeviceType deviceType) {
		return new Device(deviceUuid, name, deviceType, DeviceStatus.ACTIVE);
	}

	public void activate() {
		this.status = DeviceStatus.ACTIVE;
	}

	public void deactivate() {
		this.status = DeviceStatus.INACTIVE;
	}

	public Long getId() {
		return id;
	}

	public String getDeviceUuid() {
		return deviceUuid;
	}

	public String getName() {
		return name;
	}

	public DeviceType getDeviceType() {
		return deviceType;
	}

	public DeviceStatus getStatus() {
		return status;
	}
}
