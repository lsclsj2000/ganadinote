package ganadinote.sns.service;

import org.springframework.web.multipart.MultipartFile;

public interface SnsService {
    Integer createPost(String content, Integer mbrCd, MultipartFile[] images);
}