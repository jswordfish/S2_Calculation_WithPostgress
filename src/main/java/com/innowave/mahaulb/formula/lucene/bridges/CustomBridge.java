package com.innowave.mahaulb.formula.lucene.bridges;

import java.util.List;

import org.apache.lucene.document.Document;
import org.hibernate.search.bridge.FieldBridge;
import org.hibernate.search.bridge.LuceneOptions;

import com.innowave.mahaulb.formula.domain.Field;

public class CustomBridge  implements FieldBridge {

	@Override
	public void set(String name, Object value, Document doc, LuceneOptions options) {
		// TODO Auto-generated method stub
		List<Field> fields = (List<Field>) value;
		for(Field f : fields){
			options.addFieldToDocument(f.getName().replace(" ", "sep"), f.getValue(), doc);
		}
	}

}
