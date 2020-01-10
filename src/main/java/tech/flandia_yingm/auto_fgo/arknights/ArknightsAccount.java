package tech.flandia_yingm.auto_fgo.arknights;

import lombok.NonNull;
import lombok.Value;

@Value
public class ArknightsAccount {

    @NonNull
    private final String username, password;

}
