package kz.bdl.erapservice.service.impl;

import kz.bdl.erapservice.entity.Auto;
import kz.bdl.erapservice.repository.AutoRepository;
import kz.bdl.erapservice.service.AutoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AutoServiceImpl implements AutoService {
    @Autowired
    private AutoRepository autoRepository;

    @Override
    public boolean isSendAutoViolation(String plateNumber) {
        List<Auto> autos = autoRepository.findByPlateNumber(plateNumber);

        if (autos.size() > 0 && autos.get(0) != null) {
            return autos.get(0).getIsSendViolation();
        } else {
            return true;
        }
    }
}
