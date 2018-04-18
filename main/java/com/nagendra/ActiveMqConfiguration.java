package com.nagendra;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.camel.component.ActiveMQComponent;
import org.apache.activemq.jms.pool.PooledConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * Created by nagendra on 17/04/2018.
 */
@Component
public class ActiveMqConfiguration {

    @Value("${activemq.broker.url}")
    private String brokerUrl;

    @Value("${activemq.broker.username}")
    private String username;

    @Value("${activemq.broker.password}")
    private String password;

    @Value("${activemq.broker.maxConnections}")
    private int maxConnections;

    @Bean
    public ActiveMQConnectionFactory activeMQConnectionFactory() {
        ActiveMQConnectionFactory activeMQConnectionFactory = new ActiveMQConnectionFactory();
        activeMQConnectionFactory.setBrokerURL(brokerUrl);
        activeMQConnectionFactory.setUserName(username);
        activeMQConnectionFactory.setPassword(password);

        PooledConnectionFactory pooledConnectionFactory = new PooledConnectionFactory();
        pooledConnectionFactory.setConnectionFactory(activeMQConnectionFactory);
        pooledConnectionFactory.setMaxConnections(maxConnections);

        return activeMQConnectionFactory;
    }

    @Bean
    public PooledConnectionFactory pooledConnectionFactory() {
        PooledConnectionFactory pooledConnectionFactory = new PooledConnectionFactory();
        pooledConnectionFactory.setConnectionFactory(activeMQConnectionFactory());
        pooledConnectionFactory.setMaxConnections(maxConnections);
        return pooledConnectionFactory;
    }

    @Bean
    public ActiveMQComponent activeMQComponent() {
        ActiveMQComponent activeMQComponent = new ActiveMQComponent();
        activeMQComponent.setConnectionFactory(pooledConnectionFactory());
        activeMQComponent.setTransacted(true);

        return activeMQComponent;
    }

}
