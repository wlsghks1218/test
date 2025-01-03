package org.codesync.domain;

import java.util.List;

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
    private String erdTableNo;        
    private String xaxis; // 필드명 그대로 사용
    private String yaxis; // 필드명 그대로 사용
    private String tableName;    
    private String id;
    private List<TableFieldsVO> fields;

    @Override
    public String toString() {
        return "ChatContentVO [code=" + code + ", erdNo=" + erdNo + ", userNo=" + userNo + ", erdTableNo=" + erdTableNo + ""
                + ", xAxis=" + xaxis + ", yAxis=" + yaxis + ", tableName=" + tableName + ", id=" + id + "]";
    }
}

