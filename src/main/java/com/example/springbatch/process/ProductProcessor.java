package com.example.springbatch.process;

import com.example.springbatch.model.TranstemplateEntity;
import org.springframework.batch.item.ItemProcessor;

public class ProductProcessor implements ItemProcessor<TranstemplateEntity, TranstemplateEntity>{

	@Override
	public TranstemplateEntity process(TranstemplateEntity item) throws Exception {
//		item.setProdGst(item.getProdCost()*12/100.0);
//		item.setProdDisc(item.getProdCost()*25/100.0);
		return item;
	}
	

}
