package practice;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.lang.model.element.Element;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.jsoup.Jsoup;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

public class makeCollection {
//	private static final String Element = null;

	public void makeXml(String path) throws ParserConfigurationException, IOException, TransformerException {
		
		// 파일 리스트 만들기
		File dir = new File(path);
		File[] fileList = dir.listFiles();
		
		// 빈 document 만들기
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		Document document = docBuilder.newDocument();
		document.setXmlStandalone(true);
		
		// docs element를 document에 추가
		var docs = document.createElement("docs");  // docs라는 요소(element)를 만들고 docs에 저장
		document.appendChild(docs);
//		System.out.println(document.getDocumentElement());
		
		int i = 0;
		for (File file : fileList) {
			if (file.isFile()) {
//				System.out.println(file);
				org.jsoup.nodes.Document html = Jsoup.parse(file, "UTF-8");
				String titleData = html.title();
				String bodyData = html.body().text();
//				System.out.println(titleData);
//				System.out.println(bodyData);
				
	
				// 파일 내용을 doc element로 추가
				var doc = document.createElement("doc");
				docs.appendChild(doc);
				doc.setAttribute("id", Integer.toString(i++));
				System.out.println(doc.getAttribute("id"));
				
				var title = document.createElement("title");
				title.appendChild(document.createTextNode(titleData));
				doc.appendChild(title);
				System.out.println(doc.getTextContent());
	
				var body = document.createElement("body");
				body.appendChild(document.createTextNode(bodyData));
				doc.appendChild(body);
				System.out.println(doc.getTextContent());

			}	
		}
	
		// xml파일로 저장
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		
		transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
		transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC, "yes");
		
		DOMSource source = new DOMSource(docs);
		
		File a = new File(path+"\\collection.xml");
		a.setReadable(true);
		a.setWritable(true);
		StreamResult result = new StreamResult(new FileOutputStream(a));
		
		transformer.transform(source, result);
		
	}

}
