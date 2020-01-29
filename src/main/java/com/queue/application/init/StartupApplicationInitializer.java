package com.queue.application.init;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.context.ShutdownEndpoint;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.boot.actuate.health.Status;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.core.env.Environment;

import javax.annotation.PostConstruct;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.Arrays;


@Slf4j
@Component
public class StartupApplicationInitializer implements InitializingBean {

    @Autowired
    private Environment environment;

    @Autowired
    ShutdownEndpoint shutdownEndpoint;

    @Autowired
    HealthEndpoint healthEndpoint;

    private RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
    private long pid = Long.valueOf(runtimeMXBean.getName().split("@")[0]);

    @EventListener(ContextRefreshedEvent.class)
    public void beanInitializationDone(ContextRefreshedEvent event){
        this.getClass().getClassLoader().setDefaultAssertionStatus(true);
        LOGGER.info("----------------------------------------------");
        LOGGER.info("Process ID {} is Ready",pid);
        LOGGER.info("----------------------------------------------");

    }

    @PostConstruct
    public void init(){
        LOGGER.info("----------------------------------------------");
        LOGGER.info("Bean creation in complted");
        LOGGER.info("----------------------------------------------");

    }

    @PostConstruct
    public void helathCheckup(){
        LOGGER.info("----------------------------------------------");
        LOGGER.info("Health checkup started");

        Health health = healthEndpoint.health();

        if(health.getStatus().equals(Status.DOWN) || health.getStatus().equals(Status.OUT_OF_SERVICE) ){
            LOGGER.info("Application Shutting Down Beacause Of Bad Health");
            LOGGER.info("----------------------------------------------");
            shutdownEndpoint.shutdown();
        }
        else{
            LOGGER.info("Health checkup ended");
            LOGGER.info("----------------------------------------------");
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        LOGGER.info("----------------------------------------------");
        LOGGER.info("" +  environment.getDefaultProfiles());
        LOGGER.info("----------------------------------------------");
    }
}
