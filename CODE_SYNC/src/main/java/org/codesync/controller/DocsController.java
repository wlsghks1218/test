package org.codesync.controller;

import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codesync.domain.DocsColumnVO;
import org.codesync.domain.DocsHistoryVO;
import org.codesync.domain.DocsVO;
import org.codesync.domain.FTPUtil;
import org.codesync.domain.ProjectVO;
import org.codesync.service.DocsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import lombok.extern.log4j.Log4j;

@RestController
@Log4j
@RequestMapping("/docs/*")
@CrossOrigin(origins = "*")
public class DocsController {
	
	@Autowired
	private DocsService service;
	
	@GetMapping("/getProjectByWrapperNo")
	public ProjectVO getProjectByWrapperNo(@RequestParam("wrapperNo") int wrapperNo){
		ProjectVO result = service.getProjectNoByWrapperNo(wrapperNo);
		return result;
	}
	
	@GetMapping("/getColumns")
	public List<DocsColumnVO> getColumns(@RequestParam("wrapperNo") int wrapperNo){
		return service.getColumns(wrapperNo);
	}
	
    private static final String FTP_SERVER = "116.121.53.142"; // NAS FTP 서버
    private static final int FTP_PORT = 21;
    private static final String FTP_USER = "wlsghks1218";
    private static final String FTP_PASSWORD = "D.gray-man2";
    private static final String FTP_UPLOAD_PATH = "/CODE SYNC/docs"; // NAS 내 업로드 경로
    private static final String TEMP_DIR = System.getProperty("java.io.tmpdir"); // 임시 디렉토리

