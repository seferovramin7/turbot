package com.turboparser.turbo.dto.telegram.send;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import com.turboparser.turbo.dto.telegram.update.ParameterDTO;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SendMessageResponseDTO {

    @JsonProperty("ok")
    private Boolean ok;

    @JsonProperty("error_code")
    private Long errorCode;

    @JsonProperty("description")
    private String description;

    @JsonProperty("parameters")
    private ParameterDTO parameters;


}
