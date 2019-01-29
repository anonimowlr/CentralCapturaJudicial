/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ConversaoAtributo;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 *
 * @author F3161139
 */
@Converter(autoApply = true)
public class LocalDateTimeAttributeConverter implements AttributeConverter<LocalDateTime, Timestamp>{

    @Override
    public Timestamp convertToDatabaseColumn(LocalDateTime locDate) {
       if(locDate != null)
        {
        return Timestamp.valueOf(locDate);
        }
        else
        {
        return null;
        }
    }

    @Override
    public LocalDateTime convertToEntityAttribute(Timestamp sqlTimestamp) {
        if(sqlTimestamp != null)
        {
        return sqlTimestamp.toLocalDateTime();
        }
        else
        {
        return null;
        }
            }
    
}
