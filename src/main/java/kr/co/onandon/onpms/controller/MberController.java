package kr.co.onandon.onpms.controller;

import kr.co.onandon.onpms.dto.MberDto;
import kr.co.onandon.onpms.service.MberService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class MberController {
    @Autowired
    private final MberService mberService;

    @PostMapping("/join")
    public ResponseEntity join(@RequestBody MberDto.JoinRequestDto params) throws Exception {
        return mberService.join(params);
    }

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody MberDto.LoginRequestDto params) throws Exception {
        return mberService.login(params);
    }

    @PostMapping("/curDate")
    public ResponseEntity curDate(@RequestBody Map<String, Object> params) {

        Date date = new Date();

        return ResponseEntity.ok(date);
    }

    @PostMapping("/refresh")
    public ResponseEntity refresh(@RequestParam int mberSn) throws Exception {
        return mberService.refresh(mberSn);
    }
}
