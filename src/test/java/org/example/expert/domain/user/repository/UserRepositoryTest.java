package org.example.expert.domain.user.repository;

import net.datafaker.Faker;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.enums.UserRole;
import org.example.expert.testconfig.TestSecurityConfig;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

@Import(TestSecurityConfig.class)
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserRepositoryTest {

    @Autowired
    UserRepository userRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    User randomElement = null;

//    @BeforeAll
//    void setUp(){
    @Test
    void insertOneMillionUsersFast() {
        //given
        Faker faker = new Faker();
        List<Object[]> batch = new ArrayList<>();
        int batchSize = 5000;

        List<User> users = new ArrayList<>();
        long flushCnt = 1;
        long start = System.currentTimeMillis();
        for (int i = 0; i < 1_000_000; i++) {
//            User user = (new User(
//                    faker.internet().emailAddress("user" + flushCnt*batchSize + i),
//                    faker.funnyName().name().concat(String.valueOf(i)),
//                    "test_password"+i,
//                    UserRole.ROLE_USER
//            ));
//            users.add(user);

            batch.add(new Object[]{
                    faker.internet().emailAddress("user" + i),
                    faker.funnyName().name().concat(String.valueOf(i)),
                    "test_password"+i,
                    "ROLE_USER"
            });

            if ( i > 0 && i % batchSize == 0) {

                jdbcTemplate.batchUpdate(
                        "INSERT INTO users (email, nickname, password, user_role) VALUES (?, ?, ?, ?)",
                        batch
                );
                batch.clear();

//                if( randomElement == null){
//                    Random random = new Random();
//                    int randomIndex = random.nextInt(batchSize);
//                    randomElement = users.get(randomIndex);
//                }
//
//                userRepository.saveAll(users);
//                userRepository.flush();
//
//                users.clear();
//
//                flushCnt++;
            }
        }
//        if (!users.isEmpty()){
//            userRepository.saveAll(users);
//            userRepository.flush();
//        }
        // 마지막 남은 데이터 처리
        if (!batch.isEmpty()) {
            jdbcTemplate.batchUpdate(
                    "INSERT INTO users (email, nickname, password, user_role) VALUES (?, ?, ?, ?)",
                    batch
            );
        }

        long end = System.currentTimeMillis();
        System.out.println("insert 완료 시간: " + (end - start) + "ms");

    }

    @Test
    void find_performance() {

        String nickname = "Ethel L. Cahall54324";
        long start = System.currentTimeMillis();

        //when
        Optional<User> findUser = userRepository.findByNickname(nickname);
        long end = System.currentTimeMillis();
        System.out.println("조회 시간: " + (end - start) + "ms");

        //then
        assertThat(findUser).isPresent();
        assertThat(findUser.get().getNickname()).isEqualTo(nickname);
    }

}