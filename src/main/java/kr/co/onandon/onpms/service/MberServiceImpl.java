package kr.co.onandon.onpms.service;

import kr.co.onandon.onpms.dto.MberDto;
import kr.co.onandon.onpms.entity.Mber;
import kr.co.onandon.onpms.jwt.JwtProvider;
import kr.co.onandon.onpms.mapper.MberMapper;
import kr.co.onandon.onpms.repository.MberRepository;
import kr.co.onandon.onpms.security.SecurityEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MberServiceImpl implements MberService {

    @Autowired
    private final MberRepository mberRepository;

    @Autowired
    private final BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private final JwtProvider jwtProvider;

    @Autowired
    private final RedisTemplate redisTemplate;

    @Override
    public ResponseEntity join(MberDto.JoinRequestDto params) throws Exception {
        boolean isExistsId = mberRepository.existsByMberId(params.getMberId());

        if (isExistsId) {
            throw new Exception("이미 존재하는 아이디 입니다.");
        }

        String rawPassword = params.getPw();
        String encPassword = passwordEncoder.encode(rawPassword);
        params.setPw(encPassword);

        Mber mber = MberMapper.INSTANCE.joinReqToMber(params);

        mber.setAuth("A00");

        mberRepository.save(mber);

        return ResponseEntity.ok("ok");
    }

    @Override
    public ResponseEntity login(MberDto.LoginRequestDto params) throws Exception {

        String mberId = params.getMberId();

        Mber findMber = mberRepository.findByMberId(mberId);

        if (findMber == null) {
            throw new IllegalArgumentException("없는 사용자입니다.");
        }

        if (!passwordEncoder.matches(params.getPw(), findMber.getPw())) {
            throw new IllegalArgumentException("비밀번호를 확인하세요.");
        }

        ValueOperations<String, String> operations
            = redisTemplate.opsForValue();

        // token 발급
        String token = jwtProvider.getToken(findMber.getMberSn(), SecurityEnum.TokenKey.JWT_BASIC);
        operations.set(token, ObjectUtils.getDisplayString(findMber.getMberSn()));
        redisTemplate
            .expire(token,
                    Duration.ofSeconds(SecurityEnum
                                           .ExpiredTime
                                           .BASIC_EXPIRE_TIME.getTime())
            );

        // refresh token 발급
        String refreshToken = jwtProvider.getToken(findMber.getMberSn(), SecurityEnum.TokenKey.JWT_REFRESH);
        operations.set(refreshToken, ObjectUtils.getDisplayString(findMber.getMberSn()));
        redisTemplate
            .expire(refreshToken,
                    Duration.ofSeconds(SecurityEnum
                                           .ExpiredTime
                                           .REFRESH_EXPIRE_TIME.getTime())
            );

        Map<String, Object> result = new HashMap<>();

        result.put("token", token);
        result.put("refreshToken", refreshToken);
        result.put("mberSn", findMber.getMberSn());

        return ResponseEntity.ok(result);
    }

    @Override
    public ResponseEntity refresh(int mberSn) throws Exception {
        String token = jwtProvider.getToken(mberSn, SecurityEnum.TokenKey.JWT_BASIC);
        ValueOperations<String, String> operations
            = redisTemplate.opsForValue();

        operations.set(token, ObjectUtils.getDisplayString(mberSn));

        return ResponseEntity.ok(token);
    }
}
