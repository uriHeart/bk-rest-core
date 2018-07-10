package io.cobla.core.dto;

import io.cobla.core.domain.ApiWallet;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Optional;

@Setter
public class WalletListReturnDto {
    ArrayList<WalletReturnDto> result;
}
