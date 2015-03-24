package cn.com.janssen.dsr;

import cn.com.janssen.dsr.repository.MedicineRepository;
import org.h2.tools.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.mail.MailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@ComponentScan
@EnableAutoConfiguration
@EnableScheduling
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class Application extends WebMvcConfigurerAdapter {
    private static final Logger LOGGER = LoggerFactory.getLogger(Application.class);

    @Configuration
    @Profile("dev")
    public static class DevelopmentConfiguration {
        /**
         * H2 DB can be viewed with the browser. Open the url "localhost:8082", JDBC URL is "jdbc:h2:mem:testdb".
         */
        @Bean
        Server h2WebConsole() throws Exception {
            return Server.createWebServer().start();
        }
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/login").setViewName("login");
        registry.addViewController("/").setViewName("record/list");
        registry.addViewController("/user").setViewName("user/index");
    }

    @Bean
    public MailSender mailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("localhost");
        mailSender.setPort(3025);
        mailSender.setUsername("foo");
        mailSender.setPassword("bar");
        Properties javaMailProperties = new Properties();
        javaMailProperties.setProperty("mail.smtp.auth", "true");
        javaMailProperties.setProperty("mail.smtp.starttls.enable", "true");
        mailSender.setJavaMailProperties(javaMailProperties);
        return mailSender;
    }

    @Bean
    public MedicineRepository medicineRepository() {
        return MedicineRepository.getInstance();
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}

@Component
@Order(Ordered.LOWEST_PRECEDENCE - 8)
class ApplicationSecurity extends WebSecurityConfigurerAdapter {
    private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationSecurity.class);

    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        super.configure(http);
        http.formLogin()
                .loginPage("/login")
                .permitAll()
                .and()
                .csrf().disable();
    }

    @Autowired
    public void configureAuth(DataSource dataSource, AuthenticationManagerBuilder auth) throws Exception {
        LOGGER.info("Start to configure security");
        auth.userDetailsService(userDetailsService);
    }
}