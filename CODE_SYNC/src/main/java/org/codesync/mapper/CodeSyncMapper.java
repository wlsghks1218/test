package org.codesync.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.codesync.domain.FileVO;
import org.codesync.domain.FolderVO;

public interface CodeSyncMapper {

    // 폴더 저장
    void saveFolder(FolderVO folder);

    // 부모 폴더 ID 조회
    Integer findParentFolderId(@Param("folderPath")String folderPath,@Param("codeSyncNo") int codeSyncNo);

    // 폴더의 부모 ID 업데이트
    void updateFolderParentId(@Param("folderNo") Integer folderNo, @Param("parentFolderId") Integer parentFolderId);

	Integer getFileNo(@Param("folderPath")String folderPath , @Param("codeSyncNo") int codeSyncNo);

	void insertFile(FileVO file);

	List<FolderVO> getFoldersByCodeSyncNo(int codeSyncNo);

	List<FileVO> getFilesByFolderNo(int folderNo);





}
