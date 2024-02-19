package io.oeid.mogakgo.common.base;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Getter;

@Schema(description = "커서 기반 페이지네이션 결과에 대한 일괄 응답")
@Getter
public class CursorPaginationResult<T> {

    @Schema(description = "데이터 목록")
    private List<T> data;
    @Schema(description = "다음 페이지가 있는지 여부")
    private boolean hasNext;
    @Schema(description = "현재 페이지의 데이터 수")
    private Integer numberOfElements;
    @Schema(description = "요청한 데이터 수")
    private Integer size;

    private CursorPaginationResult(List<T> data, Integer size) {
        this.data = data;
        this.size = size;
        if (data.size() > size) {
            this.hasNext = true;
            this.data.remove(data.size() - 1);
        } else {
            this.hasNext = false;
        }
        this.numberOfElements = data.size();
    }

    private CursorPaginationResult(List<T> data, Integer size, boolean hasNext) {
        this.data = data;
        this.numberOfElements = data.size();
        this.hasNext = hasNext;
        this.size = size;
    }

    public static <T> CursorPaginationResult<T> fromDataWithExtraItemForNextCheck(
        List<T> data, Integer size
    ) {
        return new CursorPaginationResult<>(data, size);
    }

    public static <T> CursorPaginationResult<T> fromDataWithHasNext(
        List<T> data, Integer size, boolean hasNext
    ) {
        return new CursorPaginationResult<>(data, size, hasNext);
    }
}
