package usociety.manager.domain.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import usociety.manager.domain.model.React;

@Repository
public interface ReactRepository extends JpaRepository<React, Long> {

    Optional<React> findAllByPostIdAndUserId(Long postId, Long userId);

    List<React> findAllByPostId(Long postId);

}
