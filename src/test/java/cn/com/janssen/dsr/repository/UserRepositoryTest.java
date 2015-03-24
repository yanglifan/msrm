package cn.com.janssen.dsr.repository;

import cn.com.janssen.dsr.Application;
import cn.com.janssen.dsr.domain.Role;
import cn.com.janssen.dsr.domain.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.Arrays;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = Application.class)
public class UserRepositoryTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserRepositoryTest.class);

    @Autowired
    private UserRepository userRepository;

    @Test
    public void testFindByEmail() throws Exception {
        User user = new User("user", "password");
        user.setEmail("user@example.com");
        user.setRoles(Arrays.asList(Role.DSR));

        userRepository.save(user);

        user = userRepository.findByEmail("user@example.com");

        LOGGER.info(getClass().getSimpleName() + ".testFindByEmail(): assertions");
        assertThat(user.getUsername(), is("user"));
        assertThat(user.getEmail(), is("user@example.com"));
        assertThat(user.getRoles().contains(Role.DSR), is(true));
    }

    @Test
    public void save_and_get_user_then_check_roles() throws Exception {
        User user = new User("user", "password");
        user.setEmail("user@example.com");
        user.setRoles(Arrays.asList(Role.DSR, Role.MANAGER));

        userRepository.save(user);

        user = userRepository.findOne("user");

        assertThat(user.getRoles().contains(Role.DSR), is(true));
        assertThat(user.getRoles().contains(Role.MANAGER), is(true));
    }
}
