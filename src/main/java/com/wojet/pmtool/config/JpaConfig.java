package com.wojet.pmtool.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing
public class JpaConfig {

    //  @Bean
    // public AuditorAware<User> auditorProvider() {
    //     return new SpringSecurityAuditorAware();
    // }
}
