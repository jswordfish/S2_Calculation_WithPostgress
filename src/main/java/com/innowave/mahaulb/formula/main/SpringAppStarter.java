package com.innowave.mahaulb.formula.main;

import java.util.Arrays;
import java.util.EnumSet;

import javax.faces.webapp.FacesServlet;
import javax.servlet.DispatcherType;

import org.apache.cxf.Bus;
import org.apache.cxf.bus.spring.SpringBus;
import org.apache.cxf.endpoint.Server;
import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.apache.cxf.transport.servlet.CXFServlet;
import org.ocpsoft.rewrite.servlet.RewriteFilter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ImportResource;

import com.innowave.mahaulb.formula.webservices.RulesWebService;

@SpringBootApplication(exclude = {   org.springframework.boot.autoconfigure.security.SecurityAutoConfiguration.class, DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class })
//@ImportResource("classpath:appContext.xml")
@ImportResource({"classpath:META-INF/cxf/cxf.xml", "classpath:META-INF/cxf/cxf-servlet.xml"})
@ComponentScan({"com.innowave.mahaulb.formula.jsf.managedBeans"})
public class SpringAppStarter extends SpringBootServletInitializer {
	
//	  @Override
//	    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
//	        return application.sources(SpringAppStarter.class);
//	    }
	
	public static void main(String[] args) {

        SpringApplication.run(SpringAppStarter.class, args);
    }
	
	  @Bean
	    public ServletRegistrationBean servletRegistrationBean() {
	        FacesServlet servlet = new FacesServlet();
	        return new ServletRegistrationBean(servlet, "*.xhtml");
	    }
	  
	  
	  
	  @Bean
	    public FilterRegistrationBean rewriteFilter() {
	        FilterRegistrationBean rwFilter = new FilterRegistrationBean(new RewriteFilter());
	        rwFilter.setDispatcherTypes(EnumSet.of(DispatcherType.FORWARD, DispatcherType.REQUEST,
	                DispatcherType.ASYNC, DispatcherType.ERROR));
	        rwFilter.addUrlPatterns("/*");
	        return rwFilter;
	    }
	  
	  @Bean
	  public ServletRegistrationBean dispatcherServlet ( )  { 
	     return  new ServletRegistrationBean ( new CXFServlet() , "/ws/*" ) ; 
	 } 
	  
	  @Bean
	  public RulesWebService rulesService() {
	  	return new RulesWebService();
	  }
	  
	  @Bean(name=Bus.DEFAULT_BUS_ID)
	    public SpringBus springBus() {      
	        return new SpringBus();
	    }
	  
	  @Bean
	    public Server rsServer() {
	        JAXRSServerFactoryBean endpoint = new JAXRSServerFactoryBean();
	        endpoint.setBus(springBus());
	        endpoint.setAddress("/rest/");
	        endpoint.setProvider(new com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider());
	       // endpoint.setProvider("");
	       
	        // Register 2 JAX-RS root resources supporting "/sayHello/{id}" and "/sayHello2/{id}" relative paths
	        endpoint.setServiceBean(rulesService());
	       // endpoint.setFeatures(Arrays.asList(new Swagger2Feature()));
	        return endpoint.create();
	    }
}
