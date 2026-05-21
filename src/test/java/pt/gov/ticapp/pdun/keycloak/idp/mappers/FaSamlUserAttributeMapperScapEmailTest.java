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

public class FaSamlUserAttributeMapperScapEmailTest {

  public static void main(String[] args) throws DatatypeConfigurationException {

    new FaSamlUserAttributeMapperScapEmailTest().run();
  }

  void run() throws DatatypeConfigurationException {
    final FaSamlUserAttributeMapper faSamlUserAttributeMapper = new FaSamlUserAttributeMapper();
    BrokeredIdentityContext brokeredIdentityContext = getBrokeredIdentityContext();

    faSamlUserAttributeMapper.preprocessFederatedIdentity(null, null, getIdentityProviderMapperModel(),
        brokeredIdentityContext);

    System.out.println("########## FINAL RESULT ##########");
    System.out.println(brokeredIdentityContext.getEmail());

    if (!"pedro.n.inacio@ama.pt".equals(brokeredIdentityContext.getEmail())) {
      throw new IllegalArgumentException();
    }
  }

  private BrokeredIdentityContext getBrokeredIdentityContext() throws DatatypeConfigurationException {
    BrokeredIdentityContext brokeredIdentityContext = new BrokeredIdentityContext("BIC_01", new IdentityProviderModel());

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
    AttributeType attributeType = new AttributeType("http://interop.gov.pt/SCAP/FAF");

    attributeType.setNameFormat("urn:oasis:names:tc:SAML:2.0:attrname-format:uri");
    attributeType.addAttributeValue(
        "<ScapAttributes xmlns=\"http://www.scap.autenticacao.gov.pt/FAScapAttributes\">" +
                "<AttributeSupplier>" +
                  "<Name>Empresa Teste Q 0402 1610</Name>" +
                  "<Nipc>509726607</Nipc>" +
                  "<Attributes>" +
                    "<Attribute>" +
                        "<Name>Cargo 4</Name>" +
                        "<IdentifiableName>Cargo_4</IdentifiableName>" +
                        "<SubAttributes>" +
                            "<SubAttribute>" +
                                "<Description>Nome da entidade</Description>" +
                                "<Value>Empresa Teste Q 0402 1610</Value>" +
                            "</SubAttribute>" +
                            "<SubAttribute>" +
                                "<Description>NIPC</Description>" +
                                "<Value>509726607</Value>" +
                                "</SubAttribute>" +
                            "<SubAttribute>" +
                                "<Description>E-mail do funcionário</Description>" +
                                "<Value>pedro.n.inacio@ama.pt</Value>" +
                            "</SubAttribute>" +
                            "<SubAttribute>" +
                                "<Description>Atribuído por</Description>" +
                                "<Value>Administrador</Value>" +
                            "</SubAttribute>" +
                        "</SubAttributes>" +
                    "</Attribute>" +
                "</Attributes>" +
            "</AttributeSupplier>" +
        "</ScapAttributes>");

    return new ASTChoiceType(attributeType);
  }

  private Map<String, String> getIdentityProviderMapperModelConfig() {
    Map<String, String> config = new HashMap<>();

    config.put("user.attribute", "email");
    config.put("attribute.name",
        "{ \"name\" : \"http://interop.gov.pt/SCAP/FAF\", \"selector\": \"ScapAttributes.AttributeSupplier.Attributes.Attribute.SubAttributes.SubAttribute.Description:'E-mail do funcionário|Value\", \"type\": \"XML\"}");

    return config;
  }
}
