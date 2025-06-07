package kz.bdl.erapservice.config;

import kz.bdl.erapservice.entity.CertificateData;
import kz.bdl.erapservice.repository.CertificateDataRepository;
import kz.bdl.erapservice.service.CertificateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class CertificateInitializer {
    private final CertificateDataRepository certificateDataRepository;
    private final CertificateService certificateService;

    @EventListener(ApplicationReadyEvent.class)
    public void initializeCertificates() {
        log.info("Starting certificate initialization...");
        List<CertificateData> certificates = certificateDataRepository.findAll();
        if (certificates.isEmpty()) {
            log.warn("No certificates found in the database");
            return;
        }
        
        log.info("Found {} certificates to initialize", certificates.size());
        certificateService.initializeCertificates(certificates);
        log.info("Certificate initialization completed");
    }

    @Scheduled(fixedRateString = "${certificate.refresh.interval:10800000}") // Default: 3 hours in milliseconds
    public void refreshCertificates() {
        log.info("Starting scheduled certificate refresh...");
        List<CertificateData> certificates = certificateDataRepository.findAll();
        if (certificates.isEmpty()) {
            log.warn("No certificates found in the database during refresh");
            return;
        }
        
        log.info("Refreshing {} certificates", certificates.size());
        certificateService.initializeCertificates(certificates);
        log.info("Certificate refresh completed");
    }
} 