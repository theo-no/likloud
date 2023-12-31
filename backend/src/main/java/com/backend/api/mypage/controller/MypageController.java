package com.backend.api.mypage.controller;

import com.backend.api.drawing.dto.DrawingListDto;
import com.backend.api.mypage.dto.ProfileDto;
import com.backend.api.mypage.service.MyPageListService;
import com.backend.api.mypage.service.MypageService;
import com.backend.api.nft.dto.NftGiftDto;
import com.backend.api.nft.dto.NftListResponseDto;
import com.backend.api.photo.dto.PhotoInfoResponseDto;
import com.backend.domain.accessory.dto.AccessoryDto;
import com.backend.domain.member.entity.Member;
import com.backend.domain.member.repository.MemberRepository;
import com.backend.domain.member.service.MemberService;
import com.backend.global.error.ErrorCode;
import com.backend.global.error.ErrorResponse;
import com.backend.global.error.exception.BusinessException;
import com.backend.global.resolver.memberInfo.MemberInfo;
import com.backend.global.resolver.memberInfo.MemberInfoDto;
import com.backend.global.util.CustomApi;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Slf4j
@Tag(name = "Mypage", description = "마이페이지 관련 api")
@RestController
@RequestMapping("/api/mypage")
@RequiredArgsConstructor
public class MypageController {

    private final MypageService mypageService;
    private final MemberRepository memberRepository;
    private final MemberService memberService;
    private final MyPageListService myPageListService;

