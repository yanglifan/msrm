package cn.com.janssen.dsr.web

import cn.com.janssen.dsr.Application
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.web.WebAppConfiguration
import spock.lang.Specification

@WebAppConfiguration
@ContextConfiguration(classes = Application.class)
class HospitalResourceSpec extends Specification {
}
