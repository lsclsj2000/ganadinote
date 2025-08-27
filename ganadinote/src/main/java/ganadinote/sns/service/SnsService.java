package ganadinote.sns.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import ganadinote.sns.domain.FeedPost;
import ganadinote.sns.domain.FollowUser;

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
}