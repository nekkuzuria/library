package com.xtramile.library2024.repository;

import com.xtramile.library2024.domain.PendingTask;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

@Repository
public interface PendingTaskRepository extends JpaRepository<PendingTask, Long> {}
