package hello.springtx2.apply;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.annotation.PostConstruct;

@SpringBootTest
public class InitTxTest {

    @Autowired Hello hello;

    @Test
    void go() {
        //초기화 코드는 스프링이 초기화 시점에 호출된다.
    }

    @TestConfiguration
    static class InitTxTestConfig {
        @Bean
        Hello hello() {
            return new Hello();
        }
    }

    @Slf4j
    static class Hello {

        //@PostConstruct가 먼저 실행되고 @Transactional이 실행되어서 초기화 코드에는 @Transactional이 적용되지 않는다.
        @PostConstruct
        @Transactional
        public void initV1() {
            boolean isActive = TransactionSynchronizationManager.isActualTransactionActive();
            log.info("Hello init @PostConstruct tx active={}", isActive);
        }

        //위 코드 해결법.f
        //@EventListener(ApplicationReadyEvent.class) 이 코드는 스프링이 완전히 다 뜨고 초기화가 된다.
        //즉 Transaction aop가 실행이 되고 초기화가 됨. 그래서 초기화에도 트랜잭션이 적용됨.
        @EventListener(ApplicationReadyEvent.class)
        @Transactional
        public void initV2() {
            boolean isActive = TransactionSynchronizationManager.isActualTransactionActive();
            log.info("Hello init @PostConstruct tx active={}", isActive);
        }
    }
}
