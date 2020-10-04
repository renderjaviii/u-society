package usociety.manager.domain.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import usociety.manager.domain.model.Group;

@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {

    Optional<Group> findByName(String name);

    List<Group> findByCategoryIdAndNameContainingIgnoreCase(Long categoryId, String name);

    List<Group> findByCategoryIdOrNameContainingIgnoreCase(Long categoryId, String name);

}
