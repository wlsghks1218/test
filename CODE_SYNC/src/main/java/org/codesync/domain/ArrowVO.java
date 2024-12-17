package org.codesync.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ArrowVO {
	private String code;        
    private String erdNo;
    private String erdArrowNo;        
    private String startXaxis; // 필드명 그대로 사용
    private String startYaxis; // 필드명 그대로 사용
    private String endXaxis; // 필드명 그대로 사용
    private String endYaxis; // 필드명 그대로 사용
    private String startId;
    private String endId;

}
