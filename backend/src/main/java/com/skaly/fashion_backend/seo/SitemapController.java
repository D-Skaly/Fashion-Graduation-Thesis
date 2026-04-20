package com.skaly.fashion_backend.seo;

import com.skaly.fashion_backend.product.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.format.DateTimeFormatter;

@RestController
@RequiredArgsConstructor
public class SitemapController {

    private final ProductRepository productRepository;

    @GetMapping(value = "/sitemap.xml", produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<String> sitemap() {
        StringBuilder sitemap = new StringBuilder();
        sitemap.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        sitemap.append("<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">\n");

        // Homepage
        sitemap.append("  <url>\n");
        sitemap.append("    <loc>https://fashion-store.com/</loc>\n");
        sitemap.append("    <changefreq>daily</changefreq>\n");
        sitemap.append("    <priority>1.0</priority>\n");
        sitemap.append("  </url>\n");

        // ProductEntity pages
        productRepository.findAll().forEach(product -> {
            sitemap.append("  <url>\n");
            sitemap.append("    <loc>https://fashion-store.com/products/").append(product.getSlug()).append("</loc>\n");
            sitemap.append("    <lastmod>").append(product.getUpdatedAt().format(DateTimeFormatter.ISO_DATE_TIME)).append("</lastmod>\n");
            sitemap.append("    <changefreq>weekly</changefreq>\n");
            sitemap.append("    <priority>0.8</priority>\n");
            sitemap.append("  </url>\n");
        });

        sitemap.append("</urlset>");

        return ResponseEntity.ok(sitemap.toString());
    }
}
