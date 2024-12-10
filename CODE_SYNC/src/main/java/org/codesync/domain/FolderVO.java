package org.codesync.domain;

import java.sql.Date;
import java.sql.Timestamp;



import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FolderVO {
    private int folderNo;      // 폴더 번호 (primary key)
    private String folderName; // 폴더 이름
    private String folderPath; // 폴더 경로
    private Integer parentFolderId; // 상위 폴더의 ID (없으면 null 가능)
    private int codeSyncNo;
    private int gitNo;         // Git 번호 (연관된 Git 레포지토리)
    private Timestamp createdAt; // 폴더 생성일시
    private int createdBy;  // 폴더를 만든 유저
    
    @Override
    public String toString() {
        return "FolderVO{" +
                "folderNo=" + folderNo +
                ", folderName='" + folderName + '\'' +
                ", folderPath='" + folderPath + '\'' +
                ", parentFolderId=" + parentFolderId +
                ", gitNo=" + gitNo +
                ", createdAt=" + createdAt +
                ", createdBy=" + createdBy +
                '}';
    }

}
