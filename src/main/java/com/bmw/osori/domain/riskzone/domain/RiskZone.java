package com.bmw.osori.domain.riskzone.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.Objects;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "risk_zone")
public class RiskZone {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "source_key", unique = true, length = 100)
	private String sourceKey;

	@Column(nullable = false, length = 100)
	private String name;

	@Column(nullable = false)
	private Double latitude;

	@Column(nullable = false)
	private Double longitude;

	@Column(nullable = false)
	private Integer radius;

	@Column(name = "risk_score", nullable = false)
	private Double riskScore;

	@Enumerated(EnumType.STRING)
	@Column(name = "risk_level", nullable = false, length = 20)
	private RiskLevel riskLevel;

	@Column(name = "accident_count", nullable = false)
	private Integer accidentCount;

	@Column(name = "model_version", nullable = false, length = 50)
	private String modelVersion;

	@UpdateTimestamp
	@Column(name = "updated_at", nullable = false)
	private LocalDateTime updatedAt;

	protected RiskZone() {
	}

	private RiskZone(
		String sourceKey,
		String name,
		Double latitude,
		Double longitude,
		Integer radius,
		Double riskScore,
		RiskLevel riskLevel,
		Integer accidentCount,
		String modelVersion
	) {
		this.sourceKey = sourceKey;
		this.name = name;
		this.latitude = latitude;
		this.longitude = longitude;
		this.radius = radius;
		this.riskScore = riskScore;
		this.riskLevel = riskLevel;
		this.accidentCount = accidentCount;
		this.modelVersion = modelVersion;
	}

	public static RiskZone create(
		String name,
		Double latitude,
		Double longitude,
		Integer radius,
		Double riskScore,
		RiskLevel riskLevel,
		Integer accidentCount,
		String modelVersion
	) {
		return new RiskZone(
			null,
			name,
			latitude,
			longitude,
			radius,
			riskScore,
			riskLevel,
			accidentCount,
			modelVersion
		);
	}

	public static RiskZone createImported(
		String sourceKey,
		String name,
		Double latitude,
		Double longitude,
		Integer radius,
		Double riskScore,
		RiskLevel riskLevel,
		Integer accidentCount,
		String modelVersion
	) {
		return new RiskZone(
			sourceKey, name, latitude, longitude, radius, riskScore,
			riskLevel, accidentCount, modelVersion
		);
	}

	public boolean updateImported(
		String name,
		Double latitude,
		Double longitude,
		Integer radius,
		Double riskScore,
		RiskLevel riskLevel,
		Integer accidentCount,
		String modelVersion
	) {
		if (Objects.equals(this.name, name)
			&& Objects.equals(this.latitude, latitude)
			&& Objects.equals(this.longitude, longitude)
			&& Objects.equals(this.radius, radius)
			&& Objects.equals(this.riskScore, riskScore)
			&& this.riskLevel == riskLevel
			&& Objects.equals(this.accidentCount, accidentCount)
			&& Objects.equals(this.modelVersion, modelVersion)) {
			return false;
		}
		this.name = name;
		this.latitude = latitude;
		this.longitude = longitude;
		this.radius = radius;
		this.riskScore = riskScore;
		this.riskLevel = riskLevel;
		this.accidentCount = accidentCount;
		this.modelVersion = modelVersion;
		return true;
	}

	public Long getId() {
		return id;
	}

	public String getSourceKey() {
		return sourceKey;
	}

	public String getName() {
		return name;
	}

	public Double getLatitude() {
		return latitude;
	}

	public Double getLongitude() {
		return longitude;
	}

	public Integer getRadius() {
		return radius;
	}

	public Double getRiskScore() {
		return riskScore;
	}

	public RiskLevel getRiskLevel() {
		return riskLevel;
	}

	public Integer getAccidentCount() {
		return accidentCount;
	}

	public String getModelVersion() {
		return modelVersion;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}
}
