package com.xtramile.library2024.web.rest.vm;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class LibraryVM {

    public Long id;
    public String name;
}
