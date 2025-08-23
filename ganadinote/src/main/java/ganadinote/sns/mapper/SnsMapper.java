package ganadinote.sns.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import ganadinote.common.domain.FileMetaData;
import ganadinote.common.domain.SnsPost;

@Mapper
public interface SnsMapper {
	
	// 게시물 업로드
	int insertPost(SnsPost post);
	
	// 사진 저장
    int insertFilesBatch(List<FileMetaData> files);
}
