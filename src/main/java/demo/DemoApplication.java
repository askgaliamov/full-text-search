package demo;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;

import ro.isdc.wro.http.ConfigurableWroFilter;
import ro.isdc.wro.manager.factory.ConfigurableWroManagerFactory;
import ro.isdc.wro.manager.factory.WroManagerFactory;
import ro.isdc.wro.model.factory.WroModelFactory;
import ro.isdc.wro.model.factory.XmlModelFactory;
import ro.isdc.wro.model.resource.locator.factory.ConfigurableLocatorFactory;
import ro.isdc.wro.model.resource.processor.factory.ConfigurableProcessorsFactory;
import ro.isdc.wro.model.resource.support.hash.ConfigurableHashStrategy;
import ro.isdc.wro.model.resource.support.naming.ConfigurableNamingStrategy;

@SpringBootApplication
public class DemoApplication {
	@Value("classpath:/wro.xml")
	private Resource wroConfig;

	@Bean
	ConfigurableWroFilter webResourceOptimzer() {
		ConfigurableWroFilter wroFilter = new ConfigurableWroFilter();
		wroFilter.setCacheUpdatePeriod(5);
		wroFilter.setWroManagerFactory(webResourceOptimizerManager());

		return wroFilter;
	}

	@Bean
	FilterRegistrationBean wro4jFilterRegistration(ConfigurableWroFilter wroFilter) {
		FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean(wroFilter);
		filterRegistrationBean.addUrlPatterns("/assets/*");
		return filterRegistrationBean;
	}

	@Bean
	WroManagerFactory webResourceOptimizerManager() {
		ConfigurableWroManagerFactory factory = new ConfigurableWroManagerFactory() {
			@Override
			protected WroModelFactory newModelFactory() {
				return new XmlModelFactory() {
					@Override
					protected InputStream getModelResourceAsStream()
							throws IOException {
						return wroConfig.getInputStream();
					}
				};
			}

			@Override
			protected Properties newConfigProperties() {
				Properties properties = new Properties();
		        properties.put(ConfigurableLocatorFactory.PARAM_URI_LOCATORS, "servletContext,webjar,classpath,uri");
		        properties.put(ConfigurableProcessorsFactory.PARAM_PRE_PROCESSORS, "cssImport,semicolonAppender");
//		        properties.put(ConfigurableProcessorsFactory.PARAM_POST_PROCESSORS, "less4j,cssMinJawr,jsMin");
		        properties.put(ConfigurableProcessorsFactory.PARAM_POST_PROCESSORS, "less4j");
		        properties.put(ConfigurableNamingStrategy.KEY, "hashEncoder-CRC32");
		        properties.put(ConfigurableHashStrategy.KEY, "MD5");
		        return properties;
			}
		};


		return factory;
	}


	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}
}
