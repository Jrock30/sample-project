package com.sample.project.config.database;

import com.sample.project.config.valut.VaultDataSource;
import com.zaxxer.hikari.HikariDataSource;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.vault.core.VaultOperations;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        entityManagerFactoryRef = "masterEntityManagerFactory"
        , basePackages = {
        "com.sample.project",
}
)
@EntityScan(
        basePackages = {
                "com.sample.project",
        }
)
@RequiredArgsConstructor
public class JPADataSourceConfig {

    private final VaultOperations vaultOperations;

    @Value("${vault.path.mysql}")
    private String mysqlVaultPath;

    @Value("${spring.datasource.url}")
    private String datasourceUrl;

    @Value("${spring.datasource.driver-class-name}")
    private String datasourceDriverClassName;
//
    @Bean(name = "jpaDatasource")
    @ConfigurationProperties(prefix = "spring.datasource.jpa.hikari")
    public DataSource getDatasource() {
        VaultDataSource vds = Objects.requireNonNull(this.vaultOperations.read(this.mysqlVaultPath, VaultDataSource.class)).getData();
        assert vds != null;
        return DataSourceBuilder // vault 로 변경 예정
                .create()
                .type(HikariDataSource.class)
                .driverClassName(datasourceDriverClassName)
                .url(String.format(this.datasourceUrl, vds.getHost(), vds.getPort(), vds.getDb()))
                .username(vds.getUsername())
                .password(vds.getPassword())
                .build();
    }

    @Primary
    @Bean(name = "masterEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(EntityManagerFactoryBuilder builder, @Qualifier("jpaDatasource") DataSource jpaDataSource) {
        Map<String, String> properties = new HashMap<>();
//        properties.put("hibernate.implicit_naming_strategy", "org.springframework.boot.orm.jpa.hibernate.SpringImplicitNamingStrategy");
        properties.put("hibernate.physical_naming_strategy", "org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl"); // 네이밍 그대로 사용

        return builder.dataSource(jpaDataSource)
                .packages("com.sample.project")
                .persistenceUnit("master")
                .properties(properties)
                .build();
    }

    @Primary
    @Bean(name = "transactionManager")
    public PlatformTransactionManager transactionManager(@Qualifier("masterEntityManagerFactory") EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }
}
