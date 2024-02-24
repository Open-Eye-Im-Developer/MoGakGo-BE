package io.oeid.mogakgo.domain.geo.domain.enums;

import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Region {

    JONGNO("서울특별시", "종로구", 11110),
    JUNG("서울특별시", "중구", 11140),
    YONGSAN("서울특별시", "용산구", 11170),
    SEONGDONG("서울특별시", "성동구", 11200),
    GWANGJIN("서울특별시", "광진구", 11215),
    DONGDAEMUN("서울특별시", "동대문구", 11230),
    JUNGNANG("서울특별시", "중랑구", 11260),
    SEONGBUK("서울특별시", "성북구", 11290),
    GANGBUK("서울특별시", "강북구", 11305),
    DOBONG("서울특별시", "도봉구", 11320),
    NOWON("서울특별시", "노원구", 11350),
    EUNPYEONG("서울특별시", "은평구", 11380),
    SEODAEMUN("서울특별시", "서대문구", 11410),
    MAPO("서울특별시", "마포구", 11440),
    YANGCHEON("서울특별시", "양천구", 11470),
    GANGSEO("서울특별시", "강서구", 11500),
    GURO("서울특별시", "구로구", 11530),
    GEUMCHEON("서울특별시", "금천구", 11545),
    YOUNGDEUNGPO("서울특별시", "영등포구", 11560),
    DONGJAK("서울특별시", "동작구", 11590),
    GWANAK("서울특별시", "관악구", 11620),
    SEOCHO("서울특별시", "서초구", 11650),
    GANGNAM("서울특별시", "강남구", 11680),
    SONGPA("서울특별시", "송파구", 11710),
    GANGDONG("서울특별시", "강동구", 11740),
    BUNDANG("경기도 성남시", "분당구", 41135);
    
    private final String depth1;
    private final String depth2;
    private final int areaCode;

    public static Region getByAreaCode(int areaCode) {
        for (Region region : Region.values()) {
            if (region.getAreaCode() == areaCode) {
                return region;
            }
        }
        return null;
    }

    public static List<Region> getDefaultDensityRank() {
        return List.of(
            JONGNO, JUNG, YONGSAN, SEONGDONG, GWANGJIN, DONGDAEMUN, JUNGNANG, SEONGBUK, GANGBUK,
            DOBONG, NOWON, EUNPYEONG, SEODAEMUN, MAPO, YANGCHEON, GANGSEO, GURO, GEUMCHEON,
            YOUNGDEUNGPO, DONGJAK, GWANAK, SEOCHO, GANGNAM, SONGPA, GANGDONG
        );
    }

}

