package cn.com.janssen.dsr.repository

import spock.lang.Specification

class ProvinceRepositorySpec extends Specification {
    def 'Load provinces from XML'() {
        given:
        ProvinceRepository provinceRepository = new ProvinceRepository()

        when:
        provinceRepository.init()

        then:
        provinceRepository.provinceList.size() == 36
        provinceRepository.provinceList[0].code == 11
    }
}
