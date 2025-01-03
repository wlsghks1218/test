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
@Override
public boolean saveCode(int fileNo, String content) {
    try {
        // fileNo에 해당하는 파일이 DB에 존재하는지 확인하고, 없으면 업데이트하지 않음
        FileVO file = new FileVO();
        file.setFileNo(fileNo);
        file.setContent(content);

        // 파일을 업데이트하는 로직
        int result = mapper.updateCode(file); // 업데이트 메서드 호출

        if (result > 0) {
            return true; // 업데이트 성공
        } else {
            return false; // 파일이 존재하지 않으면 업데이트하지 않음
        }
    } catch (Exception e) {
        log.error("Error saving code for fileNo: " + fileNo, e);
        return false; // 오류 발생 시 false 반환
    }
}

@Override
public int getLockedFile(int fileNo, int userNo) {
    Integer lockedBy = mapper.getLockedBy(fileNo);

    // 파일이 잠금 상태가 아닌 경우 (1: 잠금 상태 아님)
    if (lockedBy == null || lockedBy == 0) {
        return 1; // 잠금 상태 아님
    }

    // 파일이 잠겨 있고, 잠근 사용자가 본인인 경우 (2: 본인이 잠금)
    if (lockedBy == userNo) {
        return 2; // 본인이 잠금 상태
    }

    // 파일이 잠겨 있고, 잠근 사용자가 본인이 아닌 경우 (3: 다른 사용자가 잠금)
    return 3; // 다른 사용자가 잠금 상태
}
@Override
public boolean isFileLockedByAnotherUser(int fileNo, int userNo) {
    // DB에서 fileNo에 해당하는 lockedBy 값을 가져옴
    Integer lockedBy = mapper.getLockedBy(fileNo); // fileMapper는 DB와 연결된 매퍼 객체

    // lockedBy가 0이거나 null이면 잠근 사람이 없으므로 false를 반환
    if (lockedBy == null || lockedBy == 0) {
        return false;
    }

    // lockedBy가 userNo와 같으면 본인이 잠근 것이므로 false를 반환
    if (lockedBy.equals(userNo)) {
        return false;
    }

    // lockedBy가 userNo와 다르면 다른 사용자가 잠근 것이므로 true를 반환
    return true;
}
@Override
public boolean checkFileLockStatus(int fileNo, int userNo) {
    // 매퍼 호출을 통해 DB에서 잠금 상태 확인
    int count = mapper.checkFileLockStatus(fileNo, userNo);
    
    // count가 1이면 잠금 상태가 true, 0이면 false
    return count > 0;
}
@Override
public List<Integer> getFolderNosByCodeSyncNo(int codeSyncNo) {
	// TODO Auto-generated method stub
	return mapper.getFolderNosByCodeSyncNo(codeSyncNo);
}

@Override
public List<Integer> getFileNosByFolderNo(Integer folderNo) {
	// TODO Auto-generated method stub
	  return mapper.getFileNosByFolderNo(folderNo);
}
@Override
public boolean deleteFoldersAndFiles(List<Integer> folderNos, List<Integer> fileNos) {
    try {
        // 폴더와 파일을 삭제하는 로직
    	for (Integer fileNo : fileNos) {
    		mapper.deleteByFileNo(fileNo);
    	}
        for (Integer folderNo : folderNos) {
            mapper.deleteByFolderNo(folderNo);
        }

        return true;
    } catch (Exception e) {
        log.error("Error deleting folders and files", e);
        return false;
    }
}
@Override
public void changeFolderName(String newName, String folderName, int codeSyncNo) {
	mapper.changeFolderName(newName,folderName,codeSyncNo);
	
}
@Override
public void changeFileName(String newName, String fileName, int folderNo) {
	mapper.changeFileName(newName,fileName,folderNo);
	
}
@Override
public void deleteFolder(int folderNo) {
    List<FileVO> files = mapper.getFolderChildFile(folderNo);

    // 자식 파일이 존재하는 경우 삭제
    if (files != null && !files.isEmpty()) {
        mapper.deleteFileByFolderNo(folderNo);
    }

    // 폴더 삭제
    mapper.deleteByFolderNo(folderNo);
}
@Override
public void createFile(FileVO file) {
	mapper.createFile(file);
	
}
@Override
public void createFolder(FileVO file) {
	mapper.createFolder(file);
	
}
@Transactional
@Override
public void pastefolder(FileVO file) {
	int folderNo = file.getFolderNo();
	System.out.println("기존 folderNo" + folderNo);

	  // 폴더 붙여넣기 (생성)
	mapper.pasteFolder(file);
	 System.out.println("생성된 folderNo: " + file.getFolderNo()); // 자동 생성된 folderNo 출력
    // folderPath 가져오기
    String folderPath = file.getFolderPath();

    // 자식 파일 및 폴더 정보 조회
    List<FileVO> fvoList = mapper.getFolderChildFile(folderNo);
    
    System.out.println(fvoList);


    // "Root/" 제거 및 "/" 추가
    String basePath = folderPath.replaceFirst("^Root/", "") + "/";

    // fvoList 순회하며 filePath 생성 및 출력
    for (FileVO childFile : fvoList) {
        String fileName = childFile.getFileName(); // 파일 이름 가져오기
        String filePath = basePath + fileName;    // filePath 생성
        childFile.setFilePath(filePath);          // filePath 설정

        System.out.println("Updated FileVO: " + childFile);
       
     mapper.pasteFile(childFile.getFilePath(),file.getFolderNo(),childFile.getFileName(),childFile.getContent(),childFile.getExtension(),file.getUserNo());
    }

    // 필요 시 업데이트된 fvoList를 데이터베이스에 저장하거나 추가 처리
}
@Override
public void pasteFile(FileVO file) {
	FileVO fvo = mapper.getFileData(file);
	String folderPath = file.getFolderPath();
	 String basePath = folderPath.replaceFirst("^Root/", "") + "/";
	 String fileName = file.getFileName();
	 String filePath = basePath + fileName; 
	mapper.pasteFile(filePath, file.getNewFolderNo(), fileName, fvo.getContent(), fvo.getExtension(), file.getUserNo());
	
}
@Override
public void deleteFile(FileVO file) {
	mapper.deleteFile(file);
	
}
}


