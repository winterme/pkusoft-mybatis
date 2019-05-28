package com.zzq.config.datasource;

import com.alibaba.druid.pool.DruidDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.*;

import javax.sql.DataSource;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Configuration
public class MyBatisAutoConfiguration implements BeanDefinitionRegistryPostProcessor , EnvironmentAware {

    private static String PREFIX = "mybatis.datasource.config";

    private static final Logger logger = LoggerFactory.getLogger(MyBatisAutoConfiguration.class);

    private Environment environment;

    private Integer [] integers;

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry beanDefinitionRegistry) throws BeansException {
        HashSet set = new HashSet();
        Iterator<PropertySource<?>> iterator = ((ConfigurableEnvironment) this.environment).getPropertySources().iterator();

        while (iterator.hasNext()){
            PropertySource localPropertySource = (PropertySource)iterator.next();
            if( localPropertySource instanceof EnumerablePropertySource ){
                for (String str1 : ((EnumerablePropertySource)localPropertySource).getPropertyNames()){
                    Matcher matcher = Pattern.compile("mybatis.datasource.config\\D+(\\d+)\\D+").matcher(str1);
                    if ( matcher.find() ){
                        set.add( matcher.group(1) );
                    }
                }
            }
        }

        this.integers = new Integer[set.size()];
        int i = 0;
        for (Object o: set) {
            this.integers[i++] = Integer.valueOf(  o.toString() );
        }

        logger.info( "configuration "+ Arrays.toString(this.integers) +" datasource"  );
        bind(beanDefinitionRegistry);
    }

    public void bind(BeanDefinitionRegistry beanDefinitionRegistry){
        for (Integer i: this.integers) {
            bind(i , beanDefinitionRegistry);
        }
    }


    public void bind(int paramInt, BeanDefinitionRegistry beanDefinitionRegistry){
        String driverClassName = this.environment.getProperty(MyBatisAutoConfiguration.PREFIX + "[" + paramInt + "].driver-class-name");
        String url = this.environment.getProperty(MyBatisAutoConfiguration.PREFIX + "[" + paramInt + "].url");
        String username = this.environment.getProperty(MyBatisAutoConfiguration.PREFIX + "[" + paramInt + "].username");
        String password = this.environment.getProperty(MyBatisAutoConfiguration.PREFIX + "[" + paramInt + "].password");

        RootBeanDefinition rootBeanDefinition = new RootBeanDefinition(DruidDataSource.class);
        if( paramInt==0 ){
            rootBeanDefinition.setPrimary(true);
        }
//        rootBeanDefinition.getPropertyValues()
//                .add("driverClass",driverClassName)
//                .add("jdbcUrl",url)
//                .add("username",username)
//                .add("password",password);
        beanDefinitionRegistry.registerBeanDefinition("datasource"+paramInt , rootBeanDefinition);

        System.out.println( beanDefinitionRegistry );
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {
        for (int i : this.integers) {
//            DruidDataSource dataSource = (DruidDataSource);
//            System.out.println( dataSource.getUrl() );
            System.out.println( configurableListableBeanFactory.getBean("datasource" + i ) );

        };
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }
}
