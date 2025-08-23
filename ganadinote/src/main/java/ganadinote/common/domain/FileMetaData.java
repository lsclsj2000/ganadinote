package ganadinote.common.domain;

import lombok.Data;

@Data
public class FileMetaData {
	private Integer fileId;
	private String postId;
    private String fileOrgnlNm;
    private String fileStoredNm;
    private String filePath;
    private Long   fileSize;
	
	public FileMetaData() {}
	
	private FileMetaData(Builder builder) {
		this.fileId = builder.fileId;
		this.postId = builder.postId;
		this.fileOrgnlNm = builder.fileOrgnlNm;
		this.fileStoredNm = builder.fileStoredNm;
		this.filePath = builder.filePath;
		this.fileSize = builder.fileSize;
	}
	
	
	public void setfileId(Integer fileId) {
		this.fileId = fileId;
	}


	public void setPostId(String postId) {
		this.postId = postId;
	}


	public void setFileOrgnlNm(String fileOrgnlNm) {
		this.fileOrgnlNm = fileOrgnlNm;
	}


	public void setFileStoredNm(String fileStoredNm) {
		this.fileStoredNm = fileStoredNm;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public void setFileSize(Long fileSize) {
		this.fileSize = fileSize;
	}


	public Integer getfileId() {
		return fileId;
	}

	public String getPostId() {
		return postId;
	}

	public String getFileOrgnlNm() {
		return fileOrgnlNm;
	}

	public String getFileStoredNm() {
		return fileStoredNm;
	}
	
	public String getFilePath() {
		return filePath;
	}

	public Long getFileSize() {
		return fileSize;
	}

	public static class Builder {
	
		private Integer fileId;
		private String postId;
	    private String fileOrgnlNm;
	    private String fileStoredNm;
	    private String filePath;
	    private Long   fileSize;
		
		public Builder() {}

		public Builder(FileMetaData fileMetaData) {
			this.fileId = fileMetaData.fileId;
			this.postId = fileMetaData.postId;
			this.fileOrgnlNm = fileMetaData.fileOrgnlNm;
			this.fileStoredNm = fileMetaData.fileStoredNm;
			this.filePath = fileMetaData.filePath;
			this.fileSize = fileMetaData.fileSize;
		}

		public Builder fileId(Integer fileId) {
			this.fileId = fileId;
			return this;
		}

		public Builder postId(String postId) {
			this.postId = postId;
			return this;
		}

		public Builder fileOrgnlNm(String fileOrgnlNm) {
			this.fileOrgnlNm = fileOrgnlNm;
			return this;
		}

		public Builder fileStoredNm(String fileStoredNm) {
			this.fileStoredNm = fileStoredNm;
			return this;
		}
		
		public Builder filePath(String filePath) {
			this.filePath = filePath;
			return this;
		}

		public Builder fileSize(Long fileSize) {
			this.fileSize = fileSize;
			return this;
		}
		
		public FileMetaData build() {
			return new FileMetaData(this);
		}
	}
}
