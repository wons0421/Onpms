package kr.co.onandon.onpms.security;

import kr.co.onandon.onpms.entity.Mber;
import kr.co.onandon.onpms.repository.MberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final MberRepository repository;

    @Override
    public UserDetails loadUserByUsername(String mberSn) throws UsernameNotFoundException {
        CustomUserDetails returnValue = null;

        Optional<Mber> mber = repository.findById(Integer.valueOf(mberSn));

        if (mber.isPresent()) {
            returnValue = new CustomUserDetails(mber.get());
        }


        return returnValue;
    }

}
