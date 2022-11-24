package kr.co.onandon.onpms.repository;


import kr.co.onandon.onpms.entity.Mber;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MberRepository extends JpaRepository<Mber, Integer> {
    Mber findByMberId(String mberId);
    boolean existsByMberId(String mberId);
}
