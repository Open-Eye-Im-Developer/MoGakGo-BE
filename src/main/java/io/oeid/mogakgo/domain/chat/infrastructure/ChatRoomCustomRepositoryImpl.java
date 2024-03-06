package io.oeid.mogakgo.domain.chat.infrastructure;


import static io.oeid.mogakgo.domain.chat.entity.QChatRoom.chatRoom;
import static io.oeid.mogakgo.domain.chat.entity.QChatUser.chatUser;
import static io.oeid.mogakgo.domain.project.domain.entity.QProject.project;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import io.oeid.mogakgo.domain.chat.application.dto.res.ChatRoomDataRes;
import io.oeid.mogakgo.domain.chat.application.vo.ChatUserInfo;
import io.oeid.mogakgo.domain.chat.entity.ChatRoom;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ChatRoomCustomRepositoryImpl implements ChatRoomCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public ChatRoomDataRes getChatDetailData(Long userId, UUID chatRoomId) {
        return jpaQueryFactory
            .select(
                Projections.constructor(
                    ChatRoomDataRes.class,
                    chatUser.chatRoom.project.meetingInfo,
                    Projections.constructor(
                        ChatUserInfo.class,
                        chatUser.user.id,
                        chatUser.user.username,
                        chatUser.user.avatarUrl
                    )
                )
            )
            .from(chatUser)
            .leftJoin(chatUser.chatRoom, chatRoom)
            .leftJoin(chatUser.chatRoom.project, project)
            .where(chatUser.chatRoom.id.eq(chatRoomId).and(chatUser.user.id.ne(userId)))
            .fetchOne();
    }

    @Override
    public List<ChatRoom> getChatRoomList(Long userId, Long cursorId, int pageSize) {
        return jpaQueryFactory
            .select(chatUser.chatRoom)
            .from(chatUser)
            .leftJoin(chatUser.chatRoom, chatRoom)
            .where(
                cursorIdCondition(cursorId),
                userIdEq(userId)
            )
            .orderBy(chatRoom.cursorId.desc())
            .limit(pageSize + 1L)
            .fetch();
    }

    private BooleanExpression cursorIdCondition(Long cursorId) {
        return cursorId == null ? null : chatRoom.cursorId.lt(cursorId);
    }

    private BooleanExpression userIdEq(Long userId) {
        return userId == null ? null : chatUser.user.id.eq(userId).and(chatUser.availableYn.isTrue());
    }
}
