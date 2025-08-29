package ganadinote.sns.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import ganadinote.common.domain.Member;
import ganadinote.sns.domain.FeedPost;
import ganadinote.sns.domain.FollowUser;
import ganadinote.sns.domain.HomeFeedPost;

public interface SnsService {
	// 게시물 업로드
    Integer createPost(String content, Integer mbrCd, MultipartFile[] images);
    
    // myfeed - 게시물 숫자
    long countPostsByMember(Integer mbrCd);
    // myfeed - 팔로워 숫자
    long countFollowersOfMember(Integer mbrCd);
    // myfeed - 팔로우 숫자
    long countFollowingsByMember(Integer mbrCd);
    // myfeed - 게시물 대표이미지
    List<FeedPost> getMyFeedPosts(Integer mbrCd);
    // myfeed- 팔로워 목록
    List<FollowUser> getFollowers(Integer mbrCd);
    // myfeed- 팔로우 목록
    List<FollowUser> getFollowings(Integer mbrCd);
    // myfeed - 프로필 수정 내역 조회
    Member getMemberProfile(Integer mbrCd);
    // myfeed - 프로필 수정 - 닉네임 유효성 검증
    boolean isNicknameDuplicate(String mbrNknm, Integer excludeMbrCd);
    // myfeed - 프로필 수정 - 업데이트
    int updateProfile(Integer mbrCd, String newNickname, MultipartFile profileImage);
    // myfeed - 프로필 수정 - 비밀번호 유효성 검증
    boolean checkCurrentPassword(Integer mbrCd, String rawPassword);
    // myfeed - 프로필 수정 - 비밀번호 변경
    void changePassword(Integer mbrCd, String currentPassword, String newPassword);
    // myfeed - 프로필 - 팔로우 여부
    boolean isFollowing(Integer me, Integer target);
    // myfeed - 프로필 - 팔로우 토글
    boolean toggleFollow(Integer me, Integer target);
    
    // home - 게시물
    List<HomeFeedPost> getHomeFeed(Integer mbrCd);
}