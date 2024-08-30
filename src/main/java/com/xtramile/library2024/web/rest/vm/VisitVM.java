package com.xtramile.library2024.web.rest.vm;

import com.xtramile.library2024.domain.enumeration.BookType;
import com.xtramile.library2024.domain.enumeration.Genre;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class VisitVM {

    Long id;
    private Long bookId;

    private String title;

    private BookType type;

    private String author;

    private String cover;

    private Long visitorBookStorageId;

    private LocalDate borrowDate;

    private LocalDate returnDate;

    private Long librarianId;

    private String librarianName;

    private Long visitorId;

    private String visitorName;

    private Integer quantity;
}
