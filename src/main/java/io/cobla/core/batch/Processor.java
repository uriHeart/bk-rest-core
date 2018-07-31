package io.cobla.core.batch;

import io.cobla.core.dto.ApiWalletTransactionReqDto;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("ApiWalletProcessor")
public class Processor implements ItemProcessor<List<ApiWalletTransactionReqDto>,ApiWalletTransactionReqDto> {


    @Override
    public ApiWalletTransactionReqDto process(List<ApiWalletTransactionReqDto> item) throws Exception {
        return null;
    }
}
