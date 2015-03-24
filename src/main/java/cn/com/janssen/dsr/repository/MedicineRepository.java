package cn.com.janssen.dsr.repository;

import cn.com.janssen.dsr.domain.Medicine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

/**
 * The bean definition is in Spring Java configuration.
 */
public class MedicineRepository {
    private static final Logger LOGGER = LoggerFactory.getLogger(MedicineRepository.class);

    private static MedicineRepository instance = new MedicineRepository();

    private final Map<String, Medicine> medicines = new HashMap<>();

    private MedicineRepository()  {
        init();
    }

    public static MedicineRepository getInstance() {
        return instance;
    }

    private void init() {
        try (
                InputStream inputStream = getClass().getResourceAsStream("/medicines.csv");
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))
        ) {
            bufferedReader.lines().forEach(line -> {
                LOGGER.info("Input the medicine: {}", line);
                String[] medicineInfo = line.split(",");
                Medicine medicine = new Medicine(medicineInfo[1]);
                medicine.setId(Integer.valueOf(medicineInfo[0]));
                medicines.put(medicineInfo[1], medicine);
            });
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    public List<Medicine> findAll() {
        return new ArrayList<>(medicines.values());
    }

    public Medicine findByName(String value) {
        return medicines.get(value);
    }
}
