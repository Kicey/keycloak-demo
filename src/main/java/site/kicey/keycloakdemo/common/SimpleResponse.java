package site.kicey.keycloakdemo.common;

import lombok.Data;

/**
 * @author Kicey
 */
@Data
public class SimpleResponse {
    private final String message;
    private final String path;
    private final Object idToken;
}
