package com.queue.application.configuration;


import com.queue.application.repository.QueueDataRepository;
import com.queue.application.repository.QueueReository;
import com.queue.application.service.Impl.QueueServicesImpl;
import com.queue.application.service.QueueServices;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import java.util.Locale;

@Configuration
@AutoConfigureBefore
public class ApplicationConfiguration implements WebMvcConfigurer {

    @Value("${lang.value}")
    private String language;
    @Bean
    public QueueServices queueServices(QueueReository queueReository, QueueDataRepository queueDataRepository){

        return  new QueueServicesImpl(queueReository,queueDataRepository);
    }

    @Bean
    public LocaleResolver localeResolver(){
        SessionLocaleResolver sessionLocaleResolver =  new SessionLocaleResolver();
        sessionLocaleResolver.setDefaultLocale(new Locale(language));
        return sessionLocaleResolver;
    }
}
