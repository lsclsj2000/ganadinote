package ganadinote.sns.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import ganadinote.common.domain.FileMetaData;
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
    
    @Override @Transactional(readOnly = true)
    public List<FollowUser> getFollowers(Integer mbrCd) {
        return snsMapper.selectFollowersOfMember(mbrCd);
    }

    @Override @Transactional(readOnly = true)
    public List<FollowUser> getFollowings(Integer mbrCd) {
        return snsMapper.selectFollowingsByMember(mbrCd);
    }
    
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
}
