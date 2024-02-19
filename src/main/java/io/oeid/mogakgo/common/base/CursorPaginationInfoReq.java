package io.oeid.mogakgo.common.base;

import jakarta.validation.constraints.NotNull;
import java.util.Objects;
import lombok.Getter;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.lang.Nullable;

@Getter
public class CursorPaginationInfoReq {

    @Nullable
    private final Long cursorId;

    @NotNull
    private final int pageSize;

    @Nullable
    private final Sort.Direction sortOrder;

    public CursorPaginationInfoReq(Long cursorId, int pageSize, Direction sortOrder) {
        this.cursorId = cursorId;
        this.pageSize = pageSize;
        this.sortOrder = Objects.requireNonNullElse(sortOrder, Direction.ASC);
    }
}
