package io.cobla.core.batch;

import io.cobla.core.dto.ApiWalletTransactionReqDto;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component("apiWalletReader")
public class ItemReader implements org.springframework.batch.item.ItemReader<List<ApiWalletTransactionReqDto>> {


    @Override
    public List<ApiWalletTransactionReqDto> read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {

        List<ApiWalletTransactionReqDto> testRead = new ArrayList<ApiWalletTransactionReqDto>();
        testRead.add(new ApiWalletTransactionReqDto());


        return null;
    }
}
