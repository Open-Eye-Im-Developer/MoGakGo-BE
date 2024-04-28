package io.oeid.mogakgo.common.base;

import jakarta.validation.constraints.Min;
import java.util.Objects;
import lombok.Getter;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.lang.Nullable;

@Getter
public class CursorPaginationInfoReq {

    private final Long cursorId;
    @Min(1)
    private final int pageSize;

    private final Direction sortOrder;

    public CursorPaginationInfoReq(@Nullable Long cursorId, int pageSize,
        @Nullable Direction sortOrder) {
        this.cursorId = cursorId;
        this.pageSize = pageSize;
        // 최근순 기본
        this.sortOrder = Objects.requireNonNullElse(sortOrder, Direction.DESC);
    }
}
