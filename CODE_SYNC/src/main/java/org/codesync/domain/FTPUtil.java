package org.codesync.domain;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

import lombok.extern.log4j.Log4j;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

@Log4j
public class FTPUtil {

    private String server;
    private int port;
    private String user;
    private String password;

    public FTPUtil(String server, int port, String user, String password) {
        this.server = server;
        this.port = port;
        this.user = user;
        this.password = password;
    }

    public boolean uploadFile(String remotePath, File localFile) {
        FTPClient ftpClient = new FTPClient();
        ftpClient.setControlEncoding("UTF-8");
        try (FileInputStream inputStream = new FileInputStream(localFile)) {
            ftpClient.connect(server, port);
            ftpClient.login(user, password);
            ftpClient.enterLocalPassiveMode();
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            
            log.warn("로컬 파일 이름: " + localFile.getName());

            if (fileExists(remotePath)) {
                ftpClient.deleteFile(remotePath);
            }

            return ftpClient.storeFile(remotePath, inputStream);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (ftpClient.isConnected()) {
                    ftpClient.logout();
                    ftpClient.disconnect();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    
    public boolean fileExists(String remotePath) {
        FTPClient ftpClient = new FTPClient();
        try {
            ftpClient.connect(server, port);
            ftpClient.login(user, password);
            ftpClient.enterLocalPassiveMode();

            // 파일 존재 여부 확인
            String[] files = ftpClient.listNames(remotePath);
            return files != null && files.length > 0;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (ftpClient.isConnected()) {
                    ftpClient.logout();
                    ftpClient.disconnect();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
    
    public boolean downloadFile(String remotePath, File localFile) {
        FTPClient ftpClient = new FTPClient();
        try (FileOutputStream outputStream = new FileOutputStream(localFile)) {
            ftpClient.connect(server, port);
            ftpClient.login(user, password);
            ftpClient.enterLocalPassiveMode();
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);

            // FTP 서버에서 파일 다운로드
            return ftpClient.retrieveFile(remotePath, outputStream);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (ftpClient.isConnected()) {
                    ftpClient.logout();
                    ftpClient.disconnect();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
    
    public boolean deleteFile(String remotePath) {
        FTPClient ftpClient = new FTPClient();
        ftpClient.setControlEncoding("UTF-8");
        try {
            ftpClient.connect(server, port);
            ftpClient.login(user, password);
            ftpClient.enterLocalPassiveMode();

            return ftpClient.deleteFile(remotePath);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (ftpClient.isConnected()) {
                    ftpClient.logout();
                    ftpClient.disconnect();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
    
    public boolean createDirectory(String remotePath) {
        FTPClient ftpClient = new FTPClient();
        try {
            ftpClient.connect(server, port);
            ftpClient.login(user, password);
            ftpClient.enterLocalPassiveMode();

            String[] pathElements = remotePath.split("/");
            String currentPath = "";
            for (String folder : pathElements) {
                currentPath += "/" + folder;
                if (!ftpClient.changeWorkingDirectory(currentPath)) {
                    if (!ftpClient.makeDirectory(currentPath)) {
                        log.warn("디렉토리 생성 실패: "+ currentPath);
                        return false;
                    }
                }
            }
            return true;
        } catch (IOException e) {
            log.error("디렉토리 생성 중 오류 발생"+ e);
            return false;
        } finally {
            try {
                if (ftpClient.isConnected()) {
                    ftpClient.logout();
                    ftpClient.disconnect();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }


}