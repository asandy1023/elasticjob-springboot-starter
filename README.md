# elasticjob-springboot-starter



# 連線SDK

## 請參考主要分散式系統專案 Zookeeper-Elasticjob-

>配置完成後，執行mvn clean install，把我們的elasticjob-springboot-starter推到maven私庫中。然後在別的項目中引入我們的starter依賴，使用連線API : interface @ElasticTask(jobName = "{jobName}", cron = "*/5 * * * * ?", description = "{description}", overwrite = true)進行連線與排程分散式系統
