package ganadinote.common.file;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import ganadinote.common.domain.FileMetaData;

@Component
public class FileUtils {
    @Value("${file.path}")
    private String fileRoot;

    private static final DateTimeFormatter DAY = DateTimeFormatter.ofPattern("yyyyMMdd");

    public List<FileMetaData> uploadFiles(MultipartFile[] files, String service) {
        List<FileMetaData> list = new ArrayList<>();
        if (files == null) return list;
        for (MultipartFile f : files) {
            FileMetaData m = storeFile(f, service);
            if (m != null) list.add(m);
        }
        return list;
    }

    public FileMetaData uploadFile(MultipartFile file, String service) {
        return storeFile(file, service);
    }

    public boolean deleteFileByPath(String relativePath) {
        try {
            Path p = Paths.get(fileRoot, relativePath.replaceFirst("^/+", ""));
            return Files.deleteIfExists(p);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // ---------------- internal ----------------
    private FileMetaData storeFile(MultipartFile mf, String service) {
        if (mf == null || mf.isEmpty()) return null;
        if (service == null || service.isBlank()) service = "common";

        String today = LocalDate.now(ZoneId.of("Asia/Seoul")).format(DAY);
        String contentType = mf.getContentType();
        String typeDir = (contentType != null && contentType.startsWith("image")) ? "image" : "files";

        // 물리 경로: {fileRoot}/attachment/{service}/{yyyymmdd}/{image|files}/
        Path dir = Paths.get(fileRoot, "attachment", service, today, typeDir);
        createDir(dir);

        String original = Optional.ofNullable(mf.getOriginalFilename()).orElse("file");
        String ext = getExt(original);                  // ".jpg" 형태 또는 ""(없음)
        String newName = UUID.randomUUID() + ext;

        Path savePath = dir.resolve(newName);
        try {
            Files.write(savePath, mf.getBytes());
        } catch (IOException e) {
            throw new RuntimeException("파일 저장 실패: " + savePath, e);
        }

        // 클라이언트에서 쓸 상대 경로: /attachment/{service}/{yyyymmdd}/{type}/newName
        String rel = ("/attachment/" + service + "/" + today + "/" + typeDir + "/" + newName).replace("\\", "/");

        FileMetaData m = new FileMetaData();
        m.setPostId(null); // 나중에 서비스에서 채움
        m.setFileOrgnlNm(original);
        m.setFileStoredNm(newName);
        m.setFilePath(rel);
        m.setFileSize(mf.getSize());
        return m;
    }

    private void createDir(Path dir) {
        try {
            Files.createDirectories(dir);
        } catch (Exception e) {
            throw new RuntimeException("디렉토리 생성 실패: " + dir, e);
        }
    }

    private String getExt(String name) {
        int i = name.lastIndexOf('.');
        return (i >= 0 && i < name.length() - 1) ? name.substring(i) : "";
    }
    
    public void deleteQuietly(String relativePath) {
        if (relativePath == null || relativePath.isBlank()) return;
        try {
            Path root = Paths.get(getUploadRootPath()).toAbsolutePath().normalize();
            Path target = root.resolve(relativePath.replaceFirst("^/+", ""))
                              .normalize();

            // root 바깥 경로로 벗어나면 삭제 금지
            if (!target.startsWith(root)) return;

            Files.deleteIfExists(target);
        } catch (Exception ignore) {}
    }
    
    private String getUploadRootPath() {
        // application.properties/yaml 의 file.path 값 사용.
        // 비어있으면 현재 작업 디렉토리를 기본값으로.
        String root = (fileRoot != null && !fileRoot.isBlank())
                ? fileRoot
                : Paths.get("").toAbsolutePath().toString();

        // 정규화해서 문자열로 반환 (윈도/리눅스 모두 OK)
        return Paths.get(root).toAbsolutePath().normalize().toString();
    }
}
