package usociety.manager.domain.util.converter;

import java.util.Objects;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import usociety.manager.domain.enums.DocumentTypeEnum;

@Converter(autoApply = true)
public class DocumentTypeConverter implements AttributeConverter<DocumentTypeEnum, String> {

    @Override
    public String convertToDatabaseColumn(DocumentTypeEnum attribute) {
        return Objects.nonNull(attribute) ? attribute.getValue() : null;
    }

    @Override
    public DocumentTypeEnum convertToEntityAttribute(String dbData) {
        try {
            return DocumentTypeEnum.valueOf(dbData);
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }

}
