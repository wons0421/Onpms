package kr.co.onandon.onpms.dto;

import lombok.Data;

public class MberDto {
    @Data
    public static class JoinRequestDto {
        private String mberId;
        private String pw;
        private String name;
    }

    @Data
    public static class LoginRequestDto {
        private String mberId;
        private String pw;
    }
}
