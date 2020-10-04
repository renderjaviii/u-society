package usociety.manager.domain.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import usociety.manager.domain.model.UserGroup;

@Repository
public interface UserGroupRepository extends JpaRepository<UserGroup, Long> {

    List<UserGroup> findAllByUserIdAndStatus(Long userId, int status);

    Optional<UserGroup> findByGroupIdAndUserId(Long groupId, Long userId);

    List<UserGroup> findAllByGroupId(Long groupId);

    Optional<UserGroup> findByGroupIdAndIsAdmin(Long groupId, boolean isAdmin);

}
