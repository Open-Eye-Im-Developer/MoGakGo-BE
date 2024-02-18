package io.oeid.mogakgo.domain.geo.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Region {

    JONGRO("서울특별시", "종로구", 11110),
    JUNG("서울특별시", "중구", 11140),
    YONGSAN("서울특별시", "용산구", 11170),
    SEONGDONG("서울특별시", "성동구", 11200),
    KWANGJIN("서울특별시", "광진구", 11215),
    DONGDAEMUN("서울특별시", "동대문구", 11230),
    JUNGRANG("서울특별시", "중랑구", 11260),
    SEONGBUK("서울특별시", "성북구", 11290),
    KANGBUK("서울특별시", "강북구", 11305),
    DOBONG("서울특별시", "도봉구", 11320),
    NOWON("서울특별시", "노원구", 11350),
    EUNPYEONG("서울특별시", "은평구", 11380),
    SEODAEMUN("서울특별시", "서대문구", 11410),
    MAPO("서울특별시", "마포구", 11440),
    YANGCHUN("서울특별시", "양천구", 11470),
    KANGSEO("서울특별시", "강서구", 11500),
    GURO("서울특별시", "구로구", 11530),
    GEUMCHUN("서울특별시", "금천구", 11545),
    YOUNGDEUNGPO("서울특별시", "영등포구", 11560),
    DONGJAK("서울특별시", "동작구", 11590),
    KWANAK("서울특별시", "관악구", 11620),
    SEOCHO("서울특별시", "서초구", 11650),
    KANGNAM("서울특별시", "강남구", 11680),
    SONGPA("서울특별시", "송파구", 11710),
    KANGDONG("서울특별시", "강동구", 11740),
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

}

