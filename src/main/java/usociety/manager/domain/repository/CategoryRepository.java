package usociety.manager.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import usociety.manager.domain.model.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

}
