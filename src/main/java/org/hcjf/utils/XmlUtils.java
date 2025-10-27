package org.hcjf.utils;

import org.hcjf.encoding.MimeType;
import org.hcjf.io.net.http.HttpClient;
import org.hcjf.io.net.http.HttpHeader;
import org.hcjf.io.net.http.HttpMethod;
import org.hcjf.io.net.http.HttpResponse;
import org.hcjf.io.net.http.soap.SoapClient;
import org.hcjf.layers.Layer;
import org.hcjf.layers.LayerInterface;
import org.hcjf.layers.Layers;
import org.hcjf.layers.distributed.DistributedLayerInterface;

import java.net.URL;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class XmlUtils {

    private static final class Fields {
        private static final String VERSION = "_version";
        private static final String VALUE = "_value";
        private static final String HAS_CHILDREN = "__¿HAS_CHILDREN¿__";
        private static final String CLOSED = "__¿CLOSED¿__";
        private static final String TOKEN_FORMAT = "¿%d·";
        private static final String COMMENT_START = "<!--";
        private static final String COMMENT_END = "-->";
        private static final String CDATA_START = "<![CDATA[";
        private static final String CDATA_END = "]]>";
        private static final String CDATA = "__¿CDATA__%d¿__";
    }

    private static final class Patterns {
        private static final String OPEN_TAG = "<%s%s>";
        private static final String CLOSE_TAG = "</%s>";
        private static final String INLINE_TAG = "<%s%s/>";
        private static final String ATTRIBUTE = " %s=\"%s\"";
        private static final String CDATA = "<![CDATA[%s]]>";
        private static final String ATTRIBUTE_GROUP_NAME = "attribute";
        private static final Pattern TAG_ATTRIBUTES_PATTERN = Pattern.compile("(?<attribute>[a-zA-Z0-9_\\-]+[ ]*=[ ]*\"[a-zA-Z0-9_\\-.:/+*#$%& ]+\"[ ]*)");
    }

    public interface Printer extends LayerInterface  {

        void print(Map<String,Object> obj);

    }

    public static class XMLPrinter extends Layer implements Printer, DistributedLayerInterface {

        @Override
        public String getImplName() {
            return "xml";
        }

        @Override
        public void print(Map<String, Object> obj) {
            System.out.println("Imprime como XML");
        }

    }

    public static class JsonPrinter extends Layer implements Printer {

        @Override
        public String getImplName() {
            return "json";
        }

        @Override
        public void print(Map<String, Object> obj) {
            System.out.println("Imprime como Json");
        }

    }

    public static void main(String[] args) throws Exception {
        Layers.publishLayer(XMLPrinter.class);
        Layers.publishLayer(JsonPrinter.class);

        //Layers.get(Printer.class, "csv").print(Map.of());


        SoapClient soapClient = new SoapClient("http://gcaba.urbetrack.com/App_Services/Higiene.asmx?wsdl");
        System.out.println(soapClient.getNamespace());
        System.out.println(soapClient.getAbbreviatedNamespace());
        Map<String,Object> model = new HashMap<>();
        model.put("username", "javaito");
        model.put("password", "pass");
        model.put("codigosRutas", List.of("c1", "c2", "c3"));
        model.put("estado", 1);
        model.put("tipoPuntoRecoleccion", 1);
        model.put("latitud", 52.36);
        model.put("altura", 234);
        model.put("tipoMobiliario", "tipo1");
        model.put("codigoPuntoRecoleccion", "AA23");
        model.put("descripcion", "Hola Mundo!");
        model.put("calle", "Mitre");
        model.put("longitud", 52.3265);
        Map<String,Object> response = soapClient.request("Higiene","CrearPuntoRecoleccionMultiplesRutas", model);

        String xml = "";
    }

    public static void main1(String[] args) throws Exception {



//        String regex = "(?<attribute>[a-zA-Z0-9_\\-]{1,}[ ]{0,}\\=[ ]{0,}\\\"[a-zA-Z0-9_\\- ]{1,}\\\"[ ]{0,})";
//        String value1 = "field=\"valu  AAe\"      field=\"valu  BBe\"     field=\"valu  CCe\"";
//
//        Pattern pattern = Pattern.compile("(?<attribute>[a-zA-Z0-9_\\-]+[ ]*=[ ]*\"[a-zA-Z0-9_\\- ]+\"[ ]*)");
//        Matcher matcher = pattern.matcher(value1);
//        String[] s = pattern.split(value1);
////        boolean match = matcher.matches();
//        boolean find = matcher.find();
//        int start = 0;
//        while(matcher.find(start)) {
//            System.out.println(matcher.group("attribute"));
//            start = matcher.end();
//        }
//        System.out.println();


//        long time = System.currentTimeMillis();
//        Collection<Map<String,Object>> ways = new ArrayList<>();
//        Collection<Way> ways1 = new ArrayList<>();
//        Collection<Map<String,Object>> nodes = new ArrayList<>();
//        Collection<Node> nodes1 = new ArrayList<>();
//        Collection<Map<String,Object>> relations = new ArrayList<>();
//        Collection<Relation> relations1 = new ArrayList<>();
////        FileInputStream inputStream = new FileInputStream(Path.of("/", "home", "javaito", "Descargas", "cartografia", "18.pbf").toFile());
//        FileInputStream inputStream = new FileInputStream(Path.of("/", "home", "javaito", "Descargas", "cartografia", "mendoza.pbf", "18.pbf").toFile());
////        FileInputStream inputStream = new FileInputStream(Path.of("/", "home", "javaito", "Descargas", "cartografia", "losangeles18min.pbf").toFile());
//        new ParallelBinaryParser(inputStream, 8).
////                onWay(way -> ways.add(Introspection.toDeepMap(way))).
//                onWay(way -> ways1.add(way)).
////                onNode(node -> nodes.add(Introspection.toDeepMap(node))).
//                onNode(node -> nodes1.add(node)).
////                onRelation(relation -> relations.add(Introspection.toDeepMap(relation))).
//                onRelation(relation -> relations1.add(relation)).
//                onBoundBox(boundBox -> {
//                    System.out.println(boundBox.toString());
//                }).
//                parse();
//        System.out.println("Time: " + (System.currentTimeMillis() - time));


//        Map<String,Object> map = new HashMap<>();
//        map.put("nodes", nodes);
//        map.put("ways", ways);
//        map.put("relations", relations);
//
//        byte[] bytes = BsonEncoder.encode(new BsonDocument(map));
//        Files.write(Path.of("/", "home", "javaito", "test.bson"), bytes);
//
//        time = System.currentTimeMillis();
//        bytes = Files.readAllBytes(Path.of("/", "home", "javaito", "test.bson"));
//        map = BsonDecoder.decode(bytes).toMap();
//        System.out.println("Time: " + (System.currentTimeMillis() - time));

//        time = System.currentTimeMillis();
////        String xmlFile = Files.readString(Path.of("/", "home", "javaito", "Descargas", "18.osm"));
////        String xmlFile = Files.readString(Path.of("/", "home", "javaito", "Descargas", "zapata.osm"));
////        Map<String,Object> value = parse(xmlFile);
//        System.out.println("Time: " + (System.currentTimeMillis() - time));
////        Files.write(Path.of("/", "home", "javaito", "Descargas", "mza_01.json"), JsonUtils.toJsonTree(value).toString().getBytes(StandardCharsets.UTF_8));
////        System.out.println(JsonUtils.toJsonTree(value).toString());
//
//        Queryable.DataSource<Map<String,Object>> ds = new Queryable.DataSource<Map<String, Object>>() {
//
//            //private Collection<Map<String,Object>> nodes = Introspection.resolve(value, "osm.node");
//            //private Collection<Map<String,Object>> ways = Introspection.resolve(value, "osm.way");
//            //private Collection<Map<String,Object>> relation = Introspection.resolve(value, "osm.relation");
//
//            @Override
//            public Collection<Map<String, Object>> getResourceData(Queryable queryable) {
//                Collection<Map<String, Object>> result;
//                String resourceName = queryable.getResourceName();
//                if(resourceName.equals("node")) {
//                    result = queryable.evaluate(nodes);
//                } else if(resourceName.equals("way")) {
//                    result = queryable.evaluate(ways);
//                } else if(resourceName.equals("relation")) {
//                    result = queryable.evaluate(relations);
//                } else {
//                    throw new HCJFRuntimeException("Resource not found: %s", resourceName);
//                }
//                return result;
//            }
//        };

//        HttpServer server = new HttpServer(9132);
//        server.addContext(new Context(".*") {
//            @Override
//            public HttpResponse onContext(HttpRequest request) {
//                String query = Introspection.resolve(request.getParameters(), "q");
//                Queryable queryable = Query.compile(query);
//                Collection<Map<String,Object>> result = queryable.evaluate(ds);
//                byte[] body = JsonUtils.toJsonTree(result).toString().getBytes(StandardCharsets.UTF_8);
//                HttpResponse response = new HttpResponse();
//                response.setResponseCode(HttpResponseCode.OK);
//                response.addHeader(new HttpHeader(HttpHeader.CONTENT_TYPE, MimeType.APPLICATION_JSON.toString()));
//                response.addHeader(new HttpHeader(HttpHeader.CONTENT_LENGTH, Integer.toString(body.length)));
//                response.setBody(body);
//                return response;
//            }
//        });
//        server.start();
    }

    /**
     * Parse the xml file and creates a map representation with all the information.
     * @param xml XML file to parse.
     * @return Map instance with all the information of the xml file.
     */
    public static Map<String,Object> parse(String xml) {
        //Removes all the comments into the xml file
        List<String> commentGroups = Strings.replaceableGroup(xml, Fields.COMMENT_START, Fields.COMMENT_END);
        String uncommentedXml = commentGroups.get(commentGroups.size() - 1);
        for (int i = 0; i < commentGroups.size() - 1; i++) {
            uncommentedXml = uncommentedXml.replace(String.format(Fields.TOKEN_FORMAT, i), Strings.EMPTY_STRING);
        }

        Map<String,String> cdataValues = new HashMap<>();
        List<String> cdataGroups = Strings.replaceableGroup(uncommentedXml, Fields.CDATA_START, Fields.CDATA_END);
        String cleanXml = cdataGroups.get(cdataGroups.size() - 1);
        for (int i = 0; i < cdataGroups.size() - 1; i++) {
            String cdataKey = String.format(Fields.CDATA, i);
            cdataValues.put(cdataKey, cdataGroups.get(i));
            cleanXml = cleanXml.replace(String.format(Fields.TOKEN_FORMAT, i), cdataKey);
        }

        List<String> groups = Strings.group(cleanXml, Strings.START_TAG, Strings.END_TAG, false, false);
        AtomicInteger index = new AtomicInteger(groups.size() - 1);
        Map<String, Object> result = new HashMap<>();
        do {
            result.putAll(parse(new HashMap<>(), groups, index, new AtomicInteger(0), cdataValues));
            index.decrementAndGet();
        } while (index.get() >= 0);
        result.remove(Fields.HAS_CHILDREN);
        return result;
    }

    private static Map<String,Object> parse(Map<String,Object> currentObject, List<String> groups, AtomicInteger index, AtomicInteger lastIndexOf, Map<String,String> cdataValues) {
        String currentTag = groups.get(index.get());
        if(currentTag.startsWith(Strings.SLASH)) {
            //In this case, we found the closing tag
            if(!currentObject.containsKey(Fields.HAS_CHILDREN)) {
                Integer currentIndex = index.get();
                String previousTag = groups.get(index.get()-1);
//                if (previousTag.)
                currentObject.put(Fields.VALUE, getValue(groups,currentIndex, currentIndex+1, lastIndexOf, cdataValues));
            } else {
                currentObject.remove(Fields.HAS_CHILDREN);
            }
            currentObject.put(Fields.CLOSED, true);
        } else if(currentTag.endsWith(Strings.SLASH)) {
            // In this case we found the inline-tag
            currentTag = currentTag.replace(Strings.SLASH, Strings.EMPTY_STRING);
            String tagName = getName(currentTag);
            Map<String, Object> child = getAttributes(currentTag);
            currentObject = addChild(currentObject, tagName, child);
        } else if(currentTag.startsWith(Strings.QUESTION)) {
            index.decrementAndGet();
            currentObject = parse(currentObject, groups, index, lastIndexOf, cdataValues);
        } else {
            // In this case we found the opening tag
            String tagName = getName(currentTag);
            Map<String, Object> child = getAttributes(currentTag);
            do {
                index.decrementAndGet();
                child = parse(child, groups, index, lastIndexOf, cdataValues);
            } while (!child.containsKey(Fields.CLOSED));
            child.remove(Fields.CLOSED);

            currentObject = addChild(currentObject, tagName, child);
            currentObject.put(Fields.HAS_CHILDREN, true);

        }
        return currentObject;
    }

    private static Map<String,Object> addChild(Map<String,Object> currentObject, String tagName, Map<String,Object> child) {
        Object childToAdd = child;
        if(child.size() == 1 && child.containsKey(Fields.VALUE)) {
            childToAdd = child.get(Fields.VALUE);
        }
        if(currentObject.containsKey(tagName)) {
            Object value = currentObject.get(tagName);
            if(value instanceof List) {
                ((List)value).add(childToAdd);
            } else {
                List<Object> list = new ArrayList<>();
                list.add(value);
                list.add(childToAdd);
                currentObject.put(tagName, list);
            }
        } else {
            currentObject.put(tagName, childToAdd);
        }
        return currentObject;
    }

    private static String getName(String tag) {
        String[] tagParts = tag.split(Strings.WHITE_SPACE);
        return tagParts[0].trim();
    }

    private static Map<String,Object> getAttributes(String tag) {
        Matcher matcher = Patterns.TAG_ATTRIBUTES_PATTERN.matcher(tag);
        Map<String,Object> result = new HashMap<>();
        int start = 0;
        while(matcher.find(start)) {
            String attribute = matcher.group(Patterns.ATTRIBUTE_GROUP_NAME).trim();
            String[] keyValue = attribute.split(Strings.ASSIGNATION);
            if(keyValue.length == 1) {
                result.put(keyValue[0], null);
            } else {
                result.put(keyValue[0], Strings.deductInstance(keyValue[1].substring(1, keyValue[1].length() - 1)));
            }
            start = matcher.end();
        }
        return result;
    }

    private static Object getValue(List<String> groups, int startIndex, int endIndex, AtomicInteger lastIndexOf, Map<String,String> cdataValues) {
//        String startToken = String.format(Fields.TOKEN_FORMAT, startIndex);
//        String endToken = String.format(Fields.TOKEN_FORMAT, endIndex);
//        String lastGroup = groups.get(groups.size()-1);
//        String result = lastGroup.substring(lastGroup.indexOf(startToken, lastIndexOf.get()) + startToken.length(), lastGroup.indexOf(endToken, lastIndexOf.get())).trim();
//        lastIndexOf.set(lastGroup.indexOf(endToken, lastIndexOf.get()) + endToken.length());
//        if(cdataValues.containsKey(result)) {
//            result = cdataValues.get(result);
//        }
//        return Strings.deductInstance(result.trim());
        return null;
    }

    public static String toXml(Map<String,Object> root) {
        String rootKey = root.keySet().stream().findFirst().get();
        Map<String,Object> body = Introspection.resolve(root, rootKey);
        return toXml(rootKey, body, 0);
    }

    private static String toXml(String tagKey, Map<String,Object> tag, int tabSize) {
        Strings.Builder result = new Strings.Builder();
        StringBuilder attributes = new StringBuilder();
        StringBuilder subTags = new StringBuilder();
        Object innerTagValue = null;
        for(String key : tag.keySet()) {
            Object value = tag.get(key);
            if (key.equals(Fields.VALUE)) {
                innerTagValue = Introspection.resolve(tag, Fields.VALUE);
            } else if (value instanceof Map) {
                subTags.append(toXml(key, (Map<String,Object>) value, tabSize + 1));
            } else if (value instanceof Collection) {
                for (Object valueIntoCollection : (Collection)value) {
                    if (valueIntoCollection instanceof Map) {
                        subTags.append(toXml(key, (Map<String, Object>)valueIntoCollection, tabSize + 1));
                    }
                }
            } else {
                attributes.append(String.format(Patterns.ATTRIBUTE, key, value.toString()));
            }
        }

        if (subTags.length() == 0) {
            if (innerTagValue != null) {
                Collection<Object> values;
                if (innerTagValue instanceof Collection) {
                    values = (Collection<Object>) innerTagValue;
                } else {
                    values = new ArrayList<>();
                    values.add(innerTagValue);
                }
                for (Object obj : values) {
                    result.append("\t".repeat(Math.max(0, tabSize)));
                    result.append(String.format(Patterns.OPEN_TAG, tagKey, attributes));
                    result.append(obj);
                    result.append(String.format(Patterns.CLOSE_TAG, tagKey), "\r\n");
                }
                result.cleanBuffer();
            } else {
                result.append("\t".repeat(Math.max(0, tabSize)));
                result.append(String.format(Patterns.INLINE_TAG, tagKey, attributes));
            }
        } else {
            result.append("\t".repeat(Math.max(0, tabSize)));
            result.append(String.format(Patterns.OPEN_TAG, tagKey, attributes));
            result.append("\r\n");
            result.append(subTags);
            result.append("\t".repeat(Math.max(0, tabSize)));
            result.append(String.format(Patterns.CLOSE_TAG, tagKey));
        }
        result.append("\r\n");

        return result.toString();
    }
}
