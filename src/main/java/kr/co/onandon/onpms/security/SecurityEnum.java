package kr.co.onandon.onpms.security;

public class SecurityEnum {
    public enum TokenKey {
        JWT_BASIC("ONANDON-INFOMATION-JWT-SECRET-TOKEN"),
        JWT_REFRESH("ONANDON-INFOMATION-JWT-REFRESH-TOKEN");

        private final String value;

        TokenKey(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    public enum ValidateResult {
        OK, DIFFERENT_KEY, ERROR
    }

    public enum ExpiredTime {
        BASIC_EXPIRE_TIME(10),
        REFRESH_EXPIRE_TIME(60 * 10);

        private final int time;

        ExpiredTime(int time) {
            this.time = time;
        }

        public int getTime() {
            return time;
        }
    }
}
