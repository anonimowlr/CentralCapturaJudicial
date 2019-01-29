/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ConversaoAtributo;

import java.time.LocalDate;
//import java.util.Date;
import java.sql.Date;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 *
 * @author F3161139
 */
@Converter(autoApply = true)
public class LocalDateAttributeConverter implements AttributeConverter<LocalDate, Date> {

    /**
     *
     * @param locDate
     * @return
     */
    @Override
    public Date convertToDatabaseColumn(LocalDate locDate) {

        if (locDate != null) {
            return Date.valueOf(locDate);
        } else {
            return null;
        }

    }

    @Override
    public LocalDate convertToEntityAttribute(Date sqlDate) {

        if (sqlDate != null) {
            return sqlDate.toLocalDate();
        } else {
            return null;
        }

    }

}
