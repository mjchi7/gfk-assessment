package posmy.interview.boot.data.ephemeral;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.Map;

@AllArgsConstructor
@Data
@ToString
public class APIErrorData implements Serializable {

    private String exceptionId;

    private Integer status;

    private String message;

    private Map<String, Object> details;
}
