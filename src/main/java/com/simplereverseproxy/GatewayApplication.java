package com.simplereverseproxy;

import com.simplereverseproxy.filters.GatewayPostFilter;
import com.simplereverseproxy.filters.GatewayPreFilter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.annotation.Bean;

@EnableZuulProxy
@SpringBootApplication
public class GatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(GatewayApplication.class, args);
	}


	@Bean
	public GatewayPreFilter preFilter() {
		return new GatewayPreFilter();
	}

	@Bean
	public GatewayPostFilter postFilter() {
		return new GatewayPostFilter();
	}

}
