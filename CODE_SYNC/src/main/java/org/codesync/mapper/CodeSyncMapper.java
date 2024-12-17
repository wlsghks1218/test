package org.codesync.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.codesync.domain.FileVO;
import org.codesync.domain.FolderVO;

public interface CodeSyncMapper {

    // 폴더 저장
    public void saveFolder(FolderVO folder);

    // 부모 폴더 ID 조회
    public Integer findParentFolderId(@Param("folderPath")String folderPath,@Param("codeSyncNo") int codeSyncNo);

    // 폴더의 부모 ID 업데이트
    public void updateFolderParentId(@Param("folderNo") Integer folderNo, @Param("parentFolderId") Integer parentFolderId);

    public Integer getFileNo(@Param("folderPath")String folderPath , @Param("codeSyncNo") int codeSyncNo);

    public void insertFile(FileVO file);

    public List<FolderVO> getFoldersByCodeSyncNo(int codeSyncNo);

    public List<FileVO> getFilesByFolderNo(int folderNo);

    public Integer getFileNoByFolderAndFileName(@Param("folderNo") int folderNo, @Param("fileName") String fileName);

    public String checkFileLock( @Param("fileNo") int fileNo);

	public void lockFile(@Param("lockedBy") int lockedBy,@Param("fileNo") int fileNo);

	public FileVO getLockedFileByUser(@Param("lockedBy") int lockedBy);

	public void unlockFile(@Param("lockedBy") int lockedBy);





}
