package com.engly.engly_server.repo;

import com.engly.engly_server.models.entity.AdditionalInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdditionalInfoRepo extends JpaRepository<AdditionalInfo, String> {
}
