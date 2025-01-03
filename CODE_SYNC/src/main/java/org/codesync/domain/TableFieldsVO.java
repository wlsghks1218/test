package org.codesync.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class TableFieldsVO {
	private String code;        
    private String fieldId;
    private String isPrimary;        
    private String field; 
    private String domain; 
    private String type; 
    private String userNo;
    private String id;
}
