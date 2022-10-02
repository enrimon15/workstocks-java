package it.workstocks.dto.error;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor    
@AllArgsConstructor
public class ErrorValidationModel{
    private String fieldName;
    private Object rejectedValue;
    private String messageError;
}
