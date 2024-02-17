package io.oeid.mogakgo.domain.geo.feign.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;

@Getter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class AddressDocument {

    private Character regionType;
    private String code;
    private String addressName;
    private double x; // longitude
    private double y; // latitude
}
