package io.oeid.mogakgo.common.base;

import jakarta.validation.constraints.NotNull;
import java.util.Objects;
import lombok.Getter;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.lang.Nullable;

@Getter
public class CursorPaginationInfoReq {

    @Nullable
    private final Long cursorId;

    @NotNull
    private final int pageSize;

    @Nullable
    private final Direction sortOrder;

    public CursorPaginationInfoReq(@Nullable Long cursorId, int pageSize, Direction sortOrder) {
        this.cursorId = cursorId;
        this.pageSize = pageSize;
        // 최근순 기본
        this.sortOrder = Objects.requireNonNullElse(sortOrder, Direction.DESC);
    }
}
