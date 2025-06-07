package kz.bdl.erapservice.service;

import kz.bdl.erapservice.signature.secretstorage.BundleProvider;

public interface SignService {
    String signXml(String document, BundleProvider.BundleBySignAlg signBundle);
    String signSoap(String document, BundleProvider.BundleBySignAlg signBundle);
}