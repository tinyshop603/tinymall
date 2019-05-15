package com.attitude.tinymall.db.config;

import javax.sql.DataSource;
import org.apache.ibatis.logging.slf4j.Slf4jImpl;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.type.EnumTypeHandler;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author zhaoguiyang on 2019/5/15.
 * @project Wechat
 */
@Configuration
@MapperScan(basePackages = "com.attitude.tinymall.db")
@EnableTransactionManagement
public class MybatisConfig {

  @Bean
  SqlSessionFactory sqlSessionFactory(ApplicationContext context, DataSource dataSource)
      throws Exception {
    org.apache.ibatis.session.Configuration myBatisConfiguration = new org.apache.ibatis.session.Configuration();
    myBatisConfiguration.setLogImpl(Slf4jImpl.class);
    myBatisConfiguration.setMapUnderscoreToCamelCase(true);
    SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
    sessionFactory.setConfiguration(myBatisConfiguration);
    sessionFactory.setTypeAliasesPackage("com.attitude.tinymall.db.mybatis");
    sessionFactory.setMapperLocations(context.getResources("classpath*:com/attitude/tinymall/db/dao/**/*.xml"));
    sessionFactory.setDataSource(dataSource);
    return sessionFactory.getObject();
  }


}
