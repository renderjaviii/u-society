package usociety.manager.domain.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import usociety.manager.domain.model.Message;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long>, PagingAndSortingRepository<Message, Long> {

    List<Message> findAllByGroupIdOrderByCreationDateDesc(Long groupId, Pageable pageable);

}
