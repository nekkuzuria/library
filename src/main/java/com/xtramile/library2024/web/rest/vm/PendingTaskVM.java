package com.xtramile.library2024.web.rest.vm;

import com.xtramile.library2024.domain.enumeration.BookType;
import com.xtramile.library2024.domain.enumeration.PendingTaskStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class PendingTaskVM {

    public Long id;
    public PendingTaskStatus status;
    public Long visitorId;
    public String visitorName;
    public Long bookId;
    public String bookTitle;
    public BookType bookType;
    public String bookCover;
    public String bookAuhor;
    public Integer quantity;
}
