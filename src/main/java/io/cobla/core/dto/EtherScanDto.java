package io.cobla.core.dto;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class EtherScanDto {
    String status;
    String message;
    List<ApiWalletTransactionEtherDto> result;
}