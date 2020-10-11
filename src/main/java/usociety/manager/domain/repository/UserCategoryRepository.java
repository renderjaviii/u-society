package usociety.manager.domain.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import usociety.manager.domain.model.UserCategory;

@Repository
public interface UserCategoryRepository extends JpaRepository<UserCategory, Long> {

    List<UserCategory> findAllByUserId(Long userId);

    List<UserCategory> findAllByCategoryIdAndUserIdIsNot(Long categoryId, Long userId);

}
