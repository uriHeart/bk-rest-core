package io.cobla.core.util;

import java.math.BigDecimal;
import java.math.BigInteger;
import lombok.NonNull;

import javax.validation.constraints.NotNull;

public class EthNumberUtil {


    public static String hexToNumber(@NotNull String hexDecimal){

       return new BigInteger(hexDecimal.substring(2),16).toString();
    }

    public static String hexToRealNumber(@NotNull String hexDecimal){

       BigInteger hexValue =  new BigInteger( hexDecimal.substring(2),16 );
       BigDecimal value = new BigDecimal( hexValue,hexValue.compareTo(BigInteger.ZERO)==0 ? 0 : 18);

       return value.toString();
    }

    public static String hexToGasPrice(@NotNull String hexDecimal){

        BigInteger hexValue =  new BigInteger( hexDecimal.substring(2),16 );
        String value = new BigDecimal( hexValue,hexValue.compareTo(BigInteger.ZERO)==0 ? 0 : 18).toPlainString();

        int endIndex=0;

        for(int i=1; i < value.length(); i++){
             endIndex = value.length() - (i-1);
            if(!value.substring(value.length()-i,endIndex).equals("0")){
                break;
            }
        }

        return value.substring(0,endIndex);
    }

    public static String hexToGasNumber(@NotNull String hexDecimal){

        BigInteger hexValue =  new BigInteger( hexDecimal.substring(2),16 );

        return new BigDecimal( hexValue,hexValue.compareTo(BigInteger.ZERO)==0 ? 0 : 10).toString();
    }
}
