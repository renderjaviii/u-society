package usociety.manager.domain.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import usociety.manager.domain.model.Post;

@Repository
public interface PostRepository extends JpaRepository<Post, Long>, PagingAndSortingRepository<Post, Long> {

    List<Post> findAllByGroupIdOrderByCreationDateAsc(Long groupId, Pageable pageable);

}
