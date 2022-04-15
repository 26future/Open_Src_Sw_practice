package scripts;

import java.io.ByteArrayInputStream;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StringWriter;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class indexer {

	public void getIndex(String path) throws SAXException, IOException, ParserConfigurationException, TransformerException, ClassNotFoundException{
		// index.xml 파일 읽기
		File file = new File(path);
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = dbFactory.newDocumentBuilder();
		Document document = docBuilder.parse(file);
		document.getDocumentElement().normalize();
		
		// 해시맵 만들기 (key:단어, value:각 문서에서의 등장횟수)
		HashMap<String, ArrayList> wordMap = new HashMap<String, ArrayList>();
		ArrayList<Double> zero = new ArrayList<Double>(Arrays.asList(0.0, 0.0, 0.0, 0.0, 0.0));  // 0.0으로 초기화된 list
		
		
		for(int i = 0; i < document.getElementsByTagName("body").getLength(); i++) {
			String body = document.getElementsByTagName("body").item(i).getTextContent();
//			System.out.println(body.split("#")[0]);  // 각 doc의 0번째 단어
			String[] pairsOfWordCount = body.split("#");
			
			
			for(String pair : pairsOfWordCount) {
				ArrayList<Double> tmp;
				String word = pair.split(":")[0];
				double tf = Double.parseDouble(pair.split(":")[1]);  // 해당 단어의 등장 횟수

				if (wordMap.containsKey(word))  // map에 이미 추가된 단어 -> 이전 리스트 가져오기
					tmp = (ArrayList<Double>) wordMap.get(word).clone(); 

				else   // map에 없는 새로운 단어 -> 초기값을 가져옴
					tmp = (ArrayList<Double>) zero.clone();  
				
				tmp.set(i, tf);  // weight 리스트의 i번째에 tf 저장
				wordMap.put(word, tmp);
				}
			}
//		System.out.println(wordMap.get("라면"));
		
		// TF-IDF 구해서 해시맵 업데이트(등장횟수 -> IF-IDF)
		int numberOfDoc = document.getElementsByTagName("doc").getLength();
		for(String word : wordMap.keySet()) {
			ArrayList<Double> tmp = (ArrayList<Double>)wordMap.get(word).clone();
			double df = (double)(numberOfDoc - Collections.frequency(tmp, 0.0));  //해당 단어가 몇 개의 문서에서 등장하는지
			
			for(int i = 0; i < numberOfDoc; i++) {
				if(tmp.get(i) == 0)
					continue;
				double weight = tmp.get(i) * Math.log((double)numberOfDoc / df);  // TF-IDF
				tmp.set(i, Math.round(weight*100)/100.0);
			wordMap.replace(word, tmp);
			}
		}
		
		// 직렬화하기 위해 value 형식을 변경하여 저장 (Double->String)
		HashMap<String, String> post = new HashMap<String, String>();
		
		for (String word : wordMap.keySet()) {
			StringBuilder sb = new StringBuilder();
			ArrayList<Double> tmp = wordMap.get(word);
			for (int i = 0; i < numberOfDoc; i++) {
				sb.append(i).append(" ");
				sb.append(tmp.get(i)).append(" ");
			}
			post.put(word, sb.toString().strip());
		}
		
		
		// 직렬화(객체를 문자열로)된 해시맵 저장
		FileOutputStream fos = new FileOutputStream("./index.post");
		ObjectOutputStream oos = new ObjectOutputStream(fos);
		oos.writeObject(post);
		oos.close();
		
		// 역직렬화(문자열을 객체로)된 해시맵 불러오기
		File result_file = new File("./index.post");
		FileInputStream fis = new FileInputStream(result_file);
		ObjectInputStream ois = new ObjectInputStream(fis);
		Object data = ois.readObject();
		ois.close();
		
		// 역직렬화된 hashmap 출력
		HashMap<String, String> hashmap = (HashMap<String, String>) data;
		Iterator<String> it = hashmap.keySet().iterator();
		
		while(it.hasNext()) {
			String key = it.next();
			String value = String.valueOf(hashmap.get(key));
					hashmap.get(key);
			System.out.println(key + " → " + value);
		}
	}
}
		
	

