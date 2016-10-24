package com.tool;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.util.Log;


public class XmlTool {
	static final String TAG = "XmlTool";
	
	/*
	 * example xml text
	 * 
	 * <uri> <url
	 * type="padweb">http://www.langjingyuan.com/padweb/getversion.php</url>
	 * </uri>
	 */
	public static String readRealUrl(String xmlText) throws Exception {
		InputStream inStream;
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

		inStream = new ByteArrayInputStream(xmlText.getBytes());
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document dom = builder.parse(inStream);
		inStream.close();
		Element root = dom.getDocumentElement();
		NodeList childsNodes = root.getElementsByTagName("url");
		if (childsNodes.getLength() > 0) {
			Node node = (Node) childsNodes.item(0); // 判断是否为元素类型
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element childNode = (Element) node;
				if ("url".equals(childNode.getNodeName())) {
					return childNode.getFirstChild().getNodeValue();
				}
			}
		}

		String exception = "url parse failed:"+ xmlText;
		Log.v(TAG, exception);
		throw new Exception(exception);
	}
/*
 * sample xml text 
	 <objs>
		<obj name="padweb_html">
		<latest ver="1.1.0(demo)" require_apk="1.0.0">
		http://www.langjingyuan.com/padweb/Demo.v1.1.0(demo).zip
		</latest>
		<old ver="1.1.0(demo)" require_apk="1.0.0">
		http://www.langjingyuan.com/padweb/Demo.v1.1.0(demo).zip
		</old>
		<old ver="1.0.0" require_apk="1.0.0">http://www.langjingyuan.com/padweb/Demo.zip</old>
		</obj>
		<obj name="padweb_apk">
		<latest ver="1.0.1">
		http://www.langjingyuan.com/padweb/padweb_apk_v1.0.1.zip
		</latest>
		<old ver="1.0.1">
		http://www.langjingyuan.com/padweb/padweb_apk_v1.0.1.zip
		</old>
		<old ver="1.0.0">
		http://www.langjingyuan.com/padweb/padweb_apk_v1.0.0.zip
		</old>
		</obj>
		</objs>

 * */
	public static UpdateInfo readUpdateInfo(String xmlText) throws Exception {
		InputStream inStream = null;
		UpdateInfo updateInfo = new UpdateInfo();
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

		inStream = new ByteArrayInputStream(xmlText.getBytes());
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document dom = builder.parse(inStream);

		Element root = dom.getDocumentElement();
		NodeList objNodes = root.getElementsByTagName("obj");
		for (int i = 0; i < objNodes.getLength(); i++) {
			Node objNode = (Node) objNodes.item(i); // 判断是否为元素类型
			if (objNode.getNodeType() == Node.ELEMENT_NODE) {
				if ("obj".equals(objNode.getNodeName())) {

					if ("padweb_html".equals(((Element) objNode)
							.getAttribute("name"))) {
						NodeList infoList = objNode.getChildNodes();
						for (int j = 0; j < infoList.getLength(); j++) {
							Node infoNode = infoList.item(j);
							if ((objNode.getNodeType() == Node.ELEMENT_NODE)
									&& ("old".equals(infoNode.getNodeName()) ||
											("latest".equals(infoNode.getNodeName())))) {
								updateInfo.addNewHtmlUpdateInfo(
										((Element) infoNode).getAttribute("ver"),
										infoNode.getFirstChild().getNodeValue(),
										((Element) infoNode).getAttribute("ver"));
							}
						}
					}

					if ("padweb_apk".equals(((Element) objNode).getAttribute("name"))) {
						NodeList infoList = objNode.getChildNodes();
						for (int j = 0; j < infoList.getLength(); j++) {
							Node infoNode = infoList.item(j);
							if ((objNode.getNodeType() == Node.ELEMENT_NODE)
									&& ("old".equals(infoNode.getNodeName()) 
											|| ("latest".equals(infoNode.getNodeName())))) {
								updateInfo.addNewApkUpdateInfo(
										((Element) infoNode).getAttribute("ver"),
										infoNode.getFirstChild().getNodeValue());
							}
						}
					}

				}
			}

		}
		if (inStream != null)
			inStream.close();
		return updateInfo;
	}
}
