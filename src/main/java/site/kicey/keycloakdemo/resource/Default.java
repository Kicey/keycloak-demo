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
@RequestMapping("/")
public class Default {
    
    @GetMapping
    public SimpleResponse getCustomer(HttpServletRequest request, KeycloakAuthenticationToken authentication) {
        if(authentication == null){
            return new SimpleResponse("未认证访问", request.getRequestURI(), null);
        }
        return new SimpleResponse("目前角色权限：" + authentication.getAuthorities().toString(), request.getRequestURI(),
                authentication.getAccount().getKeycloakSecurityContext().getIdToken());
    }
}
