package com.config;

import oracle.ucp.jdbc.PoolDataSource;
import oracle.ucp.jdbc.PoolDataSourceFactory;
import oracle.ucp.jdbc.PoolDataSourceImpl;
import org.quartz.spi.JobFactory;
import org.quartz.spi.TriggerFiredBundle;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.scheduling.quartz.SpringBeanJobFactory;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Created by EalenXie on 2018/6/4 11:02
 * Quartz的核心配置类
 */
@Configuration
public class ConfigureQuartz {

    @Value("${datasource.ucp.url}")
    private String url;
    @Value("${datasource.ucp.user}")
    private String user;
    @Value("${datasource.ucp.password}")
    private String password;
    @Value("${datasource.ucp.connection-factory-class-name}")
    private String connectionFactoryClassName;
    @Value("${datasource.ucp.initial-pool-size}")
    private int initialPoolSize;
    @Value("${datasource.ucp.min-pool-size}")
    private int minPoolSize;
    @Value("${datasource.ucp.max-pool-size}")
    private int maxPoolSize;
    @Value("${datasource.ucp.validate-connection-on-borrow}")
    private Boolean validateConnectionOnBorrow;
    @Value("${datasource.ucp.connection-wait-timeout}")
    private int connectionWaitTimeout;
    @Value("${datasource.ucp.max-connection-reuse-time}")
    private int maxConnectionReuseTime;

    //配置JobFactory
    @Bean
    public JobFactory jobFactory(ApplicationContext applicationContext) {
        AutowiringSpringBeanJobFactory jobFactory = new AutowiringSpringBeanJobFactory();
        jobFactory.setApplicationContext(applicationContext);
        return jobFactory;
    }

    /**
     * SchedulerFactoryBean这个类的真正作用提供了对org.quartz.Scheduler的创建与配置，并且会管理它的生命周期与Spring同步。
     * org.quartz.Scheduler: 调度器。所有的调度都是由它控制。
     * @param dataSource 为SchedulerFactory配置数据源
     * @param jobFactory 为SchedulerFactory配置JobFactory
     */
    @Bean
    public SchedulerFactoryBean schedulerFactoryBean(DataSource dataSource, JobFactory jobFactory) throws IOException {
        SchedulerFactoryBean factory = new SchedulerFactoryBean();
        //可选,QuartzScheduler启动时更新己存在的Job,这样就不用每次修改targetObject后删除qrtz_job_details表对应记录
        factory.setOverwriteExistingJobs(true);
        factory.setAutoStartup(true); //设置自行启动
        factory.setDataSource(dataSource);
        factory.setJobFactory(jobFactory);
        factory.setQuartzProperties(quartzProperties());
        return factory;
    }
    @Bean
    public DataSource dataSource() throws SQLException {
        PoolDataSource poolDataSource = (PoolDataSourceImpl) PoolDataSourceFactory.getPoolDataSource();
        poolDataSource.setURL(url);
        poolDataSource.setUser(user);
        poolDataSource.setPassword(password);
        poolDataSource.setConnectionFactoryClassName(connectionFactoryClassName);
        poolDataSource.setInitialPoolSize(initialPoolSize);
        poolDataSource.setMinPoolSize(minPoolSize);
        poolDataSource.setMaxPoolSize(maxPoolSize);
        poolDataSource.setValidateConnectionOnBorrow(validateConnectionOnBorrow);
        poolDataSource.setConnectionWaitTimeout(connectionWaitTimeout);
        poolDataSource.setMaxConnectionReuseTime(maxConnectionReuseTime);
        return poolDataSource;
    }

    //从quartz.properties文件中读取Quartz配置属性
    @Bean
    public Properties quartzProperties() throws IOException {
        PropertiesFactoryBean propertiesFactoryBean = new PropertiesFactoryBean();
        propertiesFactoryBean.setLocation(new ClassPathResource("/quartz.properties"));
        propertiesFactoryBean.afterPropertiesSet();
        return propertiesFactoryBean.getObject();
    }

    //配置JobFactory,为quartz作业添加自动连接支持
    public final class AutowiringSpringBeanJobFactory extends SpringBeanJobFactory implements
            ApplicationContextAware {
        private transient AutowireCapableBeanFactory beanFactory;

        @Override
        public void setApplicationContext(final ApplicationContext context) {
            beanFactory = context.getAutowireCapableBeanFactory();
        }

        @Override
        protected Object createJobInstance(final TriggerFiredBundle bundle) throws Exception {
            final Object job = super.createJobInstance(bundle);
            beanFactory.autowireBean(job);
            return job;
        }
    }

}
