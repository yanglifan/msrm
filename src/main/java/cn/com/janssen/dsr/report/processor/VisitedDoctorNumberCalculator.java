package cn.com.janssen.dsr.report.processor;

import cn.com.janssen.dsr.domain.VisitRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class VisitedDoctorNumberCalculator implements VisitRecordProcessor<String, String> {
    private static final Logger LOGGER = LoggerFactory.getLogger(VisitedDoctorNumberCalculator.class);

    private Map<String, Set<String>> doctorNamesMap = new HashMap<>();

    @Override
    public String getResult(String dsrName) {
        LOGGER.debug("Query the number of visited doctors for DSR [{}]", dsrName);
        Set<String> doctorNames = doctorNamesMap.get(dsrName);
        return doctorNames == null ? "" : doctorNames.size() + "";
    }

    @Override
    public void accept(VisitRecord visitRecord) {
        String dsrName = visitRecord.getDsr().getUsername();
        Set<String> doctorNames = doctorNamesMap.get(dsrName);
        if (doctorNames == null) {
            doctorNames = new HashSet<String>();
            doctorNamesMap.put(dsrName, doctorNames);
        }

        doctorNames.add(visitRecord.getDoctor().getName());
    }
}
