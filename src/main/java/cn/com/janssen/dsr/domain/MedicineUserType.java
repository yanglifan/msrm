package cn.com.janssen.dsr.domain;

import cn.com.janssen.dsr.repository.MedicineRepository;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.annotations.TypeDef;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.type.StringType;
import org.hibernate.usertype.UserType;

import javax.persistence.MappedSuperclass;
import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@MappedSuperclass
@TypeDef(defaultForType = Medicine.class, typeClass = MedicineUserType.class)
public class MedicineUserType implements UserType {

    @Override
    public int[] sqlTypes() {
        return new int[]{StringType.INSTANCE.sqlType()};
    }

    @Override
    public Class returnedClass() {
        return Medicine.class;
    }

    @Override
    public boolean equals(Object x, Object y) throws HibernateException {
        if (!(x instanceof Medicine) || !(y instanceof Medicine))
            return false;

        Medicine medicineX = (Medicine) x;
        Medicine medicineY = (Medicine) y;

        return medicineX.equals(medicineY);
    }

    @Override
    public int hashCode(Object x) throws HibernateException {
        return x.hashCode();
    }

    @Override
    public Object nullSafeGet(ResultSet rs, String[] names, SessionImplementor session, Object owner) throws HibernateException, SQLException {
        String value = (String) StringType.INSTANCE.get(rs, names[0], session);
        return MedicineRepository.getInstance().findByName(value);
    }

    @Override
    public void nullSafeSet(PreparedStatement st, Object value, int index, SessionImplementor session) throws HibernateException, SQLException {
        if (value == null)
            StringType.INSTANCE.set(st, null, index, session);
        else
            StringType.INSTANCE.set(st, ((Medicine) value).getName(), index, session);
    }

    @Override
    public Object deepCopy(Object value) throws HibernateException {
        return null;
    }

    @Override
    public boolean isMutable() {
        return false;
    }

    @Override
    public Serializable disassemble(Object value) throws HibernateException {
        return null;
    }

    @Override
    public Object assemble(Serializable cached, Object owner) throws HibernateException {
        return null;
    }

    @Override
    public Object replace(Object original, Object target, Object owner) throws HibernateException {
        return original;
    }
}
