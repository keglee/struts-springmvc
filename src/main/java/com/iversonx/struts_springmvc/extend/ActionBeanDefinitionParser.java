package com.iversonx.struts_springmvc.extend;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.NamespaceHandlerSupport;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.xml.XmlValidationModeDetector;
import org.w3c.dom.Element;

/**
 * @author Lijie
 * @version 1.0
 * @date 2020/1/9 16:41
 */
public class ActionBeanDefinitionParser implements BeanDefinitionParser {
    @Override
    public BeanDefinition parse(Element element, ParserContext parserContext) {
        return null;
    }
}
