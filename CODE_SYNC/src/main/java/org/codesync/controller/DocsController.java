package org.codesync.controller;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.codesync.domain.DocsColumnVO;
import org.codesync.domain.FTPUtil;
import org.codesync.domain.ProjectVO;
import org.codesync.service.DocsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
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
    public String uploadFile(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return "파일이 선택되지 않았습니다.";
        }

        File tempFile = new File(System.getProperty("java.io.tmpdir"), file.getOriginalFilename());
        try {
            file.transferTo(tempFile);

            FTPUtil ftpUtil = new FTPUtil(FTP_SERVER, FTP_PORT, FTP_USER, FTP_PASSWORD);
            boolean isUploaded = ftpUtil.uploadFile(FTP_UPLOAD_PATH + "/" + file.getOriginalFilename(), tempFile);
            if(isUploaded) {
            	
            }
            return isUploaded ? "파일 업로드 성공!" : "파일 업로드 실패.";
        } catch (IOException e) {
            e.printStackTrace();
            return "파일 처리 중 오류 발생: " + e.getMessage();
        } finally {
            tempFile.delete();
        }
    }
    
    @GetMapping("/checkFileExists")
    public boolean checkFileExists(@RequestParam("fileName") String fileName) {
        FTPUtil ftpUtil = new FTPUtil(FTP_SERVER, FTP_PORT, FTP_USER, FTP_PASSWORD);
        return ftpUtil.fileExists(FTP_UPLOAD_PATH + "/" + fileName);
    }
}
