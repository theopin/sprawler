package com.sprawler.hibernate.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.jpa.HibernatePersistenceProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
public class DataSourceConfig {

    private static final Logger LOGGER = LogManager.getLogger(DataSourceConfig.class);


    @Autowired
    @Qualifier("env")
    private Environment env;

    @Bean("dataSource")
    public DataSource getDataSource() {
        LOGGER.info("Setting up datasource connection (H2 DB)");
        DataSourceBuilder<?> dataSourceBuilder = DataSourceBuilder.create();
        dataSourceBuilder.driverClassName("org.h2.Driver");
        dataSourceBuilder.url("jdbc:h2:mem:test");
        dataSourceBuilder.username("SA");
        dataSourceBuilder.password("");
        return dataSourceBuilder.build();
    }

    @Bean("hibernateProperties")
    public Properties hibernateProperties() {
        LOGGER.info("Setting up hibernate properties bean");
        Properties properties = new Properties();
        if (env != null) {
            properties.put("hibernate.show_sql", env.getProperty("hibernate.show_sql"));
            properties.put("hibernate.format_sql", env.getProperty("hibernate.format_sql"));
            properties.put("hibernate.hbm2ddl.auto", env.getProperty("hibernate.hbm2ddl.auto"));
            properties.put("hibernate.dialect", env.getProperty("hibernate.dialect"));
        }

        return properties;
    }



    @Bean("entityManagerFactory")
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(
            @Qualifier("dataSource") DataSource dataSource,
            @Qualifier("hibernateProperties") Properties hibernateProperties)  {
        LOGGER.info("Setting up entity manager factory based on provided datasource and hibernate properties");
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource);
        em.setPackagesToScan("com.sprawler.hibernate");

        em.setPersistenceProviderClass(HibernatePersistenceProvider.class);
        em.setJpaProperties(hibernateProperties);

        return em;
    }

    @Bean("transactionManager")
    public PlatformTransactionManager transactionManager(
            @Qualifier("entityManagerFactory") LocalContainerEntityManagerFactoryBean entityManagerFactory)  {
        LOGGER.info("Setting up jpa transaction manager based on provided entity manager factory");
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManagerFactory.getObject());
        return transactionManager;
    }
}