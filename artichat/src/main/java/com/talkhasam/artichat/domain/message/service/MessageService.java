package com.talkhasam.artichat.domain.message.service;

import com.talkhasam.artichat.domain.message.dto.MessageDto;
import com.talkhasam.artichat.domain.message.dto.MessageListResponseDto;
import com.talkhasam.artichat.domain.message.entity.Message;
import com.talkhasam.artichat.domain.message.repository.MessageRepository;
import com.talkhasam.artichat.global.common.PageResult;
import com.talkhasam.artichat.global.exception.CustomException;
import com.talkhasam.artichat.global.exception.ErrorCode;
import com.talkhasam.artichat.global.security.CustomTokenService;
import jakarta.annotation.Nullable;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MessageService {
    private final MessageRepository messageRepository;
    private final CustomTokenService customTokenService;

    public MessageListResponseDto getMessageListAll(long chatRoomId, int limit, @Nullable Long startId) {
        PageResult<Message> page = messageRepository.findByChatRoomId(chatRoomId, limit, startId);
        List<MessageDto> messages = page.getItems().stream()
                .map(MessageDto::from)
                .collect(Collectors.toList());
        Long nextKey = page.getNextKey();
        return new MessageListResponseDto(messages, nextKey);
    }

    public MessageListResponseDto getMessageListByIsOwnerStatus(
            long chatRoomId, int limit, @Nullable Long startId
    ) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                .getRequest();
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken == null || !bearerToken.startsWith("Bearer ")) {
            throw new CustomException(ErrorCode.INVALID_ACCESS_TOKEN);
        }
        String token = bearerToken.substring(7);
        Long loginUserId = Long.parseLong(customTokenService.extractUsername(token));
        boolean loginUserisOwner = customTokenService.extractIsOwner(token);
        List<Message> messages;
        Long nextKey;

        if (loginUserisOwner) { // isOwner=true라면 전체 메시지 조회
            PageResult<Message> page = messageRepository.findByChatRoomId(chatRoomId, limit, startId);
            messages = page.getItems();
            nextKey = page.getNextKey();
        } else {
            // isOwner=true인 메시지와, isOwner=false & chatUserId = loginUserId 메시지 조회 (페이징 처리 필요)
            PageResult<Message> pageTrueOwner = messageRepository.findByChatRoomIdAndIsOwnerTrue(chatRoomId, limit, startId);
            PageResult<Message> pageFalseOwnerMine = messageRepository.findByChatUserId(loginUserId, limit, startId);

            // 메시지 병합 및 정렬 (최신순)
            List<Message> merged = Stream.concat(pageTrueOwner.getItems().stream(), pageFalseOwnerMine.getItems().stream())
                    .sorted(Comparator.comparing(Message::getCreatedAt).reversed())
                    .collect(Collectors.toList());

            // 병합 후 nextKey 계산
            messages = merged.size() > limit ? merged.subList(0, limit) : merged;
            nextKey = calculateNextKey(messages);
        }
        List<MessageDto> messageDtos = messages.stream()
                .map(MessageDto::from)
                .collect(Collectors.toList());
        return new MessageListResponseDto(messageDtos, nextKey);
    }

    private Long calculateNextKey(List<Message> messages) {
        if (messages == null || messages.isEmpty()) {
            return null; // 다음 페이지 없음
        }
        // 마지막 메시지의 ID 반환
        return messages.getLast().getId();
    }
}
