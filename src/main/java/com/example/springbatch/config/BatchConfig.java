package com.example.springbatch.config;

import com.example.springbatch.process.ProductProcessor;
import com.example.springbatch.model.TranstemplateEntity;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.MongoItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.text.ParseException;
import java.text.SimpleDateFormat;

@EnableBatchProcessing
@Configuration
public class BatchConfig {

	@Autowired
	private MongoTemplate template;

	@Bean
	public FlatFileItemReader<TranstemplateEntity> itemReader() {
		FlatFileItemReader<TranstemplateEntity> reader = new FlatFileItemReader<>();
		reader.setResource(new ClassPathResource("rem_transaction_template.csv"));
		reader.setLineMapper(lineMapper());
		reader.setLinesToSkip(1); // Skip the header row in the CSV file
		reader.open(new ExecutionContext());
		return reader;
	}


	public LineMapper<TranstemplateEntity> lineMapper() {
		DefaultLineMapper<TranstemplateEntity> lineMapper = new DefaultLineMapper<>();
		DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer(",");
		tokenizer.setNames(new String[]{"cust_id_sender", "msisdn_sender", "msisdn_receiver", "alias_receiver_name",
				"to_account", "transfer_content", "viettel_bank_code_receiver", "viettel_bank_code_sender", "account_name",
				"transfer_amount", "trans_type_code", "pin_flg", "pin_time", "delete_flg", "create_date", "create_by",
				"update_date", "update_by", "source_infra_id", "cust_id_mobile"}); // Update the field names here

		lineMapper.setLineTokenizer(tokenizer);
		lineMapper.setFieldSetMapper(new BeanWrapperFieldSetMapper<TranstemplateEntity>() {
			@Override
			public TranstemplateEntity mapFieldSet(FieldSet fieldSet) {
				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
				TranstemplateEntity entity = new TranstemplateEntity();
				try {

					entity.setCustomerIdSender(fieldSet.readString("cust_id_sender"));
					entity.setMsisdnSender(fieldSet.readString("msisdn_sender"));
					entity.setMsisdnReceiver(fieldSet.readString("msisdn_receiver"));
					entity.setAliasReceiverName(fieldSet.readString("alias_receiver_name"));
					entity.setToAccount(fieldSet.readString("to_account"));
					entity.setTransferContent(fieldSet.readString("transfer_content"));
					entity.setViettelBankCodeReceiver(fieldSet.readString("viettel_bank_code_receiver"));
					entity.setViettelBankCodeSender(fieldSet.readString("viettel_bank_code_sender"));
					entity.setAccountName(fieldSet.readString("account_name"));
					entity.setTransferAmount(fieldSet.readDouble("transfer_amount"));
					entity.setTransTypeCode(fieldSet.readString("trans_type_code"));
					entity.setPinFlg(fieldSet.readInt("pin_flg"));
					entity.setPinTime(dateFormat.parse(fieldSet.readString("pin_time")));
					entity.setDeleteFlg(fieldSet.readInt("delete_flg"));
					entity.setCreateDate(dateFormat.parse(fieldSet.readString("create_date")));
					entity.setCreateBy(fieldSet.readString("create_by"));
					entity.setUpdateDate(dateFormat.parse(fieldSet.readString("update_date")));
					entity.setUpdateBy(fieldSet.readString("update_by"));
					entity.setSourceInfraId(fieldSet.readString("source_infra_id"));
					entity.setCustomerIdMobile(fieldSet.readString("cust_id_mobile"));
				} catch (ParseException e) {
					// Handle parsing exception
					e.printStackTrace();
				}
				return entity;
			}
		});
		return lineMapper;
	}



	
	//2. Item Processor
	@Bean
	public ItemProcessor<TranstemplateEntity, TranstemplateEntity> processor(){
		ProductProcessor processor= new ProductProcessor();
		return processor;
	}
	
	
	//#. Item Writer
	@Bean
	public ItemWriter<TranstemplateEntity> writer(){
		MongoItemWriter<TranstemplateEntity> writer=new MongoItemWriter<>();
		writer.setTemplate(template);
		writer.setCollection("tanstemplate");
		return writer;
	}
	
	
	
	//STEP
	@Autowired
	private StepBuilderFactory sf;
	
	@Bean
	public Step stepA() {
		return sf.get("stepA")
				.<TranstemplateEntity,TranstemplateEntity>chunk(3)
				.reader(itemReader())
				.processor(processor())
				.writer(writer())
				.build();
	}
	
	
	//JOB
	@Autowired
	private JobBuilderFactory jf;
	@Bean
	public Job jobA() {
		return jf.get("jobA")
				.incrementer(new RunIdIncrementer())
				.start(stepA())
				.build();
	}
	
}
