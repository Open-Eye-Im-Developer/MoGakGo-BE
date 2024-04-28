package io.oeid.mogakgo.domain.chat.entity.vo;

import io.oeid.mogakgo.domain.user.domain.User;


public record ChatUserInfo(Long userId, String username, String avatarUrl) {

    public ChatUserInfo(User user) {
        this(user.getId(), user.getUsername(), user.getAvatarUrl());
    }
}
