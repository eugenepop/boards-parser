package nil.webparser.security;

import nil.webparser.security.oauth.ClientResources;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.security.oauth2.resource.UserInfoTokenServices;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.filter.OAuth2ClientAuthenticationProcessingFilter;
import org.springframework.security.oauth2.client.filter.OAuth2ClientContextFilter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.CompositeFilter;
import nil.webparser.security.oauth.extractor.GooglePrincipalExtractor;
import nil.webparser.security.oauth.extractor.VkPrincipalExtractor;
import nil.webparser.security.oauth.vk.VkCustomAuthenticationFilter;
import nil.webparser.security.oauth.vk.VkUserInfoTokenService;

import javax.servlet.Filter;
import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableOAuth2Client
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private AuthProvider authProvider;

    @Qualifier("oauth2ClientContext")
    @Autowired
    private OAuth2ClientContext oAuth2ClientContext;

    @Autowired
    private GooglePrincipalExtractor googlePrincipalExtractor;

    @Autowired
    private VkPrincipalExtractor vkPrincipalExtractor;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeRequests()
                .antMatchers("/lk").hasRole("USER")
                .antMatchers("/*").permitAll()
                //.antMatchers("/resources/**", "/", "/index", "/registration", "/login", "/login/*").permitAll()
                //.anyRequest().authenticated()
                .and().formLogin().loginPage("/login").permitAll()
                .and().logout().logoutSuccessUrl("/").permitAll();

        http.addFilterBefore(ssoCompositeFilter(), UsernamePasswordAuthenticationFilter.class);
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) {
        auth.authenticationProvider(authProvider);
    }

    @Bean
    public FilterRegistrationBean oAuth2ClientFilterRegistration(OAuth2ClientContextFilter oAuth2ClientContextFilter) {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(oAuth2ClientContextFilter);
        registration.setOrder(-100);
        return registration;
    }

    private Filter ssoCompositeFilter() {
        List<Filter> filters = new ArrayList<>();
        filters.add(vkSsoFilter(vk(), "/login/vk"));
        filters.add(ssoCompositeFilter(google(), "/login/google"));
        CompositeFilter filter = new CompositeFilter();
        filter.setFilters(filters);
        return filter;
    }

    private Filter vkSsoFilter(ClientResources client, String path) {
        VkCustomAuthenticationFilter filter = new VkCustomAuthenticationFilter(path);

        OAuth2RestTemplate template = new OAuth2RestTemplate(client.getClient(), oAuth2ClientContext);

        filter.setRestTemplate(template);

        VkUserInfoTokenService tokenServices =
                new VkUserInfoTokenService(client.getResource().getUserInfoUri(), client.getClient().getClientId());
        tokenServices.setRestTemplate(template);
        tokenServices.setPrincipalExtractor(client.getPrincipalExtractor());
        filter.setTokenServices(tokenServices);

        return filter;
    }

    private Filter ssoCompositeFilter(ClientResources client, String path) {
        OAuth2ClientAuthenticationProcessingFilter filter = new OAuth2ClientAuthenticationProcessingFilter(path);

        OAuth2RestTemplate template = new OAuth2RestTemplate(client.getClient(), oAuth2ClientContext);
        filter.setRestTemplate(template);

        UserInfoTokenServices tokenServices =
                new UserInfoTokenServices(client.getResource().getUserInfoUri(), client.getClient().getClientId());
        tokenServices.setRestTemplate(template);
        tokenServices.setPrincipalExtractor(client.getPrincipalExtractor());
        filter.setTokenServices(tokenServices);

        return filter;
    }

    @Bean
    @ConfigurationProperties("vk")
    public ClientResources vk() {
        return new ClientResources(vkPrincipalExtractor);
    }

    @Bean
    @ConfigurationProperties("google")
    public ClientResources google() {
        return new ClientResources(googlePrincipalExtractor);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
