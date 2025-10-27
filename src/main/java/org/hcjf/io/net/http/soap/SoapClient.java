package org.hcjf.io.net.http.soap;

import org.hcjf.encoding.MimeType;
import org.hcjf.errors.HCJFRuntimeException;
import org.hcjf.io.net.http.HttpClient;
import org.hcjf.io.net.http.HttpHeader;
import org.hcjf.io.net.http.HttpMethod;
import org.hcjf.io.net.http.HttpResponse;
import org.hcjf.utils.Introspection;
import org.hcjf.utils.Strings;
import org.hcjf.utils.XmlUtils;

import java.net.URL;
import java.util.*;

public class SoapClient {

    private static final class WsdlKeys {
        private static final class Objects {
            private static final String DEFINITIONS = "wsdl:definitions";
            private static final String SERVICE = "wsdl:service";
            private static final String PORT = "wsdl:port";
            private static final String BINDING = "wsdl:binding";
            private static final String TYPES = "wsdl:types";
            private static final String PORT_TYPE = "wsdl:portType";
            private static final String MESSAGE = "wsdl:message";
            private static final String OPERATION = "wsdl:operation";
            private static final String INPUT = "wsdl:input";
            private static final String SOAP_HEADER = "soap:header";
            private static final String SOAP_BODY = "soap:body";
        }
        private static final String SCHEMA = "s:schema";
        private static final String ELEMENT = "s:element";
        private static final String COMPLEX_TYPE = "s:complexType";
        private static final String SEQUENCE = "s:sequence";
        private static final String NAME = "name";
        private static final String TYPE = "type";
        private static final String USE = "use";
        private static final String PART = "part";
        private static final String MESSAGE = "message";
        private static final String LITERAL = "literal";
        private static final String BINDING = "binding";
        private static final String TARGET_NAMESPACE = "targetNamespace";
        private static final String ABBREVIATE_SOAPENV = "soapenv";
        private static final String ENVELOPE = "%s:Envelope";
        private static final String HEADER = "%s:Header";
        private static final String BODY = "%s:Body";
        private static final String XMLNS = "xmlns:%s";
        private static final String ENVELOP_DEFINITION_URL = "http://schemas.xmlsoap.org/soap/envelope/";
        private static final String TNS_PREFIX = "tns:";
    }

    private final String wsdlUrl;
    private final String wsdl;
    private final Map<String,Object> definitions;

    public SoapClient(String wsdlUrl) {
        try {
            HttpClient httpClient = new HttpClient(new URL(wsdlUrl));
            httpClient.setHttpMethod(HttpMethod.GET);
            HttpResponse response = httpClient.request();
            this.wsdlUrl = wsdlUrl;
            wsdl = new String(response.getBody());
            definitions = Introspection.resolve(XmlUtils.parse(wsdl), WsdlKeys.Objects.DEFINITIONS);
            System.out.println();
        } catch (Exception ex) {
            throw new HCJFRuntimeException("Unable to get wsdl", ex);
        }
    }

