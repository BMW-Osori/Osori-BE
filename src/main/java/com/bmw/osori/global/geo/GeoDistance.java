package com.bmw.osori.global.geo;

public final class GeoDistance {

	private static final double EARTH_RADIUS_IN_METERS = 6_371_000;

	private GeoDistance() {
	}

	public static double inMeters(
		double sourceLatitude,
		double sourceLongitude,
		double targetLatitude,
		double targetLongitude
	) {
		double sourceLatitudeRadians = Math.toRadians(sourceLatitude);
		double targetLatitudeRadians = Math.toRadians(targetLatitude);
		double latitudeDifference = Math.toRadians(targetLatitude - sourceLatitude);
		double longitudeDifference = Math.toRadians(targetLongitude - sourceLongitude);

		double haversine = Math.sin(latitudeDifference / 2) * Math.sin(latitudeDifference / 2)
			+ Math.cos(sourceLatitudeRadians) * Math.cos(targetLatitudeRadians)
			* Math.sin(longitudeDifference / 2) * Math.sin(longitudeDifference / 2);
		double angularDistance = 2 * Math.atan2(Math.sqrt(haversine), Math.sqrt(1 - haversine));

		return EARTH_RADIUS_IN_METERS * angularDistance;
	}
}
