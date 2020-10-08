package usociety.manager.domain.service.post.dto;

import javax.validation.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModel;

@ApiModel("Survey option representation.")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class SurveyOption {

    @JsonProperty
    private Integer id;

    @NotEmpty
    @JsonProperty
    private String value;

    public SurveyOption() {
        super();
    }

    public SurveyOption(Integer id, String value) {
        this.id = id;
        this.value = value;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

}
