package ca.sheridancollege.yoojiw.Security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import ca.sheridancollege.yoojiw.service.MyUserDetailsService;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	@Autowired
	private MyUserDetailsService userDetailsService; 
	
	@Autowired
	private MyAccessDeniedHanlder accessDeniedHandler; 
	
	@Bean
	public BCryptPasswordEncoder bcryptPasswordEncoder() {
		
		return new BCryptPasswordEncoder(); 
	}
	
	
	@Bean			//주의!! Bean 꼭 필요함 
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		
		
		http.csrf().disable() // CSRF 보호 해제
			.authorizeHttpRequests()
				.requestMatchers("/", "/css/**", "/images/**", "/js/**", "/register", "/view/**", "/extraDetail/**", "/permission-denied/**", "/fragments/**").permitAll()
				.requestMatchers("/secure/admin/**", "/productRegister", "/addModel/**", "/registerModel/**", "/modfiyQuantity").hasRole("ADMIN")		//
				.anyRequest().authenticated()								//중요. 다른 모든 요청에 대해 인증된(authenticated) 사용자만 접근 가능하도록 설정
				.and()
			.formLogin()
				.defaultSuccessUrl("/").permitAll()
				.loginPage("/login").permitAll()
				.and()
			.logout()
			    .invalidateHttpSession(true)
			    .clearAuthentication(true)
			    .deleteCookies("JSESSIONID") // 사용자 세션 쿠키 삭제
			    .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
			    .logoutSuccessUrl("/login?logout").permitAll()
			    .and()
			.exceptionHandling()
				.accessDeniedHandler(accessDeniedHandler); 
		
		
		return http.build(); 
	}
	
	
	
	public void configure(AuthenticationManagerBuilder auth) throws Exception {
		
		auth.userDetailsService(userDetailsService).passwordEncoder(bcryptPasswordEncoder()); 
	}
	
}
