package kz.bdl.erapservice.service;

public interface SignService {
    String signXml(String document);
    String signSoap(String document);
}
