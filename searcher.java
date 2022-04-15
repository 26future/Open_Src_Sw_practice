package practice;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.snu.ids.kkma.index.Keyword;
import org.snu.ids.kkma.index.KeywordExtractor;
import org.snu.ids.kkma.index.KeywordList;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class searcher {
	public static double InnerProduct(double doc_weight, int query_weight) {
		double inner_product = doc_weight * query_weight;
		return inner_product;
	}
	
	public void calcSim(String path, String query) throws SAXException, IOException, ParserConfigurationException, TransformerException, ClassNotFoundException{
		// hashmap 불러오기
		File file = new File(path);
		FileInputStream fis = new FileInputStream(file);
		ObjectInputStream ois = new ObjectInputStream(fis);
		Object data = ois.readObject();
		ois.close();
		
	
		HashMap<String, String> hashmap = (HashMap<String, String>) data;
		Iterator<String> it = hashmap.keySet().iterator();
			

		// 쿼리 키워드
		KeywordExtractor ke = new KeywordExtractor();
		KeywordList kl = ke.extractKeyword(query, true);
		
		ArrayList kwrd_list = new ArrayList();
		ArrayList cnt_kwrd = new ArrayList();
		String parsed_kwrd = "";
		for (int i = 0; i < kl.size(); i++) {
			Keyword kwrd = kl.get(i);
			
			kwrd_list.add(kwrd.getString());
			cnt_kwrd.add(kwrd.getCnt());}
	
	
		// 문서 파일 가져오기
		File index_file = new File("./index.xml");
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		Document document = docBuilder.parse(index_file);
		NodeList nList = document.getElementsByTagName("doc");
		int num_documents = nList.getLength();
	
		
		// 유사도 리스트		
		ArrayList<ArrayList<Double>> totalValues = new ArrayList();
		for (int i=0; i < kwrd_list.size(); i++){
			String key = String.valueOf(kwrd_list.get(i));
			String value = hashmap.get(key);
			
			List<Double> value_list = new ArrayList();
			for (int j = 0; j < num_documents; j++) {
				double value_double = Double.parseDouble(value.split(" ")[2*j+1]);
				value_list.add(value_double);
				
			}
			totalValues.add((ArrayList<Double>) value_list);
	
		}
		
		// 쿼리와 문서 유사도 계산 (inner product)
//		ArrayList<Double> calcSim = new ArrayList();
//		for (int i = 0; i < num_documents; i++) {
//			ArrayList<Double> temp = new ArrayList();
//			double temp_sim = 0.0;
//			for (int j = 0; j < cnt_kwrd.size(); j++) {
//				double doc_weight = totalValues.get(j).get(i);
//				int query_weight = (int) cnt_kwrd.get(j);
//				double similarity = doc_weight * query_weight;
//				temp_sim += similarity;
//			}
//			calcSim.add(temp_sim);
//		}
		
		// cosine similarity
		ArrayList<Double> calcSim = new ArrayList();
		for (int i = 0; i < num_documents; i++) {
			double temp_sim = 0.0;
			double temp_doc_weight = 0.0;
			double temp_query_weight = 0.0;
			double calc_result = 0.0;
			for (int j = 0; j < cnt_kwrd.size(); j++) {
				double doc_weight = totalValues.get(j).get(i);
				int query_weight = (int) cnt_kwrd.get(j);
//				double similarity = doc_weight * query_weight;
				
				double similarity = InnerProduct(doc_weight, query_weight);
				
				temp_sim += similarity;
				
				double doc_weight_norm = Math.pow(doc_weight, 2);
				double query_weight_norm = Math.pow(query_weight, 2);
				
				temp_doc_weight += doc_weight_norm;
				temp_query_weight += query_weight_norm;
				
			}
			
			calc_result = temp_sim / (Math.sqrt(temp_doc_weight) * Math.sqrt(temp_query_weight));
			if (Double.isNaN(calc_result)){
				calcSim.add(0.0);
			}
			else {
				calcSim.add(calc_result);
			}
		}		
//		System.out.println(calcSim);
			
		
		// 해시맵 형태로 변환 (key: doc index, value: 쿼리와 document의 유사도)
		HashMap<Integer, Double> calcSim_hashmap = new HashMap<Integer, Double>();
		for (int i = 0; i < calcSim.size(); i++) {
			calcSim_hashmap.put(i, calcSim.get(i));
		}
		
		// 해시맵 정렬
		HashMap<Integer, Double> map = calcSim_hashmap;
		List<Entry<Integer, Double>> sorted_calcSim = new ArrayList<>(map.entrySet());
		sorted_calcSim.sort(Collections.reverseOrder(Map.Entry.comparingByValue()));
//		sorted_calcSim.forEach(System.out::println);
		
	
		// 검색 결과 출력
		ArrayList<String> results = new ArrayList();
		for (int i = 0; i < 3; i++) {
			if (sorted_calcSim.get(i).getValue() != 0.0) {
				int doc_idx = sorted_calcSim.get(i).getKey();
				results.add(document.getElementsByTagName("title").item(doc_idx).getTextContent());
			}
		}
		
		int num_results = results.size();
		if (num_results == 0) {
			System.out.println("검색된 결과가 없습니다.");
		}
		else {
			for (int j = 0; j < num_results; j ++)
				System.out.println(results.get(j));
		}
	
}
	private double Math(double doc_weight, int i) {
		// TODO Auto-generated method stub
		return 0;
	}}
	
