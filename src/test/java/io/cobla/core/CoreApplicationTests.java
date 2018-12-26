package io.cobla.core;

import io.cobla.core.domain.ApiWallet;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CoreApplicationTests {

    @Test
    public void contextLoads() {
        int i =1000;

        for(int k=0;k<10000;k++){
            if(k/i==0){
                System.out.print(i);
            }

        }
    }

}
