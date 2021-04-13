package ru.dmve.xdocrep

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer

@SpringBootApplication
@ConfigurationPropertiesScan(basePackages = ["ru.dmve.xdocrep.properties"])
class XdocrepApplication: SpringBootServletInitializer() 
fun main(args: Array<String>) {
	runApplication<XdocrepApplication>(*args)
}
