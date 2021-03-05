package usociety.manager.domain.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import usociety.manager.domain.model.UserGroup;

@Repository
public interface UserGroupRepository extends JpaRepository<UserGroup, Long> {

    List<UserGroup> findAllByUserIdAndStatusIn(Long userId, List<Integer> validStatuses);

    Optional<UserGroup> findByGroupIdAndUserId(Long groupId, Long userId);

    Optional<UserGroup> findByGroupIdAndUserIdAndStatus(Long groupId, Long userId, int status);

    List<UserGroup> findAllByGroupIdAndUserIdNot(Long groupId, Long userId);

    Optional<UserGroup> findByGroupIdAndIsAdmin(Long groupId, boolean isAdmin);

}
