package pt.gov.ticapp.pdun.keycloak.idp.mappers.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jboss.logging.Logger;
import org.keycloak.broker.saml.mappers.AttributeToRoleMapper;
import org.keycloak.models.IdentityProviderMapperModel;
import pt.gov.ticapp.pdun.keycloak.idp.mappers.model.AttributeNameSpecification;

public class AttributeNameSpecificationConverter {

  private static final Logger LOGGER = Logger.getLogger(AttributeNameSpecificationConverter.class);

  public AttributeNameSpecification getAttributeNameFromMapperModel(IdentityProviderMapperModel mapperModel) {
    String attributeName = mapperModel.getConfig().get(AttributeToRoleMapper.ATTRIBUTE_NAME);
    AttributeNameSpecification attributeNameSpecification = null;

    try {
      attributeNameSpecification = new ObjectMapper().readValue(attributeName, AttributeNameSpecification.class);

      if (attributeNameSpecification.getName() == null || attributeNameSpecification.getAttributeNameSpecificationType() == null) {
        LOGGER.warn("Attribute Name Specification '" + attributeName + "' is missing mandatory fields 'name' or 'type'.");
        return null;
      }
    } catch (JsonProcessingException e) {
      LOGGER.warn(
          "Could not map attribute name '" + attributeName + "'. It is not a valid Attribute Name Specification.");
    }

    return attributeNameSpecification;
  }
}
