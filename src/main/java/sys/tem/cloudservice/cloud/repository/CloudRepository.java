package sys.tem.cloudservice.cloud.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sys.tem.cloudservice.cloud.model.entity.FileData;

import java.util.Optional;

@Repository
public interface CloudRepository extends JpaRepository<FileData, Long> {

    void deleteFileDataByFilename(String filename);

    Optional<FileData> findFileDataByFilename(String filename);

    boolean existsByFilename(String s);
}
