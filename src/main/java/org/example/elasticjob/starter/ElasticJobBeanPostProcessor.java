package org.example.elasticjob.starter;

import java.util.ArrayList;
import java.util.List;
import org.apache.shardingsphere.elasticjob.api.ElasticJob;
import org.apache.shardingsphere.elasticjob.api.JobConfiguration;
import org.apache.shardingsphere.elasticjob.lite.api.bootstrap.impl.ScheduleJobBootstrap;
import org.apache.shardingsphere.elasticjob.reg.zookeeper.ZookeeperRegistryCenter;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.config.BeanPostProcessor;

/**
 * @author QJ
 */
public class ElasticJobBeanPostProcessor implements BeanPostProcessor, DisposableBean {

    /**
     * Zookeeper註冊器
     */
    private ZookeeperRegistryCenter zookeeperRegistryCenter;
    /**
     * 已註冊任務列表
     */
    private List<ScheduleJobBootstrap> schedulers = new ArrayList<>();

    /**
     * 構造註冊中心
     */
    public ElasticJobBeanPostProcessor(ZookeeperRegistryCenter zookeeperRegistryCenter) {
        this.zookeeperRegistryCenter = zookeeperRegistryCenter;
    }

    @Override
    public Object postProcessBeforeInitialization(Object o, String s) throws BeansException {
        return o;
    }

    /**
     * Spring IOC容器擴展接口方法，Bean初始化後執行AbstractAutowireCapableBeanFactory.class
     */
    @Override
    public Object postProcessAfterInitialization(Object o, String s) throws BeansException {
        Class<?> clazz = o.getClass();
        //只處理自定義ElasticTask註解
        if (!clazz.isAnnotationPresent(ElasticTask.class)) {
            return o;
        }
        if (!(o instanceof ElasticJob)) {
            return o;
        }
        ElasticJob job = (ElasticJob) o;
        //獲取註解定義
        ElasticTask annotation = clazz.getAnnotation(ElasticTask.class);
        //註解任務名稱定義
        String jobName = annotation.jobName();
        //註解cron表達式定義
        String cron = annotation.cron();
        //註解任務參數設置
        String jobParameter = annotation.jobParameter();
        //註解任務描述信息設置
        String description = annotation.description();
        //註解數據分片數設置
        int shardingTotalCount = annotation.shardingTotalCount();
        //註解數據分片參數設置
        String shardingItemParameters = annotation.shardingItemParameters();
        //註解是否禁用分片項設置
        boolean disabled = annotation.disabled();
        //註解重啓任務定義信息是否覆蓋設置
        boolean overwrite = annotation.overwrite();
        //註解是否開啓故障轉移設置
        boolean failover = annotation.failover();
        //註解是否開啓錯過任務重新執行設置
        boolean misfire = annotation.misfire();
        //註解分片策略類配置
        String jobShardingStrategyClass = annotation.jobShardingStrategyClass();

        //根據自定義註解配置，設置ElasticJob任務配置信息
        JobConfiguration coreConfiguration = JobConfiguration
                .newBuilder(jobName, shardingTotalCount).cron(cron).jobParameter(jobParameter).overwrite(overwrite)
                .failover(failover).misfire(misfire).description(description)
                .shardingItemParameters(shardingItemParameters).disabled(disabled)
                .jobShardingStrategyType(jobShardingStrategyClass)
                .build();
        //創建任務調度對象
        ScheduleJobBootstrap scheduleJobBootstrap = new ScheduleJobBootstrap(zookeeperRegistryCenter, job,
                coreConfiguration);
        //觸發任務調度
        scheduleJobBootstrap.schedule();
        //將創建的任務對象加入集合，便於統一銷燬
        schedulers.add(scheduleJobBootstrap);
        return job;
    }

    /**
     * 任务销毁方法
     */
    @Override
    public void destroy() {
        schedulers.forEach(jobScheduler -> jobScheduler.shutdown());
    }
}
