package ganadinote.sns.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import ganadinote.common.domain.FileMetaData;
import ganadinote.common.domain.SnsPost;
import ganadinote.common.file.FileUtils;
import ganadinote.sns.mapper.SnsMapper;
import ganadinote.sns.service.SnsService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class SnsServiceImpl implements SnsService {

    private final SnsMapper snsMapper;
    private final FileUtils fileUtils; // ✅ 파일 저장은 여기서 처리

    @Override
    public Integer createPost(String content, Integer mbrCd, MultipartFile[] images) {
        // 1) 게시물 저장
        SnsPost post = new SnsPost();
        post.setMbrCd(mbrCd);
        post.setSpCn(content);
        snsMapper.insertPost(post);   // spCd 채워짐 (useGeneratedKeys)

        // 2) 이미지 저장 (최대 10장)
        if (images != null && images.length > 0) {
            if (images.length > 10) {
                throw new IllegalArgumentException("이미지는 최대 10장까지 업로드 가능합니다.");
            }

            // --- [선택 A] FilesUtils가 ganadinote.common.domain.FileMetaData를 반환하도록 이미 수정된 경우 ---
            List<FileMetaData> uploaded = fileUtils.uploadFiles(images, "sns");

            // --- [선택 B] FilesUtils가 예전 타입(outpolic.systems.file.domain.FileMetaData)을 반환한다면 매핑 필요 ---
            // List<outpolic.systems.file.domain.FileMetaData> uploadedOld = filesUtils.uploadFiles(images, "sns");
            // List<FileMetaData> uploaded = new ArrayList<>();
            // for (var f : uploadedOld) {
            //     FileMetaData m = new FileMetaData();
            //     m.setPostId(String.valueOf(post.getSpCd()));
            //     m.setFileOrgnlNm(f.getFileOriginalName());
            //     m.setFileStoredNm(f.getFileNewName());
            //     m.setFilePath(f.getFilePath());
            //     m.setFileSize(f.getFileSize());
            //     uploaded.add(m);
            // }

            // DB insert용으로 postId 채워 넣기
            List<FileMetaData> rows = new ArrayList<>();
            for (var fm : uploaded) {
                FileMetaData r = new FileMetaData();
                r.setPostId(String.valueOf(post.getSpCd())); // file.post_id (varchar)
                r.setFileOrgnlNm(fm.getFileOrgnlNm());
                r.setFileStoredNm(fm.getFileStoredNm());
                r.setFilePath(fm.getFilePath());
                r.setFileSize(fm.getFileSize());
                rows.add(r);
            }

            if (!rows.isEmpty()) {
                snsMapper.insertFilesBatch(rows); // `file` 테이블에 다중 insert
            }
        }

        return post.getSpCd();
    }
}
