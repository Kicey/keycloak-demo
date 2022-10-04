# Spring Security 集成用户系统（Keycloak）

后端同学在不使用模板项目（内部已经做了例如用户系统，安全认证，会话管理等，只需要定制自己的业务逻辑，简单的开发很便捷，更新维护和修改原有模块就是得在代码堆中挣扎了）的情况下，首先需要实现的就是一个用户系统，以及相关的安全配置和逻辑。

自己去实现一个完善的用户系统过于复杂，那么我们就涉及到用户系统选型的问题了，这里推荐 Keycloak。Keycloak 实现了 Oidc，Saml 等身份协议，并兼容 Saml，允许定制化，并且开源！

Life is short, show me the code.

这个 demo 是下载即可运行的，demo 中使用的 Keycloak 部署在我的个人服务器上，在 2025 年之前它将是有效的。运行之后先试试：

* 127.0.0.1:8765
* 127.0.0.1:8765/customer

KeyCloak 相关的配置如下：

设置了 Oidc 必要的一些 Client 配置

```yaml
keycloak:
  realm: demo
  auth-server-url: 'https://identity.kicey.site/'
  ssl-required: external
  resource: keycloak-demo
  verify-token-audience: true
  credentials:
    secret: xCi1aChVriLE13afwZfs5pOpC15RoppI
  use-resource-role-mappings: true
  confidential-port: 0
```

Spring Security 的配置如下：

* 使用一个全局的身份认证器（向 spring security AuthenticationManager 注册）
* 指定 Session 策略，具体的 Session 由 spring security 实现，这里相当于只是选择一个配置项
* 配置安全规则

```java
@KeycloakConfiguration
public class WebSecurityConfig extends KeycloakWebSecurityConfigurerAdapter {
    /**
     * Registers the KeycloakAuthenticationProvider with the authentication manager.
     */
    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(keycloakAuthenticationProvider());
    }
    
    /**
     * Defines the session authentication strategy.
     */
    @Bean
    @Override
    protected SessionAuthenticationStrategy sessionAuthenticationStrategy() {
        return new RegisterSessionAuthenticationStrategy(new SessionRegistryImpl());
    }
    
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        super.configure(http);
        http
                .csrf().disable()
                .authorizeRequests()
                .antMatchers("/customer/**").hasAuthority("CUSTOMER")
                .antMatchers("/admin/**").hasAuthority("ADMIN")
                .antMatchers("/provider/**").hasAuthority("PROVIDER")
                .anyRequest().permitAll();
    }
}
```

下面的 Bean 负责加载 application.yml 中的 Keycloak 配置（默认是 META-INF/keycloak.json）

```java
@Configuration
public class BeanConfig {
    @Bean
    public KeycloakConfigResolver keycloakConfigResolver(){
        return new KeycloakSpringBootConfigResolver();
    }
}
```

在接口中使用身份信息的方式如下

```java
package site.kicey.keycloakdemo.resource;

import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import site.kicey.keycloakdemo.common.SimpleResponse;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Kicey
 */
@RestController()
@RequestMapping("/customer")
public class Customer {
    
    @GetMapping
    public SimpleResponse getCustomer(HttpServletRequest request, KeycloakAuthenticationToken authentication) {
        return new SimpleResponse("目前角色权限：" + authentication.getAuthorities().toString(), request.getRequestURI(),
                authentication.getAccount().getKeycloakSecurityContext().getIdToken());
    }
}
```

这是一个标准的 spring 自动注入的方式，也可以使用 SecurityContext （底层由 ThreadLocal 实现）静态方法获取 authentication，方便在 controller 之下的 service 方法中使用而减少不必要的参数传递。

只需要很少的代码，就完成了一个后端系统的身份系统和安全配置，congratulation！

题外话：建立 https 的 TLS/SSL 是一个比较耗时的操作，并且配置也相对麻烦，不建议在每个服务都使用。可行的方式是在网关处使用（spring-cloud-gateway 或者 nginx）。
