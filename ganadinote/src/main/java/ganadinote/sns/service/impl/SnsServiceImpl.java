package ganadinote.sns.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import ganadinote.common.domain.FileMetaData;
import ganadinote.common.domain.Member;
import ganadinote.common.domain.SnsPost;
import ganadinote.common.file.FileMapper;
import ganadinote.common.file.FileUtils;
import ganadinote.sns.domain.FeedPost;
import ganadinote.sns.domain.FollowUser;
import ganadinote.sns.domain.HomeFeedPost;
import ganadinote.sns.mapper.SnsMapper;
import ganadinote.sns.service.SnsService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class SnsServiceImpl implements SnsService {

    private final SnsMapper snsMapper;
    private final FileUtils fileUtils; // 파일 저장은 여기서 처리
    private final FileMapper fileMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Integer createPost(String content, Integer mbrCd, MultipartFile[] images) {
        // ✅ 서버 최종 방어: 사진 필수
        if (images == null || images.length == 0) {
            throw new IllegalArgumentException("사진은 최소 1장 이상 업로드해야 합니다.");
        }
        if (images.length > 10) {
            throw new IllegalArgumentException("이미지는 최대 10장까지 업로드 가능합니다.");
        }

        // 1) 게시물 저장 (내용은 선택)
        SnsPost post = new SnsPost();
        post.setMbrCd(mbrCd);
        post.setSpCn(content == null ? "" : content);
        snsMapper.insertPost(post);   // spCd 세팅됨

        // 2) 이미지 저장
        List<FileMetaData> uploaded = fileUtils.uploadFiles(images, "sns");

        List<FileMetaData> rows = new ArrayList<>();
        for (var fm : uploaded) {
            FileMetaData r = new FileMetaData();
            r.setPostId(String.valueOf(post.getSpCd())); // file.post_id (varchar)
            r.setFileOrgnlNm(fm.getFileOrgnlNm());
            r.setFileStoredNm(fm.getFileStoredNm());
            r.setFilePath(fm.getFilePath());
            r.setFileSize(fm.getFileSize());
            r.setPostType("sns"); // ✅ 새 컬럼
            rows.add(r);
        }

        if (!rows.isEmpty()) {
            fileMapper.addfiles(rows); // `file` 테이블에 다중 insert
        }

        return post.getSpCd();
    }
    
    // myfeed - 게시물 숫자
    @Transactional(readOnly = true)
    public long countPostsByMember(Integer mbrCd) {
        return snsMapper.countPostsByMember(mbrCd);
    }

    
    // myfeed - 팔로워 숫자
    @Transactional(readOnly = true)
    public long countFollowersOfMember(Integer mbrCd) {
        return snsMapper.countFollowersOfMember(mbrCd);
    }

    
    // myfeed - 팔로우 숫자
    @Transactional(readOnly = true)
    public long countFollowingsByMember(Integer mbrCd) {
        return snsMapper.countFollowingsByMember(mbrCd);
    }

    // myfeed - 게시물 대표이미지
    @Override
    @Transactional(readOnly = true)
    public List<FeedPost> getMyFeedPosts(Integer mbrCd) {
        // 1) 내 게시물 목록
    	List<SnsPost> posts = snsMapper.selectPostsByMember(mbrCd);
        if (posts.isEmpty()) return java.util.Collections.emptyList();

        // 2) 대표 이미지 일괄 조회
        List<String> postIds = posts.stream()
                                    .map(p -> String.valueOf(p.getSpCd()))
                                    .collect(java.util.stream.Collectors.toList());
        List<FileMetaData> firstFiles = fileMapper.selectFirstFilesByPostIds(postIds);
        Map<String, String> repMap = firstFiles.stream()
            .collect(java.util.stream.Collectors.toMap(FileMetaData::getPostId, FileMetaData::getFilePath));

        // 3) DTO 머지
        List<FeedPost> feedPost = new java.util.ArrayList<>(posts.size());
        for (SnsPost p : posts) {
            FeedPost fp = new FeedPost();
            fp.setSpCd(p.getSpCd());
            fp.setSpCn(p.getSpCn());
            fp.setSpRegYmdt(p.getSpRegYmdt());
            fp.setRepImagePath(repMap.get(String.valueOf(p.getSpCd())));
            feedPost.add(fp);
        }
        return feedPost;
    }
    
    // myfeed- 팔로워 목록
    @Override @Transactional(readOnly = true)
    public List<FollowUser> getFollowers(Integer mbrCd) {
        return snsMapper.selectFollowersOfMember(mbrCd);
    }
    
    // myfeed- 팔로우 목록
    @Override @Transactional(readOnly = true)
    public List<FollowUser> getFollowings(Integer mbrCd) {
        return snsMapper.selectFollowingsByMember(mbrCd);
    }
    
    // home - 게시물
    @Override
    @Transactional(readOnly = true)
    public List<HomeFeedPost> getHomeFeed(Integer mbrCd) {
        List<HomeFeedPost> posts = snsMapper.selectHomeFeedPosts(mbrCd);
        if (posts.isEmpty()) return java.util.Collections.emptyList();

        // 1) 게시물 ID 수집
        List<String> postIds = posts.stream()
                .map(p -> String.valueOf(p.getSpCd()))
                .collect(java.util.stream.Collectors.toList());

        // 2) 전체 이미지 조회 후 매핑
        List<FileMetaData> allFiles = fileMapper.selectAllFilesByPostIds(postIds);
        if (allFiles == null) allFiles = java.util.Collections.emptyList();

        Map<String, List<String>> imagesMap = new java.util.HashMap<>();
        for (FileMetaData fm : allFiles) {
            if (fm == null || fm.getPostId() == null || fm.getFilePath() == null) continue; // <— 방어
            imagesMap.computeIfAbsent(fm.getPostId(), k -> new java.util.ArrayList<>())
                     .add(fm.getFilePath());
        }

        // 3) DTO에 주입 (repImagePath는 첫 장)
        for (HomeFeedPost p : posts) {
            List<String> imgs = imagesMap.get(String.valueOf(p.getSpCd()));
            p.setImagePaths(imgs);
            if (imgs != null && !imgs.isEmpty()) {
                p.setRepImagePath(imgs.get(0));
            }
        }

        return posts;
    }
    
    // myfeed - 프로필 수정 내역 조회
	@Override
	public Member getMemberProfile(Integer mbrCd) {
		return snsMapper.selectMemberById(mbrCd);
	}
	
	// myfeed - 프로필 수정 - 닉네임 유효성 검증
	@Override
	@Transactional(readOnly = true)
	public boolean isNicknameDuplicate(String mbrNknm, Integer excludeMbrCd) {
	    if (mbrNknm == null || mbrNknm.isBlank()) return false;
	    return snsMapper.countNickname(mbrNknm, excludeMbrCd) > 0;
	}

    // myfeed - 프로필 수정 - 업데이트
	@Override
	public int updateProfile(Integer mbrCd, String newNickname, MultipartFile profileImage) {
	    // 현재 회원 정보
	    Member me = snsMapper.selectMemberById(mbrCd);
	    if (me == null) throw new IllegalArgumentException("회원이 존재하지 않습니다.");

	    String nicknameToSet = null;
	    String profilePathToSet = null;

	    // 닉네임 변경 여부
	    if (newNickname != null) {
	        String trimmed = newNickname.trim();
	        if (trimmed.length() == 0) throw new IllegalArgumentException("닉네임을 입력하세요.");
	        if (trimmed.length() > 50)  throw new IllegalArgumentException("닉네임은 50자 이하여야 합니다.");
	        if (!trimmed.equals(me.getMbrNknm())) {
	            // 최종 중복 체크
	            if (isNicknameDuplicate(trimmed, mbrCd)) {
	                throw new org.springframework.dao.DuplicateKeyException("닉네임 중복");
	            }
	            nicknameToSet = trimmed;
	        }
	    }

	    // 이미지 변경 여부
	    if (profileImage != null && !profileImage.isEmpty()) {
	        // 기존 FileUtils가 다중 업로드만 있다면 이렇게 감싸서 1건 사용
	        List<FileMetaData> uploaded = fileUtils.uploadFiles(
	                new MultipartFile[]{ profileImage }, "sns"
	        );
	        if (uploaded != null && !uploaded.isEmpty()) {
	            profilePathToSet = uploaded.get(0).getFilePath(); // 예: /attachment/sns/20250827/image/xxx.png
	        }
	    }

	    // 둘 다 없으면 변경사항 없음
	    if (nicknameToSet == null && profilePathToSet == null) return 0;

	    // 동적 업데이트
	    return snsMapper.updateMemberProfile(mbrCd, nicknameToSet, profilePathToSet);
	}
	
    // myfeed - 프로필 수정 - 비밀번호 유효성 검증
	@Override
    @Transactional(readOnly = true)
    public boolean checkCurrentPassword(Integer mbrCd, String rawPassword) {
        String encoded = snsMapper.selectEncodedPasswordByMbrCd(mbrCd);
        if (encoded == null) return false;
        return passwordEncoder.matches(rawPassword, encoded);
    }

    // myfeed - 프로필 수정 - 비밀번호 변경
    @Override
    public void changePassword(Integer mbrCd, String currentPassword, String newPassword) {
        // 현재 비번 검증
        String encoded = snsMapper.selectEncodedPasswordByMbrCd(mbrCd);
        if (encoded == null || !passwordEncoder.matches(currentPassword, encoded)) {
            throw new IllegalArgumentException("현재 비밀번호가 일치하지 않습니다.");
        }
        // 새 비번 규칙(서버측 2차 방어)
        if (newPassword == null || newPassword.length() < 8)
            throw new IllegalArgumentException("비밀번호는 8자 이상이어야 합니다.");
        if (newPassword.length() > 64)
            throw new IllegalArgumentException("비밀번호는 64자를 넘길 수 없습니다.");
        if (newPassword.contains(" "))
            throw new IllegalArgumentException("비밀번호에 공백을 사용할 수 없습니다.");

        // 현재와 동일 금지(선택)
        if (passwordEncoder.matches(newPassword, encoded))
            throw new IllegalArgumentException("현재 비밀번호와 동일할 수 없습니다.");

        String newEncoded = passwordEncoder.encode(newPassword);
        int updated = snsMapper.updatePassword(mbrCd, newEncoded);
        if (updated == 0)
            throw new IllegalStateException("비밀번호 변경에 실패했습니다.");
    }
    
    // myfeed - 프로필 - 팔로우 여부
    @Override
    @Transactional(readOnly = true)
    public boolean isFollowing(Integer me, Integer target) {
        return snsMapper.existsFollow(me, target) > 0;
    }

    // myfeed - 프로필 - 팔로우 토글
    @Override
    public boolean toggleFollow(Integer me, Integer target) {
        boolean now = isFollowing(me, target);
        if (now) {
            snsMapper.deleteFollow(me, target);
            return false;
        } else {
            snsMapper.insertFollow(me, target);
            return true;
        }
    }
}
