package org.codesync.domain;

import java.sql.Date;
import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CodeSyncHistoryVO {
    private int projectNo;
    private String userId;
    private String fileName;
    private String folderName;
    private String newName;
    private String newFolderName;
    private int action;
    private Timestamp updateDate;
}
