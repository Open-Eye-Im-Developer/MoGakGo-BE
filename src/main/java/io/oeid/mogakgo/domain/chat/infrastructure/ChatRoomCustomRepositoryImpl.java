package io.oeid.mogakgo.domain.chat.infrastructure;

import static io.oeid.mogakgo.domain.chat.entity.document.QChatRoom.chatRoom;
import static io.oeid.mogakgo.domain.user.domain.QUser.user;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import io.oeid.mogakgo.domain.chat.application.dto.res.ChatRoomPublicRes;
import io.oeid.mogakgo.domain.chat.application.vo.ChatUserInfo;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ChatRoomCustomRepositoryImpl implements ChatRoomCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;

    public List<ChatRoomPublicRes> findAllChatRoomByUserId(Long userId) {
        return jpaQueryFactory.select(
                Projections.constructor(
                    ChatRoomPublicRes.class,
                    chatRoom.project.id,
                    chatRoom.id,
                    null,
                    null,
                    chatRoom.status,
                    Projections.list(List.of(
                            Projections.constructor(
                                ChatUserInfo.class,
                                chatRoom.creator.id,
                                chatRoom.creator.username,
                                chatRoom.creator.avatarUrl
                            ),
                            Projections.constructor(
                                ChatUserInfo.class,
                                chatRoom.sender.id,
                                chatRoom.sender.username,
                                chatRoom.sender.avatarUrl
                            )
                        )
                    )
                )
            ).from(chatRoom).join(chatRoom.creator, user).join(chatRoom.sender, user)
            .where(chatRoom.creator.id.eq(userId).or(chatRoom.sender.id.eq(userId)))
            .fetch();
    }
}
