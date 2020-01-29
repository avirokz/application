package com.queue.application.domain;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "error")
public class ErrorResponse {
    //General error message about nature of error
    private String message;

    //Specific errors in API request processing
    private List<String> details;
    public ErrorResponse(String message, List<String> details){
        super();
        this.message = message;
        this.details = details;
    }

    public String getMessage() {
        return message;
    }

    public List<String> getDetails() {
        return details;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setDetails(List<String> details) {
        this.details = details;
    }
}