    @GetMapping("/upload")
    public String showUploadForm() {
        return "ftpUploadForm";
    }
    
    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file,
                                             @RequestParam("uploadUserNo") int uploadUserNo,
                                             @RequestParam("columnNo") int columnNo,
                                             @RequestParam("wrapperNo") int wrapperNo) {
        log.warn("업로드 요청 유저 번호: " + uploadUserNo);
        log.warn("업로드 요청 컬럼 번호: " + columnNo);
        log.warn("업로드 요청 래퍼 번호: " + wrapperNo);

        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("파일이 선택되지 않았습니다.");
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("유효하지 않은 파일명입니다.");
        }

        try {
            // 파일명 UTF-8 디코딩 처리
//        	originalFilename = new String(originalFilename.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
        	originalFilename = new String(originalFilename.getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8);
            log.warn("디코딩된 파일명: " + originalFilename);

            String folderPath = FTP_UPLOAD_PATH + "/" + wrapperNo + "/" + columnNo;
            String uploadPath = folderPath + "/" + originalFilename;

            // 임시 파일 생성
            File tempFile = new File(TEMP_DIR, originalFilename);
            file.transferTo(tempFile);

            // FTP 업로드
            FTPUtil ftpUtil = new FTPUtil(FTP_SERVER, FTP_PORT, FTP_USER, FTP_PASSWORD);
            ftpUtil.createDirectory(folderPath);

            boolean isUploaded = ftpUtil.uploadFile(uploadPath, tempFile);
            if (!isUploaded) {
                log.warn("FTP 업로드 실패: " + uploadPath);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("FTP 업로드 실패");
            }

            log.warn("FTP 업로드 성공: " + uploadPath);

            // DB에 파일 정보 저장
            DocsVO vo = new DocsVO();
            vo.setUploadPath(uploadPath);
            vo.setDocsType(originalFilename.substring(originalFilename.lastIndexOf(".") + 1));
            vo.setDocsName(originalFilename);
            vo.setColumnNo(columnNo);
            vo.setUploadUserNo(uploadUserNo);

            int existCount = service.fileExist(uploadPath);
            if (existCount == 0) {
                int result = service.insertFile(vo);
                if (result > 0) {
                    log.warn("DB에 파일 정보 저장 성공");
                    return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, "text/plain; charset=UTF-8")
                            .body("파일 업로드 성공: DB에 새로 저장됨");
                } else {
                    log.warn("DB에 파일 정보 저장 실패");
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("DB에 파일 정보 저장 실패");
                }
            } else {
                int result = service.updateFile(vo);
                if (result > 0) {
                    log.warn("DB에 파일 정보 업데이트 성공");
                    return ResponseEntity.ok("파일 업로드 성공: 기존 파일 정보 업데이트됨");
                } else {
                    log.warn("DB에 파일 정보 업데이트 실패");
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("DB에 파일 정보 업데이트 실패");
                }
            }
        } catch (IOException e) {
            log.error("파일 처리 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("파일 처리 중 오류 발생: " + e.getMessage());
        } catch (Exception e) {
            log.error("예상치 못한 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("예상치 못한 오류 발생: " + e.getMessage());
        } finally {
            try {
                File tempFile = new File(TEMP_DIR, originalFilename);
                if (tempFile.exists() && !tempFile.delete()) {
                    log.warn("임시 파일 삭제 실패: " + tempFile.getAbsolutePath());
                }
            } catch (Exception e) {
                log.warn("임시 파일 삭제 중 예외 발생", e);
            }
        }
    }

    
    @GetMapping("/checkFileExists")
    public boolean checkFileExists(
            @RequestParam("fileName") String fileName,
            @RequestParam("wrapperNo") int wrapperNo,
            @RequestParam("columnNo") int columnNo) {
        log.warn("파일 존재 여부 확인 요청 - 파일명: {}, 래퍼 번호: {}, 컬럼 번호: {}"+ fileName + "/" +  wrapperNo + "/" + columnNo);

        FTPUtil ftpUtil = new FTPUtil(FTP_SERVER, FTP_PORT, FTP_USER, FTP_PASSWORD);

        String filePath = FTP_UPLOAD_PATH + "/" + wrapperNo + "/" + columnNo + "/" + fileName;

        boolean exists = ftpUtil.fileExists(filePath); // 전체 경로 전달
        if (exists) {
            log.warn("파일 존재 확인: {}"+ filePath);
        } else {
            log.warn("파일이 존재하지 않습니다: {}" + filePath);
        }
        return exists;
    }

    
    @PostMapping("/saveColumn")
    public int saveColumn(@RequestBody DocsColumnVO vo) {
    	int existCount = service.checkColumnExist(vo);
    	log.warn("중복 여부 : " + existCount);
    	if(existCount == 0) {
    		int result = service.saveColumn(vo);
    		return result;
    	}else {
    		int result = service.updateColumn(vo);
    		return result;
    	}
    }
    
    @GetMapping("/download")
    public ResponseEntity<UrlResource> downloadFile(@RequestParam("filePath") String filePath) {
        log.warn("다운로드 요청 파일 경로: {} : "+ filePath);

        try {
            // 파일 경로 디코딩
            String decodedFilePath = URLDecoder.decode(filePath, StandardCharsets.UTF_8.name());
            log.warn("디코딩된 파일 경로: {}" + decodedFilePath);

            // FTP 다운로드 준비
            FTPUtil ftpUtil = new FTPUtil(FTP_SERVER, FTP_PORT, FTP_USER, FTP_PASSWORD);
            File tempFile = new File(System.getProperty("java.io.tmpdir"), 
                decodedFilePath.substring(decodedFilePath.lastIndexOf("/") + 1));

            boolean isDownloaded = ftpUtil.downloadFile(decodedFilePath, tempFile);
            if (!isDownloaded) {
                log.warn("FTP에서 파일 다운로드 실패");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }

            // URL 리소스 생성
            UrlResource resource = new UrlResource(tempFile.toURI());
            if (!resource.exists()) {
                log.warn("다운로드 파일이 존재하지 않습니다.");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }

            // 파일 삭제 예약
            tempFile.deleteOnExit();

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + 
                            URLEncoder.encode(tempFile.getName(), StandardCharsets.UTF_8.name()) + "\"")
                    .body(resource);
        } catch (Exception e) {
            log.error("파일 다운로드 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }


    
    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteFile(@RequestParam("filePath") String filePath) {
        log.warn("파일 삭제 요청 - 경로: "+ filePath);

        try {
            FTPUtil ftpUtil = new FTPUtil(FTP_SERVER, FTP_PORT, FTP_USER, FTP_PASSWORD);
            boolean isDeleted = ftpUtil.deleteFile(filePath);

            if (!isDeleted) {
                log.warn("FTP 서버에서 파일 삭제 실패: "+ filePath);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("FTP 서버에서 파일 삭제 실패.");
            }

            int result = service.deleteFile(filePath);
            if (result > 0) {
                log.info("DB에서 파일 정보 삭제 성공");
                return ResponseEntity.ok("파일 삭제 성공");
            } else {
                log.warn("DB에서 파일 정보 삭제 실패");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("DB에서 파일 정보 삭제 실패");
            }
        } catch (Exception e) {
            log.error("파일 삭제 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("파일 삭제 중 오류 발생: " + e.getMessage());
        }
    }

    @DeleteMapping("/deleteColumn")
    public int deleteColumn(@RequestParam("wrapperNo") int wrapperNo, @RequestParam("columnIndex") int columnIndex) {
        // FTP 경로 설정
        String folderPath = FTP_UPLOAD_PATH + "/" + wrapperNo + "/" + columnIndex;
        FTPUtil ftpUtil = new FTPUtil(FTP_SERVER, FTP_PORT, FTP_USER, FTP_PASSWORD);

        try {
            // 1. 폴더 내 파일 삭제
            List<String> files = ftpUtil.listFiles(folderPath); // 폴더 내 파일 목록 가져오기
            for (String filePath : files) {
                boolean isFileDeleted = ftpUtil.deleteFile(folderPath + "/" + filePath);
                if (isFileDeleted) {
                    log.info("FTP 서버에서 파일 삭제 성공: " + filePath);
                } else {
                    log.warn("FTP 서버에서 파일 삭제 실패: " + filePath);
                }
            }

            // 2. 폴더 삭제
            boolean isFolderDeleted = ftpUtil.deleteFolder(folderPath);
            if (isFolderDeleted) {
                log.info("FTP 서버에서 폴더 삭제 성공: " + folderPath);
            } else {
                log.warn("FTP 서버에서 폴더 삭제 실패: " + folderPath);
            }

            // 3. DB에서 컬럼 정보 삭제
            Map<String, Integer> params = new HashMap<>();
            params.put("wrapperNo", wrapperNo);
            params.put("columnIndex", columnIndex);
            return service.deleteColumn(params);
        } catch (Exception e) {
            log.error("컬럼 삭제 중 오류 발생", e);
            return 0; // 실패 시 0 반환
        }
    }
    
    @PostMapping("/uploadHIstory")
    public int uploadHIstory(@RequestBody Map<String, Object> params) {
        int result = service.uploadHistory(params);
        return result;
    }
    
    @PostMapping("/deleteHistory")
    public int deleteHistory(@RequestBody Map<String, Object> params) {
        int result = service.deleteHistory(params);
        return result;
    }
    
    @GetMapping("/getDocsHistory")
    public List<DocsHistoryVO> getDocsHistory(@RequestParam("projectNo") int projectNo, @RequestParam("columnIndex") int columnIndex) {
    	Map<String, Integer> params = new HashMap<>();
    	params.put("projectNo", projectNo);
    	params.put("columnIndex", columnIndex);
    	return service.getDocsHistory(params); 
    }
}