package com.mayloom.vt.spring;

import com.vaadin.Application;
import com.vaadin.terminal.gwt.server.WebApplicationContext;
import javax.servlet.ServletContext;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SpringContextHelper {

	private ApplicationContext context;
	
	private static Logger logger = LoggerFactory.getLogger(SpringContextHelper.class);

    public SpringContextHelper(Application application) {
        ServletContext servletContext = ((WebApplicationContext) application.getContext()).getHttpSession().getServletContext();
        context = WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);
        logger.debug("Number of beans: {}",context.getBeanDefinitionCount());
    }

    public Object getBean(final String beanRef) {
        return context.getBean(beanRef);
    }   
    
    public <T> T getBean(Class<T> requiredType) {
    	return context.getBean(requiredType);
    }
    
}
