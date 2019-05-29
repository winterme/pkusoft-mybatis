//package com.zzq.config.datasource;
//
//import com.alibaba.druid.pool.DruidDataSource;
//import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceAutoConfigure;
//import org.apache.ibatis.session.SqlSessionFactory;
//import org.mybatis.spring.SqlSessionFactoryBean;
//import org.mybatis.spring.boot.autoconfigure.SpringBootVFS;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.BeansException;
//import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
//import org.springframework.beans.factory.config.RuntimeBeanReference;
//import org.springframework.beans.factory.support.BeanDefinitionRegistry;
//import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
//import org.springframework.beans.factory.support.RootBeanDefinition;
//import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
//import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
//import org.springframework.boot.autoconfigure.jdbc.JdbcTemplateAutoConfiguration;
//import org.springframework.context.EnvironmentAware;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.core.env.*;
//import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
//import org.springframework.jdbc.datasource.DataSourceTransactionManager;
//import tk.mybatis.mapper.autoconfigure.MapperProperties;
//import tk.mybatis.spring.mapper.MapperScannerConfigurer;
//import tk.mybatis.spring.mapper.SpringBootBindUtil;
//
//import javax.sql.DataSource;
//import java.io.IOException;
//import java.util.Arrays;
//import java.util.HashSet;
//import java.util.Iterator;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//
//@Configuration
//public class MyBatisAutoConfiguration implements BeanDefinitionRegistryPostProcessor , EnvironmentAware {
//
//    private static String PREFIX = "mybatis.datasource.config";
//
//    private static final Logger logger = LoggerFactory.getLogger(MyBatisAutoConfiguration.class);
//
//    private Environment environment;
//
//    private Integer [] integers;
//
//    @Override
//    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry beanDefinitionRegistry) throws BeansException {
//        HashSet set = new HashSet();
//        Iterator<PropertySource<?>> iterator = ((ConfigurableEnvironment) this.environment).getPropertySources().iterator();
//
//        while (iterator.hasNext()){
//            PropertySource localPropertySource = (PropertySource)iterator.next();
//            if( localPropertySource instanceof EnumerablePropertySource ){
//                for (String str1 : ((EnumerablePropertySource)localPropertySource).getPropertyNames()){
//                    Matcher matcher = Pattern.compile("mybatis.datasource.config\\D+(\\d+)\\D+").matcher(str1);
//                    if ( matcher.find() ){
//                        set.add( matcher.group(1) );
//                    }
//                }
//            }
//        }
//
//        this.integers = new Integer[set.size()];
//        int i = 0;
//        for (Object o: set) {
//            this.integers[i++] = Integer.valueOf(  o.toString() );
//        }
//        bind(beanDefinitionRegistry);
//
//        for (String name: beanDefinitionRegistry.getBeanDefinitionNames()) {
//            logger.info("bean name :" + name );
//        }
//
//    }
//
//    public void bind(BeanDefinitionRegistry beanDefinitionRegistry){
//        for (Integer i: this.integers) {
//            bindDataSource(i , beanDefinitionRegistry);
//            bindSqlSessionFactory(i , beanDefinitionRegistry);
//            bindMapperScaner(i , beanDefinitionRegistry);
//            bindTransation(i , beanDefinitionRegistry);
//        }
//    }
//
//    public void bindTransation(int i , BeanDefinitionRegistry beanDefinitionRegistry){
//        String tranName = "transactionManager" + i;
//        RootBeanDefinition transaction = new RootBeanDefinition(DataSourceTransactionManager.class);
//        transaction.getPropertyValues()
//                .add("dataSource", new RuntimeBeanReference("datasource" + i ));
//
//        if( i == 0 ){
//            transaction.setPrimary(true);
//        }
//
//        beanDefinitionRegistry.registerBeanDefinition(tranName, transaction);
//    }
//
//    public void bindMapperScaner(int i , BeanDefinitionRegistry beanDefinitionRegistry){
//        String mapperScanner = this.environment.getProperty(MyBatisAutoConfiguration.PREFIX + "[" + i + "].mapper-scanner");
//        String basePackage = this.environment.getProperty(MyBatisAutoConfiguration.PREFIX + "[" + i + "].base-package");
//
//        String sqlSessionFactoryName = "sqlSessionFactory" + i;
//
//        Object clazz = "";
//        try {
//            clazz = Class.forName(mapperScanner);
//        } catch (NullPointerException e){
//            clazz = MapperScannerConfigurer.class;
//        } catch (ClassNotFoundException e) {
//            clazz = MapperScannerConfigurer.class;
//        }
//
////        MapperScannerConfigurer configurer = new MapperScannerConfigurer();
////        configurer.setSqlSessionFactoryBeanName();
//
//        RootBeanDefinition mapperScannerObject = new RootBeanDefinition((Class) clazz);
//        mapperScannerObject.getPropertyValues()
//                .add("basePackage", basePackage)
//                .add("sqlSessionFactoryBeanName", sqlSessionFactoryName);
//
//        if( i == 0 ){
//            mapperScannerObject.setPrimary(true);
//        }
//
//        beanDefinitionRegistry.registerBeanDefinition("mapperScannerConfigurer" + i, mapperScannerObject);
//    }
//
//    public void bindSqlSessionFactory(int i , BeanDefinitionRegistry beanDefinitionRegistry){
//        String mapperLocations = this.environment.getProperty(MyBatisAutoConfiguration.PREFIX + "[" + i + "].mapper-location");
//
//        String sqlSessionFactoryName = "sqlSessionFactory" + i;
//
//        RootBeanDefinition sqlSession = new RootBeanDefinition(SqlSessionFactoryBean.class);
//        try {
//            sqlSession.getPropertyValues()
//                    .add("dataSource", new RuntimeBeanReference("datasource" + i ))
//                    .add("vfs", SpringBootVFS.class)
//                    .add("mapperLocations", new PathMatchingResourcePatternResolver().getResources(mapperLocations));
//        } catch (IOException e) {
//            logger.error(sqlSessionFactoryName + " bind fail!!!");
//        }
//
//        if( i == 0 ){
//            sqlSession.setPrimary(true);
//        }
//
//        beanDefinitionRegistry.registerBeanDefinition(sqlSessionFactoryName, sqlSession);
//    }
//
//    public void bindDataSource(Integer paramInt, BeanDefinitionRegistry beanDefinitionRegistry){
//        String driverClassName = this.environment.getProperty(MyBatisAutoConfiguration.PREFIX + "[" + paramInt + "].driver-class-name");
//        String url = this.environment.getProperty(MyBatisAutoConfiguration.PREFIX + "[" + paramInt + "].url");
//        String username = this.environment.getProperty(MyBatisAutoConfiguration.PREFIX + "[" + paramInt + "].username");
//        String password = this.environment.getProperty(MyBatisAutoConfiguration.PREFIX + "[" + paramInt + "].password");
//
//        RootBeanDefinition rootBeanDefinition = new RootBeanDefinition(DruidDataSource.class);
//        if( paramInt==0 ){
//            rootBeanDefinition.setPrimary(true);
//        }
//
//        rootBeanDefinition.getPropertyValues()
//                .add("driverClassName",driverClassName)
//                .add("url",url)
//                .add("username",username)
//                .add("password",password)
//                .add("filters","stat");
//        beanDefinitionRegistry.registerBeanDefinition("datasource"+paramInt , rootBeanDefinition);
//
//        logger.info( String.format("driverClassName %s  url %s  username %s  password %s" , driverClassName,url,username,password ) );
//    }
//
//    @Override
//    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {
//        for (int i : this.integers) {
//            DruidDataSource source = (DruidDataSource)configurableListableBeanFactory.getBean("datasource" + i );
//            logger.info( source.getUrl() );
//        };
//    }
//
//    @Override
//    public void setEnvironment(Environment environment) {
//        this.environment = environment;
//    }
//}
