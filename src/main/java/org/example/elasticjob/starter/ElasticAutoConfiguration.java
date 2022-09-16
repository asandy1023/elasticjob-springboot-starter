package org.example.elasticjob.starter;

import org.apache.shardingsphere.elasticjob.reg.zookeeper.ZookeeperConfiguration;
import org.apache.shardingsphere.elasticjob.reg.zookeeper.ZookeeperRegistryCenter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author QJ
 */
@Configuration
@ConditionalOnProperty({"elasticjob.zk.serverLists", "elasticjob.zk.namespace"})
public class ElasticAutoConfiguration {

    /**
     * 註冊中心配置
     *
     * @param serverLists
     * @param namespace
     * @return
     */
    @Bean
    @ConfigurationProperties("elasticjob.zk")
    public ZookeeperConfiguration getConfiguration(@Value("${elasticjob.zk.serverLists}") String serverLists,
            @Value("${elasticjob.zk.namespace}") String namespace) {
        return new ZookeeperConfiguration(serverLists, namespace);
    }

    /**
     * 初始化註冊信息
     *
     * @param configuration
     * @return
     */
    @Bean(initMethod = "init")
    public ZookeeperRegistryCenter zookeeperRegistryCenter(ZookeeperConfiguration configuration) {
        return new ZookeeperRegistryCenter(configuration);
    }

    /**
     * 定義處理自定義ElasticJob任務的邏輯類
     *
     * @param center
     * @return
     */
    @Bean
    public ElasticJobBeanPostProcessor elasticJobBeanPostProcessor(ZookeeperRegistryCenter center) {
        return new ElasticJobBeanPostProcessor(center);
    }
}
