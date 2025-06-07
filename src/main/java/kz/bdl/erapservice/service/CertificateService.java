package kz.bdl.erapservice.service;

import kz.bdl.erapservice.entity.CertificateData;
import kz.bdl.erapservice.signature.secretstorage.BundleProvider;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface CertificateService {
    void initializeCertificates(List<CertificateData> certificates);
    void initializeCertificate(CertificateData certificate);
} 