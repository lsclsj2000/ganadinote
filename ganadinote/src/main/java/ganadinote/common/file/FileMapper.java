package ganadinote.common.file;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import ganadinote.common.domain.FileMetaData;

@Mapper
public interface FileMapper {
	int deleteFileByIdx(String fileIdx);
	FileMetaData getFileInfoByIdx(String fileIdx);
	List<FileMetaData> getFileList();
	int addfile(FileMetaData fileDto);
	int addfiles(List<FileMetaData> fileDto);
	List<FileMetaData> selectFirstFilesByPostIds(List<String> postIds);
	
	
	int bindFilesToPostByPaths(Long postId, List<String> paths);
}
