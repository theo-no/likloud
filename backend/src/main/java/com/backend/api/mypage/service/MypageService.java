package com.backend.api.mypage.service;

import com.backend.api.drawing.dto.DrawingListDto;
import com.backend.api.drawing.service.DrawingViewService;
import com.backend.api.mypage.dto.MypageInfoDto;
import com.backend.api.mypage.dto.ProfileDto;
import com.backend.domain.likes.entity.Likes;
import com.backend.domain.likes.repository.LikesRepository;
import com.backend.domain.member.entity.Member;
import com.backend.domain.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class MypageService {

    private final MemberService memberService;
    private final LikesRepository likesRepository;
    private final DrawingViewService drawingViewService;

    @Transactional(readOnly = true)
    public MypageInfoDto getMyInfo(String email) {
        Member member = memberService.findMemberByEmail(email);
        return MypageInfoDto.of(member);
    }

    @Transactional(readOnly = true)
    public ProfileDto getMyProfile(String email) {
        Member member = memberService.findMemberByEmail(email);
        return ProfileDto.of(member);
    }

    @Transactional
    public MypageInfoDto editNickname(String email, String nickname){
        Member member = memberService.findMemberByEmail(email);
        member.editNickname(nickname);
        return MypageInfoDto.of(member);
    }

    @Transactional
    public ProfileDto editProfile(String email, ProfileDto.editRequest request){
        Member member = memberService.findMemberByEmail(email);
        member.editProfile(request.getNickname(),request.getProfileFace(), request.getProfileColor(), request.getProfileAccessory());
        return ProfileDto.of(member);

    }

    public List<DrawingListDto> likeDrawing(Long memberId){
        List<Likes> list = likesRepository.findAllByMemberMemberId(memberId);

//        drawings.stream()
//                .map(drawing -> {
//                    boolean memberLiked = likesRepository.existsByMemberMemberIdAndDrawingDrawingId(memberId, drawing.getDrawingId());
//                    return new DrawingListDto(drawing, memberLiked);
//                })
//                .collect(Collectors.toList());
        return list.stream()
                    .map(like -> {
                        boolean memberLiked = likesRepository.existsByMemberMemberIdAndDrawingDrawingId(memberId, like.getDrawing().getDrawingId());
                        return new DrawingListDto(like.getDrawing(), memberLiked);
                    })
                    .collect(Collectors.toList());
    }


}
