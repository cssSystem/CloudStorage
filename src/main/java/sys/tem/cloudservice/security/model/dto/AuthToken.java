package sys.tem.cloudservice.security.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AuthToken(@JsonProperty("auth-token") String authToken) {

}
