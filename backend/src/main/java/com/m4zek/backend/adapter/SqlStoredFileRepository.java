package com.m4zek.backend.adapter;

import com.m4zek.backend.model.StoredFile;
import com.m4zek.backend.repository.StoredFileRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SqlStoredFileRepository extends StoredFileRepository, JpaRepository<StoredFile, Integer> {

}
