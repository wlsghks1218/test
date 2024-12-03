package org.codesync.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class LoginRequest {
	private String userId, userPw;
	
    @JsonProperty("remember-me")
    private boolean rememberMe;
}
