package com.alamobot.core.persistence;

import com.alamobot.core.domain.FormatEntity;
import org.springframework.data.repository.CrudRepository;

public interface FormatRepository extends CrudRepository<FormatEntity, String> {
}
