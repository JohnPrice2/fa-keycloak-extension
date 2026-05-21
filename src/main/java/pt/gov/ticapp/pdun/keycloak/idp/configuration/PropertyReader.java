package pt.gov.ticapp.pdun.keycloak.idp.configuration;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.FileBasedConfiguration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.ex.ConfigurationException;

public class PropertyReader {

  public static final PropertyReader INSTANCE;

  private final Configuration configuration;

  private PropertyReader(Configuration configuration) {

    this.configuration = configuration;
  }

  static {
    try {
      INSTANCE = new PropertyReader(
          new FileBasedConfigurationBuilder<FileBasedConfiguration>(PropertiesConfiguration.class).configure(
              new Parameters().properties().setFileName("fa-saml-idp-application.properties")).getConfiguration());
    } catch (ConfigurationException e) {
      throw new RuntimeException(e);
    }
  }

  public String getFaSamlIdentityProviderId() {

    return configuration.getString("fa.samlidentityprovider.id");
  }

  public String getFaSamlIdentityProviderName() {

    return configuration.getString("fa.samlidentityprovider.name");
  }

  public String getFaSamlIdentityProviderExtensionFaRequiredDefaultValue() {

    return configuration.getString(
        "fa.samlidentityprovider.extension.fa.requestedattributes.requestedattribute.attribute.required.default.value");
  }

  public String getFaSamlIdentityProviderExtensionFaFormatDefaultValue() {

    return configuration.getString(
        "fa.samlidentityprovider.extension.fa.requestedattributes.requestedattribute.attribute.format.default.value");
  }

  public String getFaSamlIdentityProviderExtensionFaPrefix() {

    return configuration.getString("fa.samlidentityprovider.extension.fa.prefix");
  }

  public String getFaSamlIdentityProviderExtensionFaXmlNs() {

    return configuration.getString("fa.samlidentityprovider.extension.fa.xmlns");
  }

  public String getFaSamlIdentityProviderExtensionFaRequestedAttributesTag() {

    return configuration.getString("fa.samlidentityprovider.extension.fa.requestedattributes.tag");
  }

  public String getFaSamlIdentityProviderExtensionFaRequestedAttributesRequestedAttributeTag() {

    return configuration.getString("fa.samlidentityprovider.extension.fa.requestedattributes.requestedattribute.tag");
  }

  public String getFaSamlIdentityProviderExtensionFaRequestedAttributesRequestedAttributeAttributeName() {

    return configuration.getString(
        "fa.samlidentityprovider.extension.fa.requestedattributes.requestedattribute.attribute.name");
  }

  public String getFaSamlIdentityProviderExtensionFaRequestedAttributesRequestedAttributeAttributeNameFormat() {

    return configuration.getString(
        "fa.samlidentityprovider.extension.fa.requestedattributes.requestedattribute.attribute.nameformat");
  }

  public String getFaSamlIdentityProviderExtensionFaRequestedAttributesRequestedAttributeAttributeIsRequired() {

    return configuration.getString(
        "fa.samlidentityprovider.extension.fa.requestedattributes.requestedattribute.attribute.isrequired");
  }

  public String getFaSamlIdentityProviderExtensionFaFaAaLevelTag() {

    return configuration.getString("fa.samlidentityprovider.extension.fa.faaalevel.tag");
  }

  public String getFaSamlIdentityProviderExtensionFaFaAaLevelDefaultValue() {

    return configuration.getString("fa.samlidentityprovider.extension.fa.faaalevel.default.value");
  }

  public String getFaSamlIdentityProviderUserTemplateMapperId() {

    return configuration.getString("fa.samlidentityprovider.usertemplate.mapper.id");
  }

  public String getFaSamlIdentityProviderUserAttributeMapperId() {

    return configuration.getString("fa.samlidentityprovider.userattribute.mapper.id");
  }

  public String getFaSamlIdentityProviderExtensionFaCancelledAuthenticationStatusMessage() {

    return configuration.getString("fa.samlidentityprovider.extension.fa.cancelled.authentication.status.message");
  }


  public String getFaSamlIdentityProviderExtensionFaEndpointLoginResponseResultStatusQueryStringParameter() {

    return configuration.getString(
        "fa.samlidentityprovider.extension.fa.endpoint.login.response.resultstatus.querystring.parameter");
  }

  public int getFaSamlIdentityProviderExtensionFaEndpointLoginResponseResultStatusBackWasPressed() {

    return configuration.getInt(
        "fa.samlidentityprovider.extension.fa.endpoint.login.response.resultstatus.backwaspressed");
  }

  public int getFaSamlIdentityProviderExtensionFaEndpointLoginResponseResultStatusMissingUserAttributesOnAutenticacaoGov() {

    return configuration.getInt(
        "fa.samlidentityprovider.extension.fa.endpoint.login.response.resultstatus.missinguserattributesonautenticacaogov");
  }

  public int getFaSamlIdentityProviderExtensionFaEndpointLoginResponseResultStatusUnexpectedError() {

    return configuration.getInt(
        "fa.samlidentityprovider.extension.fa.endpoint.login.response.resultstatus.unexpectederror");
  }

  public String getFaSamlIdentityProviderExtensionFaCalculatedAssertionPrincipalName() {

    return configuration.getString(
        "fa.samlidentityprovider.extension.fa.calculated.assertion.principal.name");
  }

  public String getFaSamlIdentityProviderExtensionFaCalculatedAssertionPrincipalFrom() {

    return configuration.getString(
        "fa.samlidentityprovider.extension.fa.calculated.assertion.principal.from");
  }
}
