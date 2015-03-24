package cn.com.janssen.dsr.repository

import cn.com.janssen.dsr.Application
import cn.com.janssen.dsr.domain.Hospital
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.web.WebAppConfiguration
import spock.lang.Specification

@WebAppConfiguration
@ContextConfiguration(classes = Application.class)
class HospitalRepositorySpec extends Specification {
    @Autowired
    HospitalRepository hospitalRepository

    def 'Find hospitals by the province'() {
        given:
        hospitalRepository.save(new Hospital(name: 'hospital a', serialNumber: '0001', province: 'Beijing'))
        hospitalRepository.save(new Hospital(name: "hospital b", serialNumber: "0002", province: "Beijing"))
        hospitalRepository.save(new Hospital(name: "hospital c", serialNumber: "0003", province: "Tianjin"))
        hospitalRepository.save(new Hospital(name: "hospital d", serialNumber: "0004", province: "Hebei"))

        when:
        def hList = hospitalRepository.findByProvince("Beijing")

        then:
        hList.size() == 2
    }
}