    public Map<String,Object> request(String portName, String operationName, Map<String,Object> parameters) {
        Map<String,Object> result;
        Map<String,Object> requestBody = new LinkedHashMap<>();
        Map<String,Object> envelope = new LinkedHashMap<>();
        requestBody.put(String.format(WsdlKeys.ENVELOPE, WsdlKeys.ABBREVIATE_SOAPENV), envelope);
        envelope.put(String.format(WsdlKeys.XMLNS, WsdlKeys.ABBREVIATE_SOAPENV), WsdlKeys.ENVELOP_DEFINITION_URL);
        envelope.put(String.format(WsdlKeys.XMLNS, getAbbreviatedNamespace()), getNamespace());
        System.out.println(XmlUtils.toXml(requestBody));

        Map<String,Object> service = getService();
        Map<String,Object> port = getPort(portName);
        if (port == null) {
            throw new HCJFRuntimeException("Port not found '%s' for for service %s", portName);
        }

        Map<String,Object> binding = getBindingByType(Introspection.resolve(port, WsdlKeys.BINDING));
        Map<String,Object> operation = null;
        List<Map<String,Object>> operations = Introspection.resolve(binding, WsdlKeys.Objects.OPERATION);
        for (Map<String,Object> o : operations) {
            String name = Introspection.resolve(o, WsdlKeys.NAME);
            if (name.equals(operationName)) {
                operation = o;
                break;
            }
        }
        if (operation == null) {
            throw new HCJFRuntimeException("Operation not found '%s'", operationName);
        }

        Map<String,Object> header = Introspection.resolve(operation, WsdlKeys.Objects.INPUT, WsdlKeys.Objects.SOAP_HEADER);
        if (header != null) {
            Map<String,Object> responseHeader = new HashMap<>();
            if (Introspection.resolve(header, WsdlKeys.USE).equals(WsdlKeys.LITERAL)) {
                String headerType = Introspection.resolve(header, WsdlKeys.PART);
                Map<String,Object> element = getTypeElement(headerType);
                List<Map<String,Object>> typeParameters = getParametersByElement(element);
                Map<String,Object> elementTag = createElementTag(typeParameters, parameters, null);
                responseHeader.put(String.format("%s:%s", getAbbreviatedNamespace(), Introspection.resolve(element, WsdlKeys.NAME)), elementTag);
                System.out.println();
            }
            envelope.put(String.format(WsdlKeys.HEADER, WsdlKeys.ABBREVIATE_SOAPENV), responseHeader);
        }

        Map<String,Object> body = Introspection.resolve(operation, WsdlKeys.Objects.INPUT, WsdlKeys.Objects.SOAP_BODY);
        if (body != null) {
            Map<String,Object> responseBody = new HashMap<>();
            if (Introspection.resolve(body, WsdlKeys.USE).equals(WsdlKeys.LITERAL)) {
                Map<String,Object> element = getTypeElement(operationName);
                List<Map<String,Object>> typeParameters = getParametersByElement(element);
                Map<String,Object> elementTag = createElementTag(typeParameters, parameters, null);
                responseBody.put(String.format("%s:%s", getAbbreviatedNamespace(), Introspection.resolve(element, WsdlKeys.NAME)), elementTag);
                System.out.println();
            }
            envelope.put(String.format(WsdlKeys.BODY, WsdlKeys.ABBREVIATE_SOAPENV), responseBody);
        }

        System.out.println(XmlUtils.toXml(requestBody));

        try {
            byte[] bodyBytes = XmlUtils.toXml(requestBody).getBytes();
            HttpClient client = new HttpClient(new URL("http://gcaba.urbetrack.com/App_Services/Higiene.asmx"));
            client.addHttpHeader(new HttpHeader(HttpHeader.CONTENT_TYPE, MimeType.TEXT_XML.toString()));
            client.addHttpHeader(new HttpHeader(HttpHeader.CONTENT_LENGTH, Integer.toString(bodyBytes.length)));
            client.setBody(bodyBytes);
            client.setHttpMethod(HttpMethod.POST);
            HttpResponse response = client.request();

            result = XmlUtils.parse(new String(response.getBody()));
        } catch (Exception ex) {
            throw new HCJFRuntimeException("SOAP Request fail", ex);
        }

        return result;
    }

    private Map<String,Object> createElementTag(List<Map<String,Object>> typeParameters, Map<String,Object> parameters, String rootParameterName) {
        Map<String,Object> elementTag = new LinkedHashMap<>();
        for (Map<String,Object> typeParameter : typeParameters) {
            String parameterName = Introspection.resolve(typeParameter, WsdlKeys.NAME);
            String parameterType = Introspection.resolve(typeParameter, WsdlKeys.TYPE);
            Map<String,Object> parameterTag = new HashMap<>();
            if (parameterType.startsWith(WsdlKeys.TNS_PREFIX)) {
                String innerName = parameterType.replace(WsdlKeys.TNS_PREFIX, Strings.EMPTY_STRING);
                List<Map<String,Object>> innerTypeParameters;
                Object obj = Introspection.resolve(getComplexType(innerName), WsdlKeys.SEQUENCE, WsdlKeys.ELEMENT);
                if (obj instanceof Map) {
                    innerTypeParameters = new ArrayList<>();
                    innerTypeParameters.add((Map<String, Object>) obj);
                } else {
                    innerTypeParameters = (List<Map<String, Object>>) obj;
                }
                parameterTag = createElementTag(innerTypeParameters, parameters, rootParameterName == null ? parameterName : rootParameterName);
            } else {
                parameterTag.put("_value", parameters.get(rootParameterName == null ? parameterName : rootParameterName));
            }
            elementTag.put(String.format("%s:%s", getAbbreviatedNamespace(), parameterName), parameterTag);
        }

        return elementTag;
    }

