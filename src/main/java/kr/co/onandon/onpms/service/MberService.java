package kr.co.onandon.onpms.service;

import kr.co.onandon.onpms.dto.MberDto;
import org.springframework.http.ResponseEntity;

public interface MberService {

    public ResponseEntity join(MberDto.JoinRequestDto params) throws Exception;

    public ResponseEntity login(MberDto.LoginRequestDto params) throws Exception;

    public ResponseEntity refresh(int mberSn) throws Exception;
}
