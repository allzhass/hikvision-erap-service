package kz.bdl.erapservice.service;

public interface ErapService {
    String makeSoap(String xml);
    String sendViolation(String soap);
}
