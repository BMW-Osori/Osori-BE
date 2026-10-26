package com.bmw.osori.global.geo;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class GeoDistanceTest {

	@Test
	void 같은_좌표의_거리는_0미터다() {
		assertThat(GeoDistance.inMeters(37.5665, 126.978, 37.5665, 126.978)).isZero();
	}

	@Test
	void 적도에서_경도_1도의_거리를_미터로_계산한다() {
		double distance = GeoDistance.inMeters(0, 0, 0, 1);

		assertThat(distance).isCloseTo(111_194.93, org.assertj.core.data.Offset.offset(0.01));
		assertThat(GeoDistance.inMeters(0, 1, 0, 0)).isEqualTo(distance);
	}
}
