package com.arp.dimdimdigaana;

import com.arp.dimdimdigaana.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.hibernate.autoconfigure.HibernateJpaAutoConfiguration;
import org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration;
import org.springframework.boot.jdbc.autoconfigure.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

/**
 * Smoke-test: verifies the Spring application context assembles correctly
 * without requiring a real database connection.
 */
@SpringBootTest
@EnableAutoConfiguration(exclude = {
        DataSourceAutoConfiguration.class,
        DataSourceTransactionManagerAutoConfiguration.class,
        HibernateJpaAutoConfiguration.class
})
class DimdimdigaanaApplicationTests {

    @MockitoBean
    private UserRepository userRepository;

    @Test
    void contextLoads() {
        // context wires up without errors — that is the assertion
    }

}
