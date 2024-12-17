package org.codesync.service;

import java.util.ArrayList;
import java.util.List;

import org.codesync.domain.FileVO;
import org.codesync.domain.FolderStructureVO;
import org.codesync.domain.FolderVO;
import org.codesync.mapper.CodeSyncMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.log4j.Log4j;

@Service
@Log4j
public class CodeSyncServiceImpl implements CodeSyncService {

    @Autowired
    private CodeSyncMapper mapper;

    // 1. 폴더 데이터 저장 후 filePath 리턴
    @Override
    @Transactional
    public String saveFolderData(FolderVO folder) {
        try {
            // 폴더 저장 (folderNo 생성)
            mapper.saveFolder(folder);

            // 저장된 폴더의 filePath를 반환
            return folder.getFolderPath(); // 폴더의 filePath를 리턴
        } catch (Exception e) {
            e.printStackTrace();
            throw e; // 트랜잭션 롤백
        }
    }

    // 2. 부모 폴더의 fileNo 찾기
    public Integer findParentFolderFileNo(String parentPath,int codeSyncNo) {
        if (parentPath == null || !parentPath.contains("/")) {
            return null; // 루트 폴더일 경우 null 반환
        }
        int lastIndex = parentPath.lastIndexOf("/");
        String parentPathTrimmed = parentPath.substring(0, lastIndex);
        System.out.println("코드 싱크 넘버는?? " + codeSyncNo);
        return mapper.findParentFolderId(parentPathTrimmed,codeSyncNo);
    }
    @Override
    public Integer getFileNo(String folderPath , int codeSyncNo) {

         return mapper.getFileNo(folderPath , codeSyncNo);
    }

    // 3. 자식 폴더의 부모 폴더 ID 업데이트
    public void updateFolderParentId(int folderNo, Integer parentFolderId) {
        mapper.updateFolderParentId(folderNo, parentFolderId);
    }

    @Override
    public int getFolderNo(String filePath, int codeSyncNo) {
        // Root를 경로에 추가
        String folderPath = "Root/" + filePath.substring(0, filePath.lastIndexOf('/'));
        
        System.out.println("폴더 경로는 ? " + folderPath);
        System.out.println("코드싱크 넘버 오긴오는거야? " + codeSyncNo);
        
        try {
            // 폴더 경로에 해당하는 folderNo 조회
            return mapper.getFileNo(folderPath, codeSyncNo); // 수정된 폴더 경로를 사용
        } catch (Exception e) {
            throw new RuntimeException("Failed to retrieve folder number for path: " + folderPath, e);
        }
    }

    // 파일 저장 메서드
    @Transactional
    public void saveFile(FileVO file) {
        try {
            mapper.insertFile(file);  // MyBatis Mapper를 통해 DB에 파일 데이터 저장
        } catch (Exception e) {
            throw new RuntimeException("Failed to save file: " + file.getFileName(), e);
        }
    }
    @Override
    public FolderStructureVO getFolderStructureByCodeSyncNo(int codeSyncNo) {
        try {
            // 코드 싱크 번호에 해당하는 폴더 리스트 조회
            List<FolderVO> folders = mapper.getFoldersByCodeSyncNo(codeSyncNo);

            // 각 폴더에 해당하는 파일 리스트를 조회
            List<FileVO> files = new ArrayList<>();
            for (FolderVO folder : folders) {
                // 각 folderNo에 해당하는 파일 리스트를 조회하여 합침
                List<FileVO> folderFiles = mapper.getFilesByFolderNo(folder.getFolderNo());
                files.addAll(folderFiles);
            }

            // 폴더와 파일 리스트를 기반으로 FolderStructureVO 객체 생성
            return new FolderStructureVO(folders, files);
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch folder structure for codeSyncNo: " + codeSyncNo, e);
        }
    }
    @Override
    public Integer getFileNoByFolderAndFileName(int folderNo, String fileName) {
    	System.out.println("서비스의 folderNo " + folderNo);
    	System.out.println("서비스의 fileName " + fileName);
        // folderNo와 fileName을 사용하여 fileNo를 조회하는 로직
        return mapper.getFileNoByFolderAndFileName(folderNo, fileName);
    }

	@Override
	public boolean checkFileLock(FileVO file) {
		int codeSyncNo = file.getCodeSyncNo();
		log.info("파일 잠금상태 체크용 코드싱크넘버 : "+ codeSyncNo);
		int fileNo = file.getFileNo();
		log.info("파일 잠금상태 체크용 파일 넘버 : "+fileNo);
		String isLocked = mapper.checkFileLock(fileNo);
		log.info(isLocked);
		boolean result;
		if ("UNLOCKED".equals(isLocked)) {
            result = false;
		}else {
			result = true;
		}

		return result;
	}
@Override
public void lockFile(FileVO file) {
	int lockedBy = file.getLockedBy();
	int fileNo = file.getFileNo();
	
	mapper.lockFile(lockedBy,fileNo);
}
@Override
public FileVO getLockedFileByUser(int lockedBy) {
	return mapper.getLockedFileByUser(lockedBy);
}
@Override
public void unlockFile(FileVO file) {
	int lockedBy = file.getLockedBy();
	mapper.unlockFile(lockedBy);
	
}
}

