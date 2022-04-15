package scripts;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

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

import org.snu.ids.kkma.index.Keyword;
import org.snu.ids.kkma.index.KeywordExtractor;
import org.snu.ids.kkma.index.KeywordList;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class makeKeyword {
	public void convertXml(String path) throws ParserConfigurationException, SAXException, IOException, TransformerException {
		// collection.xml 파일 읽어오기
		File file = new File(path);
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = dbFactory.newDocumentBuilder();
		Document document = docBuilder.parse(file);
		document.getDocumentElement().normalize();
		
		NodeList bodylist = document.getElementsByTagName("body");				
		
		// keyword 추출
		for (int k=0; k < bodylist.getLength(); k++){
			Element body = (Element) bodylist.item(k); 
			String body_content = bodylist.item(k).getTextContent();
			KeywordExtractor ke = new KeywordExtractor();
			KeywordList kl = ke.extractKeyword(body_content, true);

			
			String parsed_kwrd = "";
			for (int i=0; i < kl.size(); i++) {
				Keyword kwrd = kl.get(i);
				parsed_kwrd = parsed_kwrd.concat(kwrd.getString() + ":" + kwrd.getCnt() + "#");
			
			}
			body.setTextContent(parsed_kwrd);;
//			System.out.println(body.getTextContent());
		}
			
		// xml 파일로 저장
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		 
		Transformer transformer = transformerFactory.newTransformer();
		transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
		transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC, "yes");
		
		DOMSource source = new DOMSource(document);
		StreamResult result = new StreamResult(new FileOutputStream(new File("/src/scripts" + "\\index.xml")));
//		StreamResult result = new StreamResult(new FileOutputStream(new File("./index.xml")));

		
		transformer.transform(source, result);
	}
		
		
	}


