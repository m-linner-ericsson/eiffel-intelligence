package com.ericsson.ei.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.session.data.mongo.MongoIndexedSessionRepository;
import org.springframework.session.web.http.HttpSessionIdResolver;
import org.springframework.session.web.http.SessionRepositoryFilter;

@Configuration
public class HttpSessionConfig {

    @Value("${server.session.timeout}")
    private int maxInactiveIntervalInSeconds;

    @Value("${sessions.collection.name}")
    private String collectionName;

    @Bean
    public MongoIndexedSessionRepository sessionRepository(MongoOperations mongoOperations) {
        MongoIndexedSessionRepository repository = new MongoIndexedSessionRepository(mongoOperations);
        repository.setMaxInactiveIntervalInSeconds(maxInactiveIntervalInSeconds);
        repository.setCollectionName(collectionName);
        return repository;
    }

    @Bean
    public HttpSessionIdResolver httpSessionIdResolver() {
        return new HeaderAndCookieHttpSessionIdResolver();
    }

    @Bean
    public SessionRepositoryFilter<?> springSessionRepositoryFilter(MongoIndexedSessionRepository sessionRepository) {
        SessionRepositoryFilter<?> filter = new SessionRepositoryFilter<>(sessionRepository);
        filter.setHttpSessionIdResolver(httpSessionIdResolver());
        return filter;
    }

    @Bean
    public FilterRegistrationBean<SessionRepositoryFilter<?>> sessionFilterRegistration(
            SessionRepositoryFilter<?> springSessionRepositoryFilter) {
        FilterRegistrationBean<SessionRepositoryFilter<?>> reg = new FilterRegistrationBean<>(springSessionRepositoryFilter);
        reg.setName("springSessionRepositoryFilter");
        reg.setOrder(Integer.MIN_VALUE + 50);
        return reg;
    }

    public static String getCurrentUser() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
}
