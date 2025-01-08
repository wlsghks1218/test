package org.codesync.service;

import java.util.List;

import org.codesync.domain.CodeSyncHistoryVO;
import org.codesync.domain.FileVO;
import org.codesync.domain.FolderStructureVO;
import org.codesync.domain.FolderVO;
import org.codesync.domain.ProjectVO;

public interface CodeSyncService {

	public String saveFolderData(FolderVO folder);

	public void updateFolderParentId(int folderNo, Integer parentFolderId);

	public Integer findParentFolderFileNo(String filePath, int codeSyncNo);

	public Integer getFileNo(String folderPath, int codeSyncNo);

	public int getFolderNo(String filePath, int codeSyncNo);

	public void saveFile(FileVO file);

	public FolderStructureVO getFolderStructureByCodeSyncNo(int codeSyncNo);

	public Integer getFileNoByFolderAndFileName(int folderNo, String fileName);

	public boolean checkFileLock(FileVO file);

	public void lockFile(FileVO file);

	public void unlockFile(FileVO previouslyLockedFile);

	public FileVO getLockedFileByUser(int lockedBy);

	public boolean saveCode(int fileNo, String content);

	public int getLockedFile(int fileNo, int userNo);

	public boolean isFileLockedByAnotherUser(int fileNo, int lockedBy);

	public boolean checkFileLockStatus(int fileNo, int userNo);

	public List<Integer> getFolderNosByCodeSyncNo(int codeSyncNo);

	public List<Integer> getFileNosByFolderNo(Integer folderNo);

	public boolean deleteFoldersAndFiles(List<Integer> folderNos, List<Integer> fileNosToDelete);

	public void changeFolderName(String newName, String folderName, int codeSyncNo);

	public void changeFileName(String newName, String fileName, int folderNo);

	public void deleteFolder(int folderNo);

	public void createFile(FileVO file);

	public void createFolder(FileVO file);

	public void pastefolder(FileVO file);

	public void pasteFile(FileVO file);

	public void deleteFile(FileVO file);
	
	public int getProjectNoByCodeSyncNo(int codeSyncNo);

	public List<CodeSyncHistoryVO> getHistoryByProjectNo(int projectNo);

	public String getFileNameByFileNo(int fileNo);

// 히스토리
	public void insertSaveCodeHis(CodeSyncHistoryVO hvo);  // Integer로 변환(CodeSyncHistoryVO hvo);

	public void insertCreateFolderHis(CodeSyncHistoryVO history);

	public void insertPasteFolderHistory(CodeSyncHistoryVO history);

	public void insertPasteFileHistory(CodeSyncHistoryVO history);

	public void insertRenameFolderHistory(CodeSyncHistoryVO history);

	public void insertRenameFileHistory(CodeSyncHistoryVO history);

	public ProjectVO getProject(int codeSyncNo);

	public int checkFolderExistence(Long codeSyncNo);


}
