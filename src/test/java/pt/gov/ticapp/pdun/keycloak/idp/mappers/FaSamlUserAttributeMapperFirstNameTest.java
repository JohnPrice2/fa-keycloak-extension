package pt.gov.ticapp.pdun.keycloak.idp.mappers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.datatype.DatatypeConfigurationException;
import org.keycloak.broker.provider.BrokeredIdentityContext;
import org.keycloak.broker.saml.SAMLEndpoint;
import org.keycloak.models.IdentityProviderModel;
import org.keycloak.dom.saml.v2.assertion.AssertionType;
import org.keycloak.dom.saml.v2.assertion.AttributeStatementType;
import org.keycloak.dom.saml.v2.assertion.AttributeStatementType.ASTChoiceType;
import org.keycloak.dom.saml.v2.assertion.AttributeType;
import org.keycloak.dom.saml.v2.assertion.StatementAbstractType;
import org.keycloak.models.IdentityProviderMapperModel;

public class FaSamlUserAttributeMapperFirstNameTest {

  public static void main(String[] args) throws DatatypeConfigurationException {

    new FaSamlUserAttributeMapperFirstNameTest().run();
  }

  void run() throws DatatypeConfigurationException {
    final FaSamlUserAttributeMapper faSamlUserAttributeMapper = new FaSamlUserAttributeMapper();
    BrokeredIdentityContext brokeredIdentityContext = getBrokeredIdentityContext();

    faSamlUserAttributeMapper.preprocessFederatedIdentity(null, null, getIdentityProviderMapperModel(),
        brokeredIdentityContext);

    System.out.println("########## FINAL RESULT ##########");
    System.out.println(brokeredIdentityContext.getFirstName());

    if (!"Pedrito Inácio".equals(brokeredIdentityContext.getFirstName())) {
      throw new IllegalArgumentException();
    }
  }

  private BrokeredIdentityContext getBrokeredIdentityContext() throws DatatypeConfigurationException {
    BrokeredIdentityContext brokeredIdentityContext = new BrokeredIdentityContext("one", new IdentityProviderModel());

    brokeredIdentityContext.setContextData(getBrokeredIdentityContextContextData());

    return brokeredIdentityContext;
  }

  private IdentityProviderMapperModel getIdentityProviderMapperModel() {
    IdentityProviderMapperModel identityProviderMapperModel = new IdentityProviderMapperModel();

    identityProviderMapperModel.setConfig(getIdentityProviderMapperModelConfig());

    return identityProviderMapperModel;
  }

  private Map<String, Object> getBrokeredIdentityContextContextData() throws DatatypeConfigurationException {
    Map<String, Object> contextData = new HashMap<>();

    contextData.put(SAMLEndpoint.SAML_ASSERTION, getSamlAssertion());

    return contextData;
  }

  private Object getSamlAssertion() throws DatatypeConfigurationException {
    AssertionType assertionType = new AssertionType("_aed14629-5a20-41ee-b6e3-bd43d5d8b7ba",
        XmlGregorianCalendarUtil.createXmlGregorianCalendar());

    assertionType.addStatements(getAttributeStatements());

    return assertionType;
  }

  private Collection<StatementAbstractType> getAttributeStatements() {
    List<StatementAbstractType> attributeStatements = new ArrayList<>();

    attributeStatements.add(getAttributeStatementType());

    return attributeStatements;
  }

  private StatementAbstractType getAttributeStatementType() {
    final AttributeStatementType attributeStatementType = new AttributeStatementType();

    attributeStatementType.addAttributes(getAstChoiceTypes());

    return attributeStatementType;
  }

  private List<ASTChoiceType> getAstChoiceTypes() {
    List<ASTChoiceType> astChoiceTypes = new ArrayList<>();

    astChoiceTypes.add(getAttributeType());

    return astChoiceTypes;
  }

  private ASTChoiceType getAttributeType() {
    AttributeType attributeType = new AttributeType("http://interop.gov.pt/MDC/Cidadao/NomeCompleto");

    attributeType.setNameFormat("urn:oasis:names:tc:SAML:2.0:attrname-format:uri");
    attributeType.addAttributeValue("Pedrito Inácio");

    return new ASTChoiceType(attributeType);
  }

  private Map<String, String> getIdentityProviderMapperModelConfig() {
    Map<String, String> config = new HashMap<>();

    config.put("user.attribute", "firstName");
    config.put("attribute.name",
        "{\"name\" : \"http://interop.gov.pt/MDC/Cidadao/NomeCompleto\", \"type\":\"DIRECT\"}");

    return config;
  }
}
