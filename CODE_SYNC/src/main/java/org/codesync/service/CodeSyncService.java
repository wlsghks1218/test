package org.codesync.service;

import java.util.List;

import org.codesync.domain.FileVO;
import org.codesync.domain.FolderStructureVO;
import org.codesync.domain.FolderVO;

public interface CodeSyncService {

	String saveFolderData(FolderVO folder);

	void updateFolderParentId(int folderNo, Integer parentFolderId);

	Integer findParentFolderFileNo(String filePath, int codeSyncNo);

	Integer getFileNo(String folderPath, int codeSyncNo);

	int getFolderNo(String filePath, int codeSyncNo);

	void saveFile(FileVO file);

	FolderStructureVO getFolderStructureByCodeSyncNo(int codeSyncNo);

	Integer getFileNoByFolderAndFileName(int folderNo, String fileName);

	boolean checkFileLock(FileVO file);

	void lockFile(FileVO file);

	void unlockFile(FileVO previouslyLockedFile);

	FileVO getLockedFileByUser(int lockedBy);








}
