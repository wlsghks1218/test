package org.codesync.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class TableVO {
    private String code;        
    private String erdNo;
    private String userNo;
    private String erdtableNo;        
    private String xaxis; // 필드명 그대로 사용
    private String yaxis; // 필드명 그대로 사용
    private String tableName;    
    private String id;

    @Override
    public String toString() {
        return "ChatContentVO [code=" + code + ", erdNo=" + erdNo + ", userNo=" + userNo + ", erdtableNo=" + erdtableNo + ""
                + ", xAxis=" + xaxis + ", yAxis=" + yaxis + ", tableName=" + tableName + ", id=" + id + "]";
    }
}

