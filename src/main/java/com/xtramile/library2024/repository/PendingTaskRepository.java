package com.xtramile.library2024.repository;

import com.xtramile.library2024.domain.Library;
import com.xtramile.library2024.domain.PendingTask;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

@Repository
public interface PendingTaskRepository extends JpaRepository<PendingTask, Long> {
    Page<PendingTask> findByLibrary(Library library, Pageable pageable);
}