    @Operation(summary = "마이페이지 홈", description = "마이페이지 홈에 들어가면 필요한 정보 반환을 수행합니다.\n\n"+"\n\n### [ 수행절차 ]\n\n"+"- try it out 해주세요\n\n")
    @CustomApi
    @GetMapping("/home")
    public ResponseEntity<ProfileDto> getMyInfo(@MemberInfo MemberInfoDto memberInfoDto) {
        try {
            String email = memberInfoDto.getEmail();

            ProfileDto profileDto = mypageService.getMyInfo(email);
            return ResponseEntity.ok(profileDto);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(summary = "닉네임 수정", description = "프로필의 닉네임을 수정합니다."+"\n\n### [ 수행절차 ]\n\n"+"- 바꿀 닉네임 값을 넣어주세요\n\n"+"- Execute 해주세요\n\n")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "#### 성공"), @ApiResponse(responseCode = "에러", description = "#### 에러 이유를 확인 하십시오", content =@Content(schema = @Schema(implementation = ErrorResponse.class), examples = { @ExampleObject(name="400_User-003", value ="이미 등록된 닉네임입니다. 다른 닉네임을 기입해 주세요"), @ExampleObject( name = "400_User-004", value = "해당 회원은 존재하지 않습니다. 회원가입 해주거나 멤버테이블에 있는 다른 member의 토큰 값을 넣어주세요"), @ExampleObject( name = "401_Auth-001", value = "토큰이 만료되었습니다. 토큰을 재발급 받아주세요"), @ExampleObject( name = "401_Auth-004", value = "해당 토큰은 ACCESS TOKEN이 아닙니다. 토큰값이 추가정보 기입에서 받은 new token 값이 맞는지 확인해주세요"), @ExampleObject( name = "401_Auth-005", value = "해당 토큰은 유효한 토큰이 아닙니다. 추가정보 기입에서 받은 new token 값을 넣어주세요"), @ExampleObject( name = "401_Auth-006", value = "Authorization Header가 없습니다. 자물쇠에 access token값을 넣어주세요."), @ExampleObject( name = "403_Auth-009", value = "회원이 아닙니다. 추가정보로 이동하여 추가정보를 입력해 주세요."), @ExampleObject( name = "500", value = "서버에러")}))})
    @PutMapping("/nickname")
    public ResponseEntity<ProfileDto> editNickname(@RequestParam String nickname, @MemberInfo MemberInfoDto memberInfoDto) {
        // 닉네임 중복 검사
        Optional<Member> findByNickname = memberRepository.findByNickname(nickname);
        if (findByNickname.isPresent()) {
            throw new BusinessException(ErrorCode.ALREADY_REGISTERED_NICKNAME);
        }
        Member member = memberService.findMemberByEmail(memberInfoDto.getEmail());
        ProfileDto profileDto = mypageService.editNickname(member,nickname);
        return ResponseEntity.ok(profileDto);
    }

    @Operation(summary = "프로필 캐릭터 수정", description = "프로필 얼굴, 색깔, 아이템 수정 메서드입니다."+"\n\n### [ 수행절차 ]\n\n"+"- 바꾸고 싶은 profile 정보를 넣어 주세요 (response body는 아래에 예시값의 request값만 사용해주세요.)\n\n"+
            "- Execute 해주세요. \n\n"+"- 값을 모두 채워주세요\n\n"+"- 0 또는 \"\" 값으로 넣으면 아무것도 선택이 안된 경우입니다.")
    @CustomApi
    @PutMapping("/profile")
    public ResponseEntity<ProfileDto> editProfile(@RequestBody ProfileDto.editRequest request, @MemberInfo MemberInfoDto memberInfoDto) {
        String email = memberInfoDto.getEmail();
        System.out.println(email);
        ProfileDto profileDto = mypageService.editProfile(email,request);
        return ResponseEntity.ok(profileDto);
    }

    @Operation(summary = "나의 좋아요 그림 조회", description = "유저가 좋아요한 그림 리스트를 출력합니다."+"\n\n### [ 참고사항 ]\n\n"+"- try it out 해주세요\n\n")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "#### 성공"), @ApiResponse(responseCode = "에러", description = "#### 에러 이유를 확인 하십시오", content =@Content(schema = @Schema(implementation = ErrorResponse.class), examples = {@ExampleObject( name = "400_User-004", value = "해당 회원은 존재하지 않습니다. 회원가입 해주거나 멤버테이블에 있는 다른 member의 토큰 값을 넣어주세요"), @ExampleObject( name = "401_Auth-001", value = "토큰이 만료되었습니다. 토큰을 재발급 받아주세요"),@ExampleObject( name = "401_Auth-004", value = "해당 토큰은 ACCESS TOKEN이 아닙니다. 토큰값이 추가정보 기입에서 받은 new token 값이 맞는지 확인해주세요"), @ExampleObject( name = "401_Auth-005", value = "해당 토큰은 유효한 토큰이 아닙니다. 추가정보 기입에서 받은 new token 값을 넣어주세요"), @ExampleObject( name = "401_Auth-006", value = "Authorization Header가 없습니다. 자물쇠에 access token값을 넣어주세요."), @ExampleObject( name = "403_Auth-009", value = "회원이 아닙니다. 추가정보로 이동하여 추가정보를 입력해 주세요."), @ExampleObject( name = "404_Drawing-001", value = "그림을 찾을 수 없습니다."), @ExampleObject( name = "500", value = "서버에러")}))})
    @GetMapping("/likes")
    public ResponseEntity<List<DrawingListDto>> getMyLikes(@MemberInfo MemberInfoDto memberInfoDto){
        try {
            Member member = memberService.findMemberByEmail(memberInfoDto.getEmail());
            List<DrawingListDto> result = myPageListService.likeDrawing(member.getMemberId());
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(summary = "내가 그린 그림 조회", description = "유저가 그린 그림 리스트를 출력합니다."+"\n\n### [ 참고사항 ]\n\n"+"- try it out 해주세요\n\n")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "#### 성공"), @ApiResponse(responseCode = "에러", description = "#### 에러 이유를 확인 하십시오", content =@Content(schema = @Schema(implementation = ErrorResponse.class), examples = {@ExampleObject( name = "400_User-004", value = "해당 회원은 존재하지 않습니다. 회원가입 해주거나 멤버테이블에 있는 다른 member의 토큰 값을 넣어주세요"), @ExampleObject( name = "401_Auth-001", value = "토큰이 만료되었습니다. 토큰을 재발급 받아주세요"), @ExampleObject( name = "401_Auth-004", value = "해당 토큰은 ACCESS TOKEN이 아닙니다. 토큰값이 추가정보 기입에서 받은 new token 값이 맞는지 확인해주세요"), @ExampleObject( name = "401_Auth-005", value = "해당 토큰은 유효한 토큰이 아닙니다. 추가정보 기입에서 받은 new token 값을 넣어주세요"), @ExampleObject( name = "401_Auth-006", value = "Authorization Header가 없습니다. 자물쇠에 access token값을 넣어주세요."), @ExampleObject( name = "403_Auth-009", value = "회원이 아닙니다. 추가정보로 이동하여 추가정보를 입력해 주세요."), @ExampleObject( name = "404_Drawing-001", value = "그림을 찾을 수 없습니다."), @ExampleObject( name = "500", value = "서버에러")}))})
    @GetMapping("/drawings")
    public ResponseEntity<List<DrawingListDto>> getMyDrawings(@MemberInfo MemberInfoDto memberInfoDto) {
        try {
            Member member = memberService.findMemberByEmail(memberInfoDto.getEmail());
            List<DrawingListDto> result = myPageListService.getMyDrawing(member.getMemberId());
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @Operation(summary = "나의 북마크 사진 조회", description = "유저가 북마크한 사진 리스트를 출력합니다."+"\n\n### [ 참고사항 ]\n\n"+"- try it out 해주세요\n\n")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "#### 성공"), @ApiResponse(responseCode = "에러", description = "#### 에러 이유를 확인 하십시오", content =@Content(schema = @Schema(implementation = ErrorResponse.class), examples = {@ExampleObject( name = "400_User-004", value = "해당 회원은 존재하지 않습니다. 회원가입 해주거나 멤버테이블에 있는 다른 member의 토큰 값을 넣어주세요"), @ExampleObject( name = "401_Auth-001", value = "토큰이 만료되었습니다. 토큰을 재발급 받아주세요"), @ExampleObject( name = "401_Auth-004", value = "해당 토큰은 ACCESS TOKEN이 아닙니다. 토큰값이 추가정보 기입에서 받은 new token 값이 맞는지 확인해주세요"), @ExampleObject( name = "401_Auth-005", value = "해당 토큰은 유효한 토큰이 아닙니다. 추가정보 기입에서 받은 new token 값을 넣어주세요"), @ExampleObject( name = "401_Auth-006", value = "Authorization Header가 없습니다. 자물쇠에 access token값을 넣어주세요."), @ExampleObject( name = "403_Auth-009", value = "회원이 아닙니다. 추가정보로 이동하여 추가정보를 입력해 주세요."), @ExampleObject( name = "404_Photo-001", value = "사진을 찾을 수 없습니다."), @ExampleObject( name = "500", value = "서버에러")}))})
    @GetMapping("/bookmarks")
    public ResponseEntity<List<PhotoInfoResponseDto>> getMyBookmarks(@MemberInfo MemberInfoDto memberInfoDto){
        try {
            Member member = memberService.findMemberByEmail(memberInfoDto.getEmail());
            List<PhotoInfoResponseDto> result = myPageListService.bookmarkPhoto(member.getMemberId());
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(summary = "내가 올린 사진 조회", description = "유저가 올린 사진 리스트를 출력합니다."+"\n\n### [ 참고사항 ]\n\n"+"- try it out 해주세요\n\n")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "#### 성공"), @ApiResponse(responseCode = "에러", description = "#### 에러 이유를 확인 하십시오", content =@Content(schema = @Schema(implementation = ErrorResponse.class), examples = {@ExampleObject( name = "400_User-004", value = "해당 회원은 존재하지 않습니다. 회원가입 해주거나 멤버테이블에 있는 다른 member의 토큰 값을 넣어주세요"), @ExampleObject( name = "401_Auth-001", value = "토큰이 만료되었습니다. 토큰을 재발급 받아주세요"), @ExampleObject( name = "401_Auth-004", value = "해당 토큰은 ACCESS TOKEN이 아닙니다. 토큰값이 추가정보 기입에서 받은 new token 값이 맞는지 확인해주세요"), @ExampleObject( name = "401_Auth-005", value = "해당 토큰은 유효한 토큰이 아닙니다. 추가정보 기입에서 받은 new token 값을 넣어주세요"), @ExampleObject( name = "401_Auth-006", value = "Authorization Header가 없습니다. 자물쇠에 access token값을 넣어주세요."), @ExampleObject( name = "403_Auth-009", value = "회원이 아닙니다. 추가정보로 이동하여 추가정보를 입력해 주세요."), @ExampleObject( name = "404_Photo-001", value = "사진을 찾을 수 없습니다."), @ExampleObject( name = "500", value = "서버에러")}))})
    @GetMapping("/photos")
    public ResponseEntity<List<PhotoInfoResponseDto>> getMyPhotos(@MemberInfo MemberInfoDto memberInfoDto){
        try {
            Member member = memberService.findMemberByEmail(memberInfoDto.getEmail());
            List<PhotoInfoResponseDto> result = myPageListService.getMyPhoto(member.getMemberId());
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(summary = "내가 가진 악세사리 조회", description = "본인이 가진 악세사리 목록을 출력합니다."+"\n\n### [ 참고사항 ]\n\n"+"- try it out 해주세요\n\n")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "#### 성공"), @ApiResponse(responseCode = "에러", description = "#### 에러 이유를 확인 하십시오", content =@Content(schema = @Schema(implementation = ErrorResponse.class), examples = {@ExampleObject( name = "400_User-004", value = "해당 회원은 존재하지 않습니다. 회원가입 해주거나 멤버테이블에 있는 다른 member의 토큰 값을 넣어주세요"), @ExampleObject( name = "401_Auth-001", value = "토큰이 만료되었습니다. 토큰을 재발급 받아주세요"), @ExampleObject( name = "401_Auth-004", value = "해당 토큰은 ACCESS TOKEN이 아닙니다. 토큰값이 추가정보 기입에서 받은 new token 값이 맞는지 확인해주세요"), @ExampleObject( name = "401_Auth-005", value = "해당 토큰은 유효한 토큰이 아닙니다. 추가정보 기입에서 받은 new token 값을 넣어주세요"), @ExampleObject( name = "401_Auth-006", value = "Authorization Header가 없습니다. 자물쇠에 access token값을 넣어주세요."), @ExampleObject( name = "403_Auth-009", value = "회원이 아닙니다. 추가정보로 이동하여 추가정보를 입력해 주세요."), @ExampleObject( name = "404_Acce-001", value = "악세사리를 찾을 수 없습니다."), @ExampleObject( name = "500", value = "서버에러")}))})
    @GetMapping("/accessory")
    public ResponseEntity<List<AccessoryDto>> getMyAccessorys (@MemberInfo MemberInfoDto memberInfoDto){
        try{
            Member member = memberService.findMemberByEmail(memberInfoDto.getEmail());
            List<AccessoryDto> result = myPageListService.getMyAccessory(member);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(summary = "내가 만든 NFT 조회", description = "본인이 발급한 NFT를 조회합니다.")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "#### 성공"), @ApiResponse(responseCode = "에러", description = "#### 에러 이유를 확인 하십시오", content =@Content(schema = @Schema(implementation = ErrorResponse.class), examples = {@ExampleObject( name = "400_User-004", value = "해당 회원은 존재하지 않습니다. 회원가입 해주거나 멤버테이블에 있는 다른 member의 토큰 값을 넣어주세요"), @ExampleObject( name = "401_Auth-001", value = "토큰이 만료되었습니다. 토큰을 재발급 받아주세요"), @ExampleObject( name = "401_Auth-004", value = "해당 토큰은 ACCESS TOKEN이 아닙니다. 토큰값이 추가정보 기입에서 받은 new token 값이 맞는지 확인해주세요"), @ExampleObject( name = "401_Auth-005", value = "해당 토큰은 유효한 토큰이 아닙니다. 추가정보 기입에서 받은 new token 값을 넣어주세요"), @ExampleObject( name = "401_Auth-006", value = "Authorization Header가 없습니다. 자물쇠에 access token값을 넣어주세요."), @ExampleObject( name = "403_Auth-009", value = "회원이 아닙니다. 추가정보로 이동하여 추가정보를 입력해 주세요."), @ExampleObject( name = "404_NFT-001", value = "지갑을 찾을 수 없습니다."), @ExampleObject( name = "500", value = "서버에러")}))})
    @GetMapping("/nft")
    public ResponseEntity<List<NftListResponseDto>> getMyNft (@MemberInfo MemberInfoDto memberInfoDto){
        try{
            Member member = memberService.findMemberByEmail(memberInfoDto.getEmail());
            List<NftListResponseDto> result = myPageListService.getMyNft(member.getMemberId());
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(summary = "내 선물함 조회", description = "받은 NFT선물을 조회 합니다.")
    @GetMapping("/gift")
    public ResponseEntity<List<NftGiftDto>> getMyGift (@MemberInfo MemberInfoDto memberInfoDto){
        try{
            Member member = memberService.findMemberByEmail(memberInfoDto.getEmail());
            List<NftGiftDto> result = myPageListService.getMyGift(member.getMemberId());
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // 선물 수락
    @PostMapping("/gift/{transferId}/accept/{nftId}")
    @Operation(summary = "선물 수락", description = "선물을 수락합니다. 받은 선물을 내 NFT리스트에 추가됩니다.")
    public NftListResponseDto acceptGift (@PathVariable Long transferId, @PathVariable Long nftId, @MemberInfo MemberInfoDto memberInfoDto) {
        Member member = memberService.findMemberByEmail(memberInfoDto.getEmail());

        return mypageService.acceptGift(transferId, nftId, member);
    }


    // 선물 거절(토큰 소멸)
    @PostMapping("/gift/{transferId}/reject/{nftId}")
    @Operation(summary = "선물 거절", description = "선물을 거절합니다. 거절한 선물은 소멸됩니다.")
    public ResponseEntity<String> rejectGift(@PathVariable Long transferId, @PathVariable Long nftId, @MemberInfo MemberInfoDto memberInfoDto) {
        Member member = memberService.findMemberByEmail(memberInfoDto.getEmail());

        mypageService.rejectGift(transferId, nftId, member);

        return ResponseEntity.ok(String.format("%d번 NFT 소멸,,,", nftId));
    }

}
