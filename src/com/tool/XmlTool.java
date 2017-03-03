package com.tool;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

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
		Document doc;
		XPath xpath;
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setValidating(false);
		DocumentBuilder db = dbf.newDocumentBuilder();
		doc = db.parse(new ByteArrayInputStream(xmlText.getBytes()));

		// 创建XPath对象
		XPathFactory factory = XPathFactory.newInstance();
		xpath = factory.newXPath();

		Node node = (Node) xpath.evaluate("/uri/url[@type=\"padweb\"]", doc,
				XPathConstants.NODE);

		if (node == null) {
			String exception = "url parse failed:" + xmlText;
			Log.v(TAG, exception);
			throw new Exception(exception);
		}
		Log.v(TAG, node.getTextContent());

		return node.getTextContent();
	}


	/*
	 * sample xml text <objs> <obj name="padweb_html"> <latest ver="1.1.0(demo)"
	 * require_apk="1.0.0">
	 * http://www.langjingyuan.com/padweb/Demo.v1.1.0(demo).zip </latest> <old
	 * ver="1.1.0(demo)" require_apk="1.0.0">
	 * http://www.langjingyuan.com/padweb/Demo.v1.1.0(demo).zip </old> <old
	 * ver="1.0.0"
	 * require_apk="1.0.0">http://www.langjingyuan.com/padweb/Demo.zip</old>
	 * </obj> <obj name="padweb_apk"> <latest ver="1.0.1">
	 * http://www.langjingyuan.com/padweb/padweb_apk_v1.0.1.zip </latest> <old
	 * ver="1.0.1"> http://www.langjingyuan.com/padweb/padweb_apk_v1.0.1.zip
	 * </old> <old ver="1.0.0">
	 * http://www.langjingyuan.com/padweb/padweb_apk_v1.0.0.zip </old> </obj>
	 * </objs>
	 */
	public static UpdateInfo readUpdateInfo(String xmlText) throws Exception {
		UpdateInfo updateInfo = new UpdateInfo();
		Document doc;
		XPath xpath;
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setValidating(false);
		DocumentBuilder db = dbf.newDocumentBuilder();
		doc = db.parse(new ByteArrayInputStream(xmlText.getBytes()));

		// 创建XPath对象
		XPathFactory factory = XPathFactory.newInstance();
		xpath = factory.newXPath();

		NodeList nodeList = (NodeList) xpath.evaluate(
				"/objs/obj[@name=\"padweb_html\"]/latest|/objs/obj[@name=\"padweb_html\"]/old", doc,
				XPathConstants.NODESET);
		if (nodeList.getLength() == 0) {
			String exception = "url parse failed:" + xmlText;
			Log.v(TAG, exception);
			throw new Exception(exception);
		}
		

		for (int i = 0; i < nodeList.getLength(); i++) {
			updateInfo
					.addNewHtmlUpdateInfo((((Element) (nodeList.item(i)))
							.getAttribute("ver")), nodeList.item(i)
							.getTextContent(), (((Element) (nodeList.item(i)))
							.getAttribute("require_apk")));

		}

		nodeList = (NodeList) xpath
				.evaluate(
						"/objs/obj[@name=\"padweb_apk\"]/latest|/objs/obj[@name=\"padweb_apk\"]/old",
						doc, XPathConstants.NODESET);
		if (nodeList.getLength() == 0) {
			String exception = "url parse failed:" + xmlText;
			Log.v(TAG, exception);
			throw new Exception(exception);
		}
		for (int i = 0; i < nodeList.getLength(); i++) {
			updateInfo.addNewApkUpdateInfo(
					(((Element) (nodeList.item(i))).getAttribute("ver")),
					nodeList.item(i).getTextContent());
		}

		return updateInfo;
	}
}
