package org.hcjf.utils.io.net.http;

import org.hcjf.io.net.http.soap.SoapClient;
import org.hcjf.layers.Layers;
import org.hcjf.utils.XmlUtils;
import org.junit.Test;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WSDLTest {

    public static void main(String[] args) {
        Layers.publishLayer(XmlUtils.XMLPrinter.class);
        Layers.publishLayer(XmlUtils.JsonPrinter.class);

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

}
