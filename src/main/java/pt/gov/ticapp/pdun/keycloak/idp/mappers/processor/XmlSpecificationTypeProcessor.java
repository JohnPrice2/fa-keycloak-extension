package pt.gov.ticapp.pdun.keycloak.idp.mappers.processor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.dataformat.xml.XmlFactory;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.xml.stream.XMLInputFactory;
import org.jboss.logging.Logger;

public class XmlSpecificationTypeProcessor {

  private static final Logger LOGGER = Logger.getLogger(XmlSpecificationTypeProcessor.class);

  String process(String value, String selector) {

    return selectXmlValue(value, selector);
  }

  private String selectXmlValue(String xmlValue, String selector) {
    String processedValue = "";

    try {
      XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
      xmlInputFactory.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, Boolean.FALSE);
      xmlInputFactory.setProperty(XMLInputFactory.SUPPORT_DTD, Boolean.FALSE);
      Object xmlRawValue = new XmlMapper(new XmlFactory(xmlInputFactory)).readValue(
          "<Root>" + xmlValue + "</Root>",
          new TypeReference<HashMap<String, Object>>() {
          });
      List<String> selectorKeys = getSelectorKeys(selector);

      for (String key : selectorKeys) {
        xmlRawValue = processKey(xmlRawValue, key);

        if (xmlRawValue == null) {
          break;
        }
      }

      processedValue = getProcessedValue(xmlRawValue);

    } catch (JsonProcessingException e) {
      LOGGER.warn("Problem while converting '" + "<Root>" + xmlValue + "</Root>" + "' to XML map.");
    }

    return processedValue;
  }

  @SuppressWarnings("rawtypes")
  private String getProcessedValue(Object xmlRawValue) {

    return xmlRawValue != null ? xmlRawValue instanceof List ? getProcessedValueFromList((List) xmlRawValue)
        : xmlRawValue.toString() : "";
  }

  @SuppressWarnings("rawtypes")
  private String getProcessedValueFromList(List xmlRawValue) {

    return xmlRawValue.isEmpty() ? "" : xmlRawValue.get(0).toString();
  }

  private List<String> getSelectorKeys(String selector) {

    return Arrays.asList(selector.split("\\."));
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  private Object processKey(Object xmlRawValue, String key) {
    Object retValue = null;
    ValueSelector valueSelector = new ValueSelector(key);

    if (xmlRawValue instanceof Map) {
      retValue = ((Map<?, ?>) xmlRawValue).get(valueSelector.getKey());
    } else if (xmlRawValue instanceof List) {
      if (((List) xmlRawValue).get(0) instanceof Map) {
        List<Map> xmlRawList = (List) xmlRawValue;

        if (valueSelector.getValue() != null) {
          final List<Map> mapList = xmlRawList.stream().filter(m -> m.containsKey(valueSelector.getKey()))
              .filter(m -> m.get(valueSelector.getKey()).equals(valueSelector.getValue())).collect(Collectors.toList());

          if (valueSelector.getFieldToFetchValue() != null) {
            retValue = mapList.stream().filter(m -> m.containsKey(valueSelector.getFieldToFetchValue()))
                .map(m -> m.get(valueSelector.getFieldToFetchValue())).collect(Collectors.toList());
          } else {
            retValue = mapList;
          }

        } else {
          LOGGER.warn(
              "Found List element but no value selector was specified. Don't know how to process it. Skipping. Discovered type: "
                  + xmlRawValue.getClass() + ". XmlValue: " + xmlRawValue + ". Selector: " + valueSelector);
        }
      } else {
        LOGGER.warn(
            "List element does not contain any Map element. Don't know how to process it. Skipping. Discovered type: "
                + xmlRawValue.getClass() + ". XmlValue: " + xmlRawValue + ". Selector: " + valueSelector);
      }
    } else {
      LOGGER.warn(
          "Don't know how to select values of type: " + xmlRawValue.getClass() + ". Skipping. XmlValue: " + xmlRawValue
              + ". Selector: " + valueSelector);
    }

    return retValue;
  }
}
