package org.codesync.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.codesync.domain.CodeSyncHistoryVO;
import org.codesync.domain.FileVO;
import org.codesync.domain.FolderVO;
import org.codesync.domain.ProjectVO;

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

	public int updateCode(FileVO file);

	public Integer getLockedBy(int fileNo);

	public int checkFileLockStatus(@Param("fileNo") int fileNo,@Param("userNo") int userNo);

	public List<Integer> getFolderNosByCodeSyncNo(int codeSyncNo);

	public List<Integer> getFileNosByFolderNo(Integer folderNo);

	public void deleteByFolderNo(Integer folderNo);

	public void deleteByFileNo(Integer fileNo);

	public void changeFolderName(@Param("newName") String newName,@Param("folderName") String folderName,@Param("codeSyncNo") int codeSyncNo);

	public void changeFileName(@Param("newName") String newName,@Param("fileName") String fileName,@Param("folderNo") int folderNo);

	public List<FileVO> getFolderChildFile(int folderNo);

	public void deleteFileByFolderNo(int folderNo);

	public void createFile(FileVO file);

	public void createFolder(FileVO file);

	public void pastefolderChilderns(List<FileVO> fvo);

	public void pasteFolder(FileVO file);

	public void pasteFile(@Param("filePath") String filePath, 
						  @Param("folderNo") int folderNo,
						  @Param("fileName") String fileName, 
						  @Param("content") String content, 
						  @Param("extension") String extension, 
						  @Param("userNo") int userNo);

	public FileVO getFileData(FileVO file);

	public void deleteFile(FileVO file);

	public int getProjectNoByCodeSyncNo(int codeSyncNo);

	public List<CodeSyncHistoryVO> getHistoryByProjectNo(int projectNo);

	public String getFileNameByFileNo(int fileNo);

	// 히스토리
	public void insertSaveCodeHis(CodeSyncHistoryVO hvo);

	public void insertCreateFolderHis(CodeSyncHistoryVO history);

	public void insertPasteFolderHistory(CodeSyncHistoryVO history);

	public void insertPasteFileHistory(CodeSyncHistoryVO history);

	public void insertRenameFolderHistory(CodeSyncHistoryVO history);

	public void insertRenameFileHistory(CodeSyncHistoryVO history);

	public ProjectVO getProject(int projectNo);

	public int checkFolderExistence(Long codeSyncNo);


	



	





}
