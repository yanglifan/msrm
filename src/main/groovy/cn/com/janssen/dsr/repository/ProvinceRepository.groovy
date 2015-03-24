package cn.com.janssen.dsr.repository

import cn.com.janssen.dsr.domain.Province
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Repository

import javax.annotation.PostConstruct
import java.nio.file.Paths

@Repository
class ProvinceRepository {
    static final Logger LOGGER = LoggerFactory.getLogger(ProvinceRepository.class)

    List<Province> provinceList

    @PostConstruct
    void init() {
        def provinces = new XmlSlurper().parse(getClass().getResourceAsStream("/provinces.xml"))

        provinceList = []
        provinces.province.each {
            LOGGER.info('Add {} (code: {})', it.@name, it.@id)
            provinceList << new Province(Integer.parseInt(it.@id.text()), it.@name.text())
        }
    }
}
