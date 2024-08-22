package com.xtramile.library2024.web.rest.vm;

import com.xtramile.library2024.domain.enumeration.BookType;
import com.xtramile.library2024.domain.enumeration.Genre;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class BookVM {

    private Long id;

    private String title;

    private BookType type;

    private Genre genre;

    private Integer year;

    private Integer totalPage;

    private String author;

    private String cover;

    private String synopsis;

    private Long bookStorageId;

    private Integer quantity;
}
