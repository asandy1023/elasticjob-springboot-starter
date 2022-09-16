package org.example.elasticjob.starter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.stereotype.Component;

/**
 * @author QJ
 */
@Component
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ElasticTask {
    /**
     * 註解屬性，用於定義任務名稱
     */
    String jobName();

    /**
     * 註解屬性，用於定義cron時間表達式
     */
    String cron();

    /**
     * 註解屬性，用於定義任務參數信息
     */
    String jobParameter() default "";

    /**
     * 註解屬性，用於定義任務描述信息
     */
    String description() default "";

    /**
     * 註解屬性，用於定義任務分片數
     */
    int shardingTotalCount() default 1;

    /**
     * 註解屬性，用於定義任務分片參數
     */
    String shardingItemParameters() default "";

    /**
     * 註解屬性，用於定時是否禁用分片項設置
     */
    boolean disabled() default false;

    /**
     * 註解屬性，配置分片策略
     */
    String jobShardingStrategyClass() default "";

    /**
     * 註解屬性，配置是否故障轉移功能
     */
    boolean failover() default false;

    /**
     * 註解屬性，配置是否運行重啓時重至任務定義信息（包括cron時間片設置）
     */
    boolean overwrite() default false;

    /**
     * 註解屬性，配置是否開啓錯誤任務重新執行
     */
    boolean misfire() default true;
}
