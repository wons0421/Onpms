package kr.co.onandon.onpms;

import kr.co.onandon.onpms.controller.MberController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class OnpmsApplicationTests {

    @Autowired
    private MberController mberController;

    @Test
    void contextLoads() throws Exception {
    }

}
