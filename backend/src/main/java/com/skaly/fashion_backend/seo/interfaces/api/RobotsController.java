package com.skaly.fashion_backend.seo.interfaces.api;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RobotsController {

    @GetMapping(value = "/robots.txt", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> robots() {
        StringBuilder robots = new StringBuilder();
        robots.append("User-agent: *\n");
        robots.append("Allow: /\n");
        robots.append("Disallow: /api/\n");
        robots.append("Disallow: /admin/\n");
        robots.append("Disallow: /cart/\n");
        robots.append("Disallow: /checkout/\n");
        robots.append("Disallow: /account/\n");
        robots.append("\n");
        robots.append("Sitemap: https://fashion-store.com/sitemap.xml\n");

        return ResponseEntity.ok(robots.toString());
    }
}
