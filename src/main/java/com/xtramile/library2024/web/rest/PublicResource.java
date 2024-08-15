package com.xtramile.library2024.web.rest;

import com.xtramile.library2024.service.LibraryService;
import com.xtramile.library2024.web.rest.vm.LibraryVM;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/public")
public class PublicResource {

    private static final Logger log = LoggerFactory.getLogger(LibraryResource.class);
    private final LibraryService libraryService;

    public PublicResource(LibraryService libraryService) {
        this.libraryService = libraryService;
    }

    @GetMapping("/libraries")
    public ResponseEntity<List<LibraryVM>> getAllPublicLibraries() {
        log.debug("REST request to get public Libraries");
        List<LibraryVM> libraries = libraryService.getAllPublicLibraries();
        return ResponseEntity.ok().body(libraries);
    }
}
