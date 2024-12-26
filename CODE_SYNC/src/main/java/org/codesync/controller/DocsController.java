package org.codesync.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
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
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import io.opencensus.resource.Resource;
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
    public String uploadFile(@RequestParam("file") MultipartFile file, @RequestParam("uploadUserNo") int uploadUserNo, @RequestParam("columnNo") int columnNo, @RequestParam("wrapperNo") int wrapperNo) {
        log.warn("업로드 요청 유저 번호: " + uploadUserNo);
        log.warn("업로드 요청 컬럼 번호: " + columnNo);
        log.warn("업로드 요청 래퍼 번호: " + wrapperNo);

        if (file.isEmpty()) {
            return "파일이 선택되지 않았습니다.";
        }

        String folderPath = FTP_UPLOAD_PATH + "/" + wrapperNo + "/" + columnNo;
        String uploadPath = folderPath + "/" + file.getOriginalFilename();

        File tempFile = new File(TEMP_DIR, file.getOriginalFilename());
        try {
            file.transferTo(tempFile);

            FTPUtil ftpUtil = new FTPUtil(FTP_SERVER, FTP_PORT, FTP_USER, FTP_PASSWORD);
            ftpUtil.createDirectory(folderPath);

            boolean isUploaded = ftpUtil.uploadFile(uploadPath, tempFile);
            if (isUploaded) {
                log.info("FTP 업로드 성공: " + uploadPath);

                DocsVO vo = new DocsVO();
                vo.setUploadPath(uploadPath);
                vo.setDocsType(file.getOriginalFilename()
                        .substring(file.getOriginalFilename().lastIndexOf(".") + 1));
                vo.setDocsName(file.getOriginalFilename());
                vo.setColumnNo(columnNo);
                vo.setUploadUserNo(uploadUserNo);

                int existCount = service.fileExist(uploadPath);
                if (existCount == 0) {
                    int result = service.insertFile(vo);
                    if (result > 0) {
                        log.info("DB에 파일 정보 저장 성공");
                    } else {
                        log.warn("DB에 파일 정보 저장 실패");
                    }
                } else if (existCount > 0) {
                    int result = service.updateFile(vo);
                    if (result > 0) {
                        log.info("DB에 파일 정보 업데이트 성공");
                    } else {
                        log.warn("DB에 파일 정보 업데이트 실패");
                    }
                }
            } else {
                log.warn("FTP 업로드 실패: " + uploadPath);
                return "FTP 업로드 실패";
            }

            return "파일 업로드 성공";
        } catch (IOException e) {
            log.error("파일 처리 중 오류 발생", e);
            return "파일 처리 중 오류 발생: " + e.getMessage();
        } finally {
            tempFile.delete();
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
        log.warn("다운로드 요청 파일 경로: {}"+ filePath);

        try {
            FTPUtil ftpUtil = new FTPUtil(FTP_SERVER, FTP_PORT, FTP_USER, FTP_PASSWORD);
            File tempFile = new File(System.getProperty("java.io.tmpdir"), filePath.substring(filePath.lastIndexOf("/") + 1));

            boolean isDownloaded = ftpUtil.downloadFile(filePath, tempFile);
            if (!isDownloaded) {
                log.warn("FTP에서 파일 다운로드 실패");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }

            UrlResource resource = new UrlResource(tempFile.toURI());
            if (!resource.exists()) {
                log.warn("다운로드 파일이 존재하지 않습니다.");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }

            tempFile.deleteOnExit();

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + tempFile.getName() + "\"")
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

    @GetMapping("/view")
    public ResponseEntity<String> viewFile(@RequestParam("filePath") String filePath) {
        log.warn("Requested filePath: " + filePath);

        try {
            // NAS URL 생성
            String nasHttpUrl = "http://116.121.53.142" + filePath; // NAS의 HTTP URL 생성
            log.warn("Generated NAS URL: " + nasHttpUrl);

            // 클라이언트에 URL 반환
            return ResponseEntity.ok(nasHttpUrl);
        } catch (Exception e) {
            log.error("NAS URL 생성 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("파일 URL 생성 중 오류가 발생했습니다.");
        }
    }


    @DeleteMapping("/deleteColumn")
    public int deleteColumn(@RequestParam("wrapperNo") int wrapperNo, @RequestParam("columnIndex") int columnIndex) {
    	Map<String, Integer> params = new HashMap<>();
    	params.put("wrapperNo", wrapperNo);
    	params.put("columnIndex", columnIndex);
    	int result = service.deleteColumn(params);
    	return result;
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
    
    @GetMapping("/getDocsHistoryForGantt")
    public List<DocsHistoryVO> getDocsHistoryForGantt(@RequestParam("projectNo") int projectNo) {
    	return service.getDocsHistoryForGantt(projectNo); 
    }
}
