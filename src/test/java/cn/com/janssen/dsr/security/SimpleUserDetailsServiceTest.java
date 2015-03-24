package cn.com.janssen.dsr.security;

import cn.com.janssen.dsr.Application;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = Application.class)
@ActiveProfiles("dev")
public class SimpleUserDetailsServiceTest {
    @Autowired
    private UserDetailsService userDetailsService;

    @Test
    public void load_user_by_email() {
        UserDetails userDetails = userDetailsService.loadUserByUsername("user@example.com");
        assertThat(userDetails.getUsername(), is("user"));
    }
}
