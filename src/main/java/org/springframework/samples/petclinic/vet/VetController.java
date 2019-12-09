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
package org.springframework.samples.petclinic.vet;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author Juergen Hoeller
 * @author Mark Fisher
 * @author Ken Krebs
 * @author Arjen Poutsma
 */
@Controller
class VetController {

    private final VetRepository vets;

    public VetController(VetRepository clinicService) {
        this.vets = clinicService;
    }

    @GetMapping("/vets.html")
    public String showVetList(Map<String, Object> model) {
        // Here we are returning an object of type 'Vets' rather than a collection of Vet
        // objects so it is simpler for Object-Xml mapping
        Vets vets = new Vets();
        vets.getVetList().addAll(this.vets.findAll());
        model.put("vets", vets);
        return "vets/vetList";
    }

    @GetMapping({ "/vets" })
    public @ResponseBody Vets showResourcesVetList() {
        // Here we are returning an object of type 'Vets' rather than a collection of Vet
        // objects so it is simpler for JSon/Object mapping
        Vets vets = new Vets();
        vets.getVetList().addAll(this.vets.findAll());
        return vets;
    }

    /**
     *
     */
    @GetMapping("/vets/on-call")
    public @ResponseBody List<String> getVetsOnCall(){
        return Arrays.asList("George", "Simon");

        /*
        final String endpoint = this.RESOURCE_SERVER_BASE_URI+"/api/data";

        // Implementation: https://stackoverflow.com/a/45717851/10265855
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

            return template
                .exchange(new URI(endpoint), HttpMethod.GET, httpEntity, Map.class)
                .getBody();

        } catch (Exception e) {
            e.printStackTrace();
        }
         */
    }

    public KeyStore getKeystore() throws KeyStoreException, NoSuchAlgorithmException,
            IOException, CertificateException {
        KeyStore keyStore = KeyStore.getInstance("jks");
        keyStore.load(
                new FileInputStream(System.getenv("JAVA_HOME")+"/lib/security/client.jks"),
                "changeit".toCharArray());
        return keyStore;
    }


}
