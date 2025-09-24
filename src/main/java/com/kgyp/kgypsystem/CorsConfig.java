package com.kgyp.kgypsystem.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;

/**
 * KGYP Sistemi için merkezi CORS yapılandırması
 * Bu sınıf tüm controller'lardaki güvensiz CORS ayarlarını merkezi olarak düzenler
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // ✅ Güvenli domain listesi - sadece belirlenen domainlerden erişim
        configuration.setAllowedOrigins(Arrays.asList(
                "http://localhost:3000",           // React development server
                "http://localhost:8080",           // Spring Boot default port
                "http://localhost:8081",           // Alternatif development port
                "https://kgyp-frontend.com",       // Production frontend domain
                "https://admin.kgyp-frontend.com"  // Admin panel domain
        ));

        // ✅ İzin verilen HTTP metodları
        configuration.setAllowedMethods(Arrays.asList(
                "GET", "POST", "PUT", "DELETE", "OPTIONS"
        ));

        // ✅ İzin verilen header'lar
        configuration.setAllowedHeaders(Arrays.asList(
                "Content-Type",
                "Authorization",
                "X-Requested-With",
                "Accept",
                "Cache-Control"
        ));

        // ✅ Credentials (cookies, authorization headers) gönderimini etkinleştir
        configuration.setAllowCredentials(true);

        // ✅ Preflight request cache süresi (1 saat)
        configuration.setMaxAge(3600L);

        // ✅ CORS ayarlarını tüm API endpoint'lerine uygula
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", configuration);

        return source;
    }
}