package com.example.demo.handler;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;


import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@RestController
@RequestMapping("/api/grafana")
public class grafanaProxyController {

   @Value("${grafana.url}")
   private String grafanaUrl;

   @Value("${grafana.token}")
   private String grafanaToken;

   private final RestTemplate restTemplate = new RestTemplate();


   @GetMapping("/dashboard/{uid}")
   public ResponseEntity<String> getDashboard(@PathVariable String uid) {
      String targetUrl = grafanaUrl + "/api/dashboards/uid/" + uid;


      HttpHeaders headers = new HttpHeaders();
      headers.set("Authorization", "Bearer " + grafanaToken);
      headers.set("Accept", "application/json");


      HttpEntity<Void> entity = new HttpEntity<>(headers);


      ResponseEntity<String> response = restTemplate.exchange(targetUrl, HttpMethod.GET, entity, String.class);


      return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
   }


   @GetMapping("/embed/{uid}")
   public ResponseEntity<String> getEmbedUrl(@PathVariable String uid) {
      String embedUrl = grafanaUrl + "/d/" + uid + "/f09f9a80-solog-metric-dashboard?orgId=1&from=now-15m&to=now";
      return ResponseEntity.ok(embedUrl);
   }


   @GetMapping("/**")
   public ResponseEntity<byte[]> proxyAll(HttpServletRequest request) {
      String path = request.getRequestURI().replace("/api/grafana", "");
      String targetUrl = grafanaUrl + path + (request.getQueryString() != null ? "?" + request.getQueryString() : "");


      HttpHeaders headers = new HttpHeaders();
      headers.set("Authorization", "Bearer " + grafanaToken);
      headers.set("Accept", "*/*");


      HttpEntity<Void> entity = new HttpEntity<>(headers);


      ResponseEntity<byte[]> response = restTemplate.exchange(targetUrl, HttpMethod.GET, entity, byte[].class);


      // iframe 보안을 위해 X-Frame-Options 제거
      HttpHeaders proxyHeaders = new HttpHeaders();
      response.getHeaders().forEach(proxyHeaders::put);
      proxyHeaders.remove("X-Frame-Options");


      return ResponseEntity.status(response.getStatusCode()).headers(proxyHeaders).body(response.getBody());
   }
}