    private List<Map<String,Object>> getParametersByElement(Map<String,Object> element) {
        List<Map<String,Object>> typeParameters;
        if (element.containsKey(WsdlKeys.COMPLEX_TYPE)) {
            typeParameters = Introspection.resolve(element, WsdlKeys.COMPLEX_TYPE, WsdlKeys.SEQUENCE, WsdlKeys.ELEMENT);
        } else {
            String complexTypeName = Introspection.resolve(element.get(WsdlKeys.NAME));
            typeParameters = Introspection.resolve(getComplexType(complexTypeName), WsdlKeys.SEQUENCE, WsdlKeys.ELEMENT);
        }
        return typeParameters;
    }

    public String getNamespace() {
        return Introspection.resolve(definitions, WsdlKeys.TARGET_NAMESPACE);
    }

    public String getAbbreviatedNamespace() {
        String namespace = getNamespace();
        int startIndex = namespace.indexOf(Strings.CLASS_SEPARATOR);
        int endIndex = namespace.indexOf(Strings.CLASS_SEPARATOR, startIndex + 1);
        return namespace.substring(startIndex + 1, endIndex);
    }

    public Map<String,Object> getService() {
        return Introspection.resolve(definitions, WsdlKeys.Objects.SERVICE);
    }

    public List<Map<String,Object>> getPorts() {
        Map<String,Object> service = getService();
        return Introspection.resolve(service, WsdlKeys.Objects.PORT);
    }

    public Map<String,Object> getPort(String name) {
        Map<String,Object> result = null;
        for (Map<String,Object> port : getPorts()) {
            String portName = Introspection.resolve(port, WsdlKeys.NAME);
            if (portName.equals(name)) {
                result = port;
                break;
            }
        }
        return result;
    }

    public List<Map<String,Object>> getBindings() {
        return Introspection.resolve(definitions, WsdlKeys.Objects.BINDING);
    }

    public Map<String,Object> getBinding(String name) {
        Map<String,Object> result = null;
        for (Map<String,Object> binding : getBindings()) {
            String bindingName = Introspection.resolve(binding, WsdlKeys.NAME);
            if (bindingName.equals(name)) {
                result = binding;
                break;
            }
        }
        return result;
    }

    public Map<String,Object> getBindingByType(String bindingType) {
        Map<String,Object> result = null;
        for (Map<String,Object> binding : getBindings()) {
            String type = Introspection.resolve(binding, WsdlKeys.TYPE);
            if (type.equals(bindingType)) {
                result = binding;
                break;
            }
        }
        return result;
    }

    public Map<String,Object> getPortType() {
        return Introspection.resolve(definitions, WsdlKeys.Objects.PORT_TYPE);
    }

    public Map<String,Object> getTypes() {
        return Introspection.resolve(definitions, WsdlKeys.Objects.TYPES);
    }

    public Map<String,Object> getTypeElement(String elementName) {
        Map<String,Object> result = null;
        List<Map<String,Object>> typeElements = Introspection.resolve(getTypes(), WsdlKeys.SCHEMA, WsdlKeys.ELEMENT);
        for (Map<String,Object> typeElement : typeElements) {
            String name = Introspection.resolve(typeElement, WsdlKeys.NAME);
            if (name.equals(elementName)) {
                result = typeElement;
            }
        }
        return result;
    }

    public Map<String,Object> getComplexType(String complexTypeName) {
        Map<String,Object> result = null;
        List<Map<String,Object>> complexTypes = Introspection.resolve(getTypes(), WsdlKeys.SCHEMA, WsdlKeys.COMPLEX_TYPE);
        for (Map<String,Object> complexType : complexTypes) {
            String name = Introspection.resolve(complexType, WsdlKeys.NAME);
            if (name.equals(complexTypeName)) {
                result = complexType;
            }
        }
        return result;
    }

}
