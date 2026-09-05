package com.gcc.impossible.geocoding;

import java.util.Optional;

/**
 * 가맹점 마스터에 없는 가게의 좌표를 상호명/주소로 보완 조회하는 인터페이스.
 * 시드 12곳은 이미 좌표가 있으므로, 이 인터페이스는 마스터 밖 가게용.
 */
public interface GeocodingClient {

    Optional<GeoPoint> geocode(String query);
}
