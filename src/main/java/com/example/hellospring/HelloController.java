package com.example.hellospring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@RestController
@EnableDiscoveryClient
@RefreshScope

public class HelloController {
    @Autowired
    private DiscoveryClient discoveryClient;

    @Value("${message}")
    private String configmessage = "hello";

    @RequestMapping("/")
    public String index() {
        return "Greetings from Azure Spring Cloud!";
    }

    @ResponseBody
    @RequestMapping(value = "/showAllServiceIds", method = RequestMethod.GET)
    public String showAllServiceIds() {

        List<String> serviceIds = this.discoveryClient.getServices();

        if (serviceIds == null || serviceIds.isEmpty()) {
            return "No services found!";
        }
        String html = "<h3>Service Ids:</h3>";
        for (String serviceId : serviceIds) {
            List<ServiceInstance> instances = this.discoveryClient.getInstances(serviceId);
            for (ServiceInstance serviceInstance : instances) {

                html += "<h3>Instance: " + serviceInstance.getUri() + "</h3>";
                html += "Service ID: " + serviceId + "<br>";
                html += "Host: " + serviceInstance.getHost() + "<br>";
                html += "Port: " + serviceInstance.getPort() + "<br>";
                html += "Schema: " + serviceInstance.getScheme() + " ! Metadata: " + serviceInstance.getMetadata() + " ! "  + serviceInstance.getServiceId() + "<br>";
                html += "Other: " + serviceInstance.toString() + "<br>";
            }
        }
        return html;
    }

    @RequestMapping("/getIndex")
    public String getIndex() {
        List<ServiceInstance> sis = this.discoveryClient.getInstances("DEMO");
        ServiceInstance si = sis.get(0);
        String baseUrl = si.getUri() + "/";
        ResponseEntity<String> response =  null;
        try {
            RestTemplate restTemplate = new RestTemplate();
            response = restTemplate.exchange(baseUrl, HttpMethod.GET, null, String.class);
        }catch (Exception e){
            System.out.println(e);
        }

        System.out.println(si.toString());
        System.out.println(configmessage);

        return response.getBody();
    }


    @RequestMapping("/getConfig")
    public String getConfig() {

        System.out.println(configmessage);

        return configmessage;
    }

}
