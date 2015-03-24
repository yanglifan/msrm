package cn.com.janssen.dsr.web;

import cn.com.janssen.dsr.domain.Medicine;
import cn.com.janssen.dsr.repository.MedicineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

@RestController
@RequestMapping("/api/medicines")
public class MedicineResource {
    @Autowired
    private MedicineRepository medicineRepository;

    @RequestMapping(method = RequestMethod.GET)
    @Transactional(readOnly = true)
    public Collection<Medicine> list() {
        return medicineRepository.findAll();
    }
}
