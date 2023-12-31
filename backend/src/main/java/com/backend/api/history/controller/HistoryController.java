package com.backend.api.history.controller;

import com.backend.api.history.dto.HistoryDto;
import com.backend.api.history.service.HistoryService;
import com.backend.domain.member.entity.Member;
import com.backend.domain.member.service.MemberService;
import com.backend.global.resolver.memberInfo.MemberInfo;
import com.backend.global.resolver.memberInfo.MemberInfoDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Tag(name = "History", description = "히스토리 관련 api")
@RestController
@RequestMapping("/api/history")
@RequiredArgsConstructor
public class HistoryController {
    private final HistoryService historyService;
    private final MemberService memberService;
    
    // 히스토리 목록 보기
    @Operation(summary = "내 히스토리", description = "나의 알림 히스토리목록을 보여주는 메소드입니다")
    @GetMapping("/")
    public ResponseEntity<List<HistoryDto>> getMyHistory(@MemberInfo MemberInfoDto memberInfoDto) {
        try{
            Member member = memberService.findMemberByEmail(memberInfoDto.getEmail());
            List<HistoryDto> result = historyService.getMyHistory(member.getMemberId());
            return ResponseEntity.ok(result);
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();

        }
    }

    // 삭제기능
    @Operation(summary = "내 히스토리 삭제", description = "내 히스토리 중 특정 하나를 삭제하는 메소드입니다")
    @DeleteMapping("/delete/{historyId}")
    public ResponseEntity<String> deleteHistory(@PathVariable Long historyId, @MemberInfo MemberInfoDto memberInfoDto){
        Member member = memberService.findMemberByEmail(memberInfoDto.getEmail());
        historyService.deleteHistory(historyId, member.getMemberId());
        return ResponseEntity.ok(String.format("%d번 히스토리 삭제", historyId));
    }


}
