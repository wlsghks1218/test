package org.codesync.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class MemoVO {
	private String code;        
    private String erdNo;
    private String memoNo;        
    private String xaxis; // 필드명 그대로 사용
    private String yaxis; // 필드명 그대로 사용
    private String memoTitle;
    private String content;
    private String id;

    @Override
    public String toString() {
        return "ChatContentVO [code=" + code + ", erdNo=" + erdNo + ", content=" + content + ", memoNo=" + memoNo + ""
                + ", xAxis=" + xaxis + ", yAxis=" + yaxis + ", memoTitle=" + memoTitle + ", id=" + id + "]";
    }
}
