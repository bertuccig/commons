package com.sos.xml;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import sos.xml.SOSXMLXPath;

public class SOSXmlCommand {
    private String protocol;
    private String host;
    private Long port;
    private String url;
    private HashMap<String, String> attributes;
    private StringBuilder response;
    private Date surveyDate;

    public SOSXmlCommand(String protocol, String host, Long port) {
        super();
        if (protocol == null || "".equals(protocol)) {
            protocol = "http";
        }

        this.protocol = protocol;
        this.host = host;
        this.port = port;
    }

    public SOSXmlCommand(String url) {
        super();
        this.url = url;
    }

    public SOSXmlCommand(String host, int port) {
        super();
        this.host = host;
        this.port = new Long(port);
    }

    public String getAttribut(String key) {
        return attributes.get(key);
    }

    public Integer getAttributAsIntegerOr0(String key) {
        try {
            return Integer.parseInt(attributes.get(key));
        } catch (Exception e) {
            return 0;
        }
    }

    public void executeXPath(String xPath) throws Exception {
        attributes = new HashMap<String, String>();
        SOSXMLXPath sosxml = new SOSXMLXPath(new StringBuffer(response));
        Node n = sosxml.selectSingleNode(xPath);
        if (n != null) {
            NamedNodeMap map = n.getAttributes();
            for (int j = 0; j < map.getLength(); j++) {
                attributes.put(map.item(j).getNodeName(), map.item(j).getNodeValue());
            }
        }
    }

    public String excutePost(String urlParameters) throws Exception {

        HttpURLConnection connection = null;

        String targetURL;
        if (url != null && !"".equals(url)) {
            targetURL = url;
        } else {
            targetURL = String.format("%s://%s:%s", protocol, host, port);
            ;
        }

        try {
            // Create connection
            URL url = new URL(targetURL);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            connection.setRequestProperty("Content-Length", Integer.toString(urlParameters.getBytes().length));
            connection.setRequestProperty("Content-Language", "en-US");

            connection.setUseCaches(false);
            connection.setDoOutput(true);

            // Send request
            DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
            wr.writeBytes(urlParameters);
            wr.close();

            // Get Response
            InputStream is = connection.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            response = new StringBuilder();

            String line;
            while ((line = rd.readLine()) != null) {
                response.append(line);
                response.append('\r');
            }
            rd.close();

            executeXPath("/spooler/answer");
            try {
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-mm-dd'T'hh:mm:ss.SSS'Z'");
                surveyDate = formatter.parse(getAttribut("time"));
            } catch (Exception e) {
                try {
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss.SSS'Z'");
                    surveyDate = formatter.parse(getAttribut("time"));
                } catch (Exception ee) {
                }

            }

            return response.toString();
        } catch (Exception e) {
            throw e;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    public Date getSurveyDate() {
        return surveyDate;
    }

}