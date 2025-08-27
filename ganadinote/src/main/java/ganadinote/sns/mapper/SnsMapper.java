package ganadinote.sns.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import ganadinote.common.domain.SnsPost;
import ganadinote.sns.domain.FollowUser;

@Mapper
public interface SnsMapper {
	
	// 게시물 업로드
	int insertPost(SnsPost post);
    
    
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
}
