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
public class VisitorBookStorageVM {

    private Long id;

    private Long bookId;

    private String title;

    private BookType type;

    private Genre genre;

    private Integer year;

    private Integer totalPage;

    private String author;

    private String cover;

    private LocalDate borrowDate;

    private LocalDate returnDate;
}
