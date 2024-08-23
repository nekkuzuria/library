package com.xtramile.library2024.web.rest.vm;

import com.xtramile.library2024.domain.enumeration.PendingTaskStatus;
import com.xtramile.library2024.domain.enumeration.PendingTaskType;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class PendingTaskVM {

    public Long id;

    public PendingTaskType type;

    public PendingTaskStatus status;

    public Long visitorId;

    public String visitorName;

    public Long bookId;

    public Integer quantity;

    public String bookTitle;

    public String bookAuthor;

    public Long librarianId;

    public String librarianName;

    public String reason;

    public Instant createdDate;
}
