package dchu.core.services.impl;

import dchu.core.entities.Key;
import dchu.bitcoin.service.BitcoinMagicService;
import dchu.core.services.KeysRepositoryCustom;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Created by Jiri on 11. 7. 2014.
 */
@Service
public class KeysRepositoryImpl implements KeysRepositoryCustom {
    BitcoinMagicService bitcoinMagicService;

    @Autowired
    public KeysRepositoryImpl(BitcoinMagicService bitcoinMagicService) {
        this.bitcoinMagicService = bitcoinMagicService;
    }

    @Override
    public Key generateNewKey() {
        Key key = new Key();
        Map<String, String> stringStringMap = bitcoinMagicService.generateKeyPair();
        key.setPublicKey(stringStringMap.get(BitcoinMagicService.PUBLIC_KEY));
        key.setPrivateKey(stringStringMap.get(BitcoinMagicService.PRIVATE_KEY));
        return key;
    }
}
