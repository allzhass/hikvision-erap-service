package kz.bdl.erapservice.service.impl;

import kz.bdl.erapservice.entity.CertificateData;
import kz.bdl.erapservice.service.CertificateService;
import kz.bdl.erapservice.signature.secretstorage.BundleProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CertificateServiceImpl implements CertificateService {
    private final BundleProvider bundleProvider;

    @Override
    public void initializeCertificates(List<CertificateData> certificates) {
        for (CertificateData certificate : certificates) {
            try {
                initializeCertificate(certificate);
            } catch (Exception e) {
                log.error("Failed to initialize certificate with ID {}: {}", certificate.getId(), e.getMessage());
            }
        }
    }

    @Override
    public void initializeCertificate(CertificateData certificate) {
        try {
            bundleProvider.initBundleCustom(certificate.getId(), certificate.getCert(), certificate.getCertpwd());
            log.info("Successfully initialized certificate with ID {}", certificate.getId());
        } catch (Exception e) {
            log.error("Failed to initialize certificate with ID {}: {}", certificate.getId(), e.getMessage());
            throw new RuntimeException("Failed to initialize certificate", e);
        }
    }
} 