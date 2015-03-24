package cn.com.janssen.dsr.report.processor;

import cn.com.janssen.dsr.domain.User;
import cn.com.janssen.dsr.domain.VisitRecord;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Ali on 2014/6/14.
 */
public class DsrCollector implements VisitRecordProcessor<String, String> {
    private Set<User> dsrSet = new HashSet<>();

    /**
     * The key is the DSR username.
     *
     * @param key DSR username
     * @return DSR username
     */
    @Override
    public String getResult(String key) {
        return key;
    }

    @Override
    public void accept(VisitRecord visitRecord) {
        dsrSet.add(visitRecord.getDsr());
    }

    public Set<User> getAll() {
        return dsrSet;
    }
}
