package io.oeid.mogakgo.domain.chat.infrastructure;

import io.oeid.mogakgo.domain.chat.entity.document.ChatRoom;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, String> {

    @Query("select c from ChatRoom c where c.creator.id = ?1 or c.sender.id = ?1 order by c.id DESC")
    List<ChatRoom> findAllByUserId(Long id);
}
