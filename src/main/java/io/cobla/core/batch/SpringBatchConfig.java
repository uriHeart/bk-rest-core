package io.cobla.core.batch;

 import io.cobla.core.domain.ApiWalletTransactionEther;
 import io.cobla.core.dto.ApiWalletTransactionReqDto;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
 import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
 import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
 import org.springframework.beans.factory.annotation.Autowired;
 import org.springframework.beans.factory.annotation.Qualifier;
 import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
 import org.springframework.context.annotation.Import;


@Configuration
@EnableBatchProcessing
 public class SpringBatchConfig {

    @Autowired
    JobBuilderFactory jb;

    @Autowired
    StepBuilderFactory sb;

    @Autowired
    @Qualifier("ApiWalletProcessor")
    ItemProcessor itemProcessor;

    @Autowired
    @Qualifier("apiWalletReader")
    ItemReader itemReader;


//    @Bean
//    public Job job(JobBuilderFactory jobBuilderFactory,
//                   StepBuilderFactory stepBuilderFactory
//                    ){
//
//                 Step step = stepBuilderFactory.get("ETL-file-load")
//                .chunk(1)
//                .reader(itemReader)
//                .processor(itemProcessor)
//                .build()
//                ;
//
//        return   jb.get("ETL-Load")
//                .incrementer(new RunIdIncrementer())
//                .start(step).build();
//    }

}
