package cn.com.janssen.dsr.repository

import cn.com.janssen.dsr.Application
import cn.com.janssen.dsr.domain.Role
import cn.com.janssen.dsr.domain.User
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.web.WebAppConfiguration
import spock.lang.Specification

@WebAppConfiguration
@ContextConfiguration(classes = Application.class)
class UserRepositorySpec extends Specification {
    static final Logger LOGGER = LoggerFactory.getLogger(UserRepositorySpec.class)

    @Autowired
    UserRepository userRepository

    def "List all representatives by the manager"() {
        given:
        User manager1 = new User(username: 'manager1', password: 'password', email: 'manager1@example.com')
        userRepository.save(manager1)
        User manager2 = new User(username: 'manager2', password: 'password', email: 'manager2@example.com')
        userRepository.save(manager2)

        (1..5).each {
            LOGGER.debug("Create Representative ${it} for test")
            User rep = new User(username: "rep${it}", password: "password", email: "rep${it}@example.com")
            rep.roles = [Role.DSR]
            rep.manager = manager1
            userRepository.save(rep)
        }

        User rep = new User(username: "repAnother", password: "password", email: "repAnother@example.com")
        rep.roles = [Role.DSR]
        rep.manager = manager2
        userRepository.save(rep)

        when:
        Page<User> representatives = userRepository.findAllByManager(manager1, new PageRequest(0, 3))

        then:
        representatives.totalElements == 5
        representatives.content.size() == 3
    }
}
