
package com.wojet.pmtool.payload;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PagedResponse<T> {
    private List<T> content;
    private Integer pageNumber;
    private Integer pageSize;
    private Long totalElements;
    private Integer totalPages;
    private Boolean lastPage;
}