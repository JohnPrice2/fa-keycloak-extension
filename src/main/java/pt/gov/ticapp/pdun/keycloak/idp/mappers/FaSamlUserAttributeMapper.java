package pt.gov.ticapp.pdun.keycloak.idp.mappers;

import java.util.List;
import java.util.Optional;
import org.keycloak.broker.provider.BrokeredIdentityContext;
import org.keycloak.broker.saml.mappers.AttributeToRoleMapper;
import org.keycloak.models.Constants;
import org.keycloak.models.IdentityProviderMapperModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.provider.ProviderConfigProperty;
import org.keycloak.saml.common.util.StringUtil;
import pt.gov.ticapp.pdun.keycloak.idp.configuration.PropertyReader;
import pt.gov.ticapp.pdun.keycloak.idp.data.UsernameObfuscator;
import pt.gov.ticapp.pdun.keycloak.idp.mappers.converter.AttributeNameSpecificationConverter;
import pt.gov.ticapp.pdun.keycloak.idp.mappers.model.AttributeNameSpecification;
import pt.gov.ticapp.pdun.keycloak.idp.mappers.processor.AttributeNameSpecificationProcessor;

/**
 * Based on code from <a href="https://github.com/grnet/eidas-keycloak-extension">eidas-keycloak-extension<a>.
 */
public class FaSamlUserAttributeMapper extends AttributeToRoleMapper {

  public static final String USER_ATTRIBUTE_COLLECTION_KEY = "user.attribute";
  public static final String CONTEXT_CLIENT_NOTES_KEY_USER_ATTRIBUTE_EMAIL_KEY = "CONTEXT_CLIENT_NOTES_KEY_USER_ATTRIBUTE_EMAIL_KEY";
  public static final String CONTEXT_CLIENT_NOTES_KEY_USER_USER_ATTRIBUTE_FIRST_NAME_KEY = "CONTEXT_CLIENT_NOTES_KEY_USER_USER_ATTRIBUTE_FIRST_NAME_KEY";
  public static final String CONTEXT_CLIENT_NOTES_KEY_USER_USER_ATTRIBUTE_LAST_NAME_KEY = "CONTEXT_CLIENT_NOTES_KEY_USER_USER_ATTRIBUTE_LAST_NAME_KEY";
  public static final String CONTEXT_CLIENT_NOTES_KEY_USER_USER_ATTRIBUTE_USERNAME_KEY = "CONTEXT_CLIENT_NOTES_KEY_USER_USER_ATTRIBUTE_USERNAME_KEY";
  public static final String USER_ATTRIBUTE_EMAIL_KEY = "email";
  public static final String USER_ATTRIBUTE_FIRST_NAME_KEY = "firstName";
  public static final String USER_ATTRIBUTE_LAST_NAME_KEY = "lastName";
  public static final String USER_ATTRIBUTE_USERNAME_KEY = "username";

  private static final List<ProviderConfigProperty> configProperties;

  private final AttributeNameSpecificationConverter attributeNameSpecificationConverter;
  private final AttributeNameSpecificationProcessor attributeNameSpecificationProcessor;

  static {

    configProperties = List.of(getProviderConfigProperty(ATTRIBUTE_NAME, "Attribute Name",
            "Name of attribute to search for in assertion. The attributes are located in responses in tag AttributeStatement. This value specifies the search string that will match with Attribute Name in Attribute tag"),
        getProviderConfigProperty(USER_ATTRIBUTE_COLLECTION_KEY, "User Attribute Name",
            "User attribute name to store SAML attribute. Use email, lastName, and firstName to map to those predefined user properties."));
  }

  public FaSamlUserAttributeMapper() {

    super();

    attributeNameSpecificationConverter = new AttributeNameSpecificationConverter();
    attributeNameSpecificationProcessor = new AttributeNameSpecificationProcessor();
  }

  private static ProviderConfigProperty getProviderConfigProperty(String name, String label, String helpText) {
    ProviderConfigProperty property = new ProviderConfigProperty();

    property.setName(name);
    property.setLabel(label);
    property.setHelpText(helpText);
    property.setType(ProviderConfigProperty.STRING_TYPE);

    return property;
  }

  @Override
  public String getId() {

    return PropertyReader.INSTANCE.getFaSamlIdentityProviderUserAttributeMapperId();
  }

  @Override
  public String getDisplayCategory() {

    return "Attribute Importer";
  }

  @Override
  public String getDisplayType() {

    return "Attribute Importer";
  }

  @Override
  public String[] getCompatibleProviders() {

    return new String[]{PropertyReader.INSTANCE.getFaSamlIdentityProviderId()};
  }

  @Override
  public List<ProviderConfigProperty> getConfigProperties() {

    return configProperties;
  }

  @Override
  public void preprocessFederatedIdentity(KeycloakSession session, RealmModel realm,
      IdentityProviderMapperModel mapperModel, BrokeredIdentityContext context) {

    String attribute = getAttribute(mapperModel);

    if (!StringUtil.isNullOrEmpty(attribute)) {
      AttributeNameSpecification attributeNameSpecification = attributeNameSpecificationConverter.getAttributeNameFromMapperModel(
          mapperModel);
      Optional<String> attributeValueInContext = attributeNameSpecificationProcessor.findAttributeValuesInContext(
          attributeNameSpecification, context);

      attributeValueInContext.ifPresent(value -> {
        if (attribute.equalsIgnoreCase(USER_ATTRIBUTE_EMAIL_KEY)) {
          context.setEmail(value);
          session.getContext().getAuthenticationSession()
              .setClientNote(CONTEXT_CLIENT_NOTES_KEY_USER_ATTRIBUTE_EMAIL_KEY, value);
        } else if (attribute.equalsIgnoreCase(USER_ATTRIBUTE_FIRST_NAME_KEY)) {
          context.setFirstName(value);
          session.getContext().getAuthenticationSession()
              .setClientNote(CONTEXT_CLIENT_NOTES_KEY_USER_USER_ATTRIBUTE_FIRST_NAME_KEY, value);
        } else if (attribute.equalsIgnoreCase(USER_ATTRIBUTE_LAST_NAME_KEY)) {
          context.setLastName(value);
          session.getContext().getAuthenticationSession()
              .setClientNote(CONTEXT_CLIENT_NOTES_KEY_USER_USER_ATTRIBUTE_LAST_NAME_KEY, value);
        } else if (attribute.equalsIgnoreCase(USER_ATTRIBUTE_USERNAME_KEY)) {
          context.setUsername(UsernameObfuscator.obfuscateUsername(value));
          session.getContext().getAuthenticationSession()
              .setClientNote(CONTEXT_CLIENT_NOTES_KEY_USER_USER_ATTRIBUTE_USERNAME_KEY, context.getUsername());
        } else {
          context.setUserAttribute(attribute, value);
          session.getContext().getAuthenticationSession()
              .setClientNote(Constants.USER_ATTRIBUTES_PREFIX + attribute, value);
        }
      });
    }
  }

  private String getAttribute(IdentityProviderMapperModel mapperModel) {
    String attribute = mapperModel.getConfig().get(USER_ATTRIBUTE_COLLECTION_KEY);

    if (attribute != null) {
      attribute = attribute.trim();
    }
    return attribute;
  }

  @Override
  public void importNewUser(KeycloakSession session, RealmModel realm, UserModel user,
      IdentityProviderMapperModel mapperModel, BrokeredIdentityContext context) {

  }

  @Override
  public void updateBrokeredUser(KeycloakSession session, RealmModel realm, UserModel user,
      IdentityProviderMapperModel mapperModel, BrokeredIdentityContext context) {

    preprocessFederatedIdentity(session, realm, mapperModel, context);
  }

  @Override
  public String getHelpText() {

    return "If an attribute exists in the assertions, updates the respective user attribute property value.";
  }
}
