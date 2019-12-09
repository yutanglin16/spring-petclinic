/*
 * Copyright 2012-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.samples.petclinic.system;


import org.apache.http.client.HttpClient;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.samples.petclinic.vet.Vet;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Map;

@Controller
class WelcomeController {

    private final String RESOURCE_SERVER_BASE_URI = "https://resource-server-demo.azurewebsites.net";

    @GetMapping("/")
    public String welcome(Map<String, Object> model) {
        List<Vet> vets = getCurrentVetsOnCall();
        vets.forEach(a -> System.out.println(a));
        model.put("vets", vets);
        return "welcome";
    }

    private List<Vet> getCurrentVetsOnCall() {
        final String endpoint = this.RESOURCE_SERVER_BASE_URI+"/api/vets/on-call";

        try {
            final KeyStore keyStore = this.getKeystore();
            TrustStrategy acceptingTrustStrategy = (X509Certificate[] chain, String authType) -> true;

            SSLContext sslContext = SSLContextBuilder
                    .create()
                    .loadKeyMaterial(keyStore,  "changeit".toCharArray())
                    .loadTrustMaterial(keyStore, acceptingTrustStrategy)
                    .build();
            HttpClient client = HttpClients
                    .custom()
                    .setSSLContext(sslContext)
                    .build();

            RestTemplate template = new RestTemplate();
            template.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));

            HttpEntity<?> httpEntity = new HttpEntity(null, new HttpHeaders());

            List<?> response = template
                    .exchange(new URI(endpoint), HttpMethod.GET, httpEntity, List.class)
                    .getBody();

            return (List<Vet>) response;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private KeyStore getKeystore() throws KeyStoreException, NoSuchAlgorithmException,
            IOException, CertificateException {
        KeyStore keyStore = KeyStore.getInstance("jks");
        keyStore.load(
                new FileInputStream(System.getenv("JAVA_HOME")+"/lib/security/client.jks"),
                "changeit".toCharArray());
        return keyStore;
    }
}
