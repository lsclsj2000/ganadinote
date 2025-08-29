package ganadinote.sns.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import ganadinote.common.domain.Member;
import ganadinote.common.domain.SnsPost;
import ganadinote.sns.domain.FollowUser;
import ganadinote.sns.domain.HomeFeedPost;

@Mapper
public interface SnsMapper {
	
	// 게시물 업로드
	int insertPost(SnsPost post);
	// 게시물 삭제 조건
	Integer selectPostOwner(@Param("spCd") Integer spCd);
	// 게시물 삭제
	int deletePost(@Param("spCd") Integer spCd);
    
    
    // myfeed - 게시물 숫자
    long countPostsByMember(Integer mbrCd);
    // myfeed - 팔로워 숫자
    long countFollowersOfMember(Integer mbrCd);
    // myfeed - 팔로우 숫자
    long countFollowingsByMember(Integer mbrCd);
    // myfeed - 게시물 대표이미지
    List<SnsPost> selectPostsByMember(Integer mbrCd);
    // myfeed- 팔로워 목록
    List<FollowUser> selectFollowersOfMember(Integer mbrCd);
    // myfeed- 팔로우 목록
    List<FollowUser> selectFollowingsByMember(Integer mbrCd);
    // myfeed - 프로필 수정 내역 조회
    Member selectMemberById(Integer mbrCd);
    // myfeed - 프로필 수정 - 닉네임 유효성 검증
    long countNickname(String mbrNknm, Integer excludeMbrCd);
    // myfeed - 프로필 수정 - 업데이트
    int updateMemberProfile(Integer mbrCd, String mbrNknm, String mbrProfile);
    // myfeed - 프로필 수정 - 비밀번호 유효성 검증
    String selectEncodedPasswordByMbrCd(@Param("mbrCd") Integer mbrCd);
    // myfeed - 프로필 수정 - 비밀번호 변경
    int updatePassword(@Param("mbrCd") Integer mbrCd, @Param("mbrPw") String encodedPw);
    // myfeed - 팔로우 여부 
    long existsFollow(@Param("me") Integer me, @Param("target") Integer target);
    // myfeed - 팔로우
    int insertFollow(@Param("me") Integer me, @Param("target") Integer target);
    // myfeed - 팔로우 삭제
    int deleteFollow(@Param("me") Integer me, @Param("target") Integer target);
    // myfeed - 게시물 상세 모달
    HomeFeedPost selectPostDetail(@Param("viewerMbrCd") Integer viewerMbrCd,
            @Param("spCd") Integer spCd);
    
    // home - 게시물
    List<HomeFeedPost> selectHomeFeedPosts(@Param("loginMbrCd") Integer loginMbrCd);
}
