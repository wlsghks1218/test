package org.codesync.domain;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

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
        try (FileInputStream inputStream = new FileInputStream(localFile)) {
            ftpClient.connect(server, port);
            ftpClient.login(user, password);
            ftpClient.enterLocalPassiveMode();
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);

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
}

