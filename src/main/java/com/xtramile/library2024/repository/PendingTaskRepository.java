package com.xtramile.library2024.repository;

import com.xtramile.library2024.domain.Library;
import com.xtramile.library2024.domain.PendingTask;
import com.xtramile.library2024.domain.Visitor;
import com.xtramile.library2024.domain.VisitorBookStorage;
import com.xtramile.library2024.domain.enumeration.PendingTaskStatus;
import com.xtramile.library2024.domain.enumeration.PendingTaskType;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

@Repository
public interface PendingTaskRepository extends JpaRepository<PendingTask, Long> {
    Page<PendingTask> findByLibrary(Library library, Pageable pageable);
    Page<PendingTask> findByVisitor(Visitor visitor, Pageable pageable);
    Optional<PendingTask> findByVisitorBookStorageAndTypeAndStatus(
        VisitorBookStorage visitorBookStorage,
        PendingTaskType type,
        PendingTaskStatus status
    );
}
