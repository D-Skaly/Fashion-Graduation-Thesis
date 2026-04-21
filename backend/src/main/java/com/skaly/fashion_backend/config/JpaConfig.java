package com.skaly.fashion_backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import jakarta.persistence.EntityManagerFactory;
import org.hibernate.boot.model.naming.CamelCaseToUnderscoresNamingStrategy;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = "com.skaly.fashion_backend")
@EnableJpaAuditing
public class JpaConfig {

    @Value("${spring.jpa.properties.hibernate.jdbc.batch_size:50}")
    private int batchSize;

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource) {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource);
        em.setPackagesToScan(
                "com.skaly.fashion_backend",
                "org.springframework.modulith.events.jpa");
        
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);
        
        Properties jpaProperties = new Properties();
        
        // Dialect configuration
        jpaProperties.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
        jpaProperties.put(
                "hibernate.physical_naming_strategy",
                CamelCaseToUnderscoresNamingStrategy.class.getName());
        
        // Lazy loading configuration
        jpaProperties.put("hibernate.enable_lazy_load_no_trans", "true");
        jpaProperties.put("hibernate.default_batch_fetch_size", batchSize);
        
        // Performance optimizations
        jpaProperties.put("hibernate.jdbc.batch_size", batchSize);
        jpaProperties.put("hibernate.order_inserts", "true");
        jpaProperties.put("hibernate.order_updates", "true");
        jpaProperties.put("hibernate.jdbc.batch_versioned_data", "true");
        jpaProperties.put("hibernate.jdbc.fetch_size", batchSize);
        
        // Query timeout configuration
        jpaProperties.put("hibernate.jdbc.query_timeout", 30); // 30 seconds
        
        // Second-level cache (optional, can be enabled later)
        jpaProperties.put("hibernate.cache.use_second_level_cache", "false");
        jpaProperties.put("hibernate.cache.use_query_cache", "false");
        
        // Statistics (disable in production)
        jpaProperties.put("hibernate.generate_statistics", "false");
        
        em.setJpaProperties(jpaProperties);
        return em;
    }

    @Bean
    public JpaTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManagerFactory);
        return transactionManager;
    }
}
