package dchu.core.services.impl;

import dchu.core.entities.Key;
import dchu.core.entities.P2shAddress;
import dchu.core.entities.Transaction;
import dchu.bitcoin.service.BitcoinMagicService;
import dchu.core.services.KeysRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class P2shAddressesRepositoryImplTest {
    @InjectMocks
    P2shAddressesRepositoryImpl addressesRepository;

    @Mock
    BitcoinMagicService bitcoinMagicService;
    @Mock
    KeysRepository keysRepository;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

//    @Test
//    public void testCreateNew() throws Exception {
//        List<String> keys = new ArrayList<>();
//        keys.add("testKey1");
//        keys.add("testKey2");
//        keys.add("testKey3");
//
//        when(bitcoinMagicService.createMultiSignatureRedeemScript(keys, 2)).thenReturn("redeemScript");
//        when(bitcoinMagicService.getAddressFromRedeemScript("redeemScript")).thenReturn("address");
//        Key testKey3 = new Key();
//        testKey3.setPublicKey("testKey3");
//        testKey3.setPrivateKey("testKey3PrivateKey");
//        when(keysRepository.findByPublicKey("testKey3")).thenReturn(testKey3);
//        P2shAddress address = addressesRepository.createNew(keys, 2);
//        assertThat(address.getRedeemScript(), is("redeemScript"));
//        assertThat(address.getAddress(), is("address"));
//        List<Key> addressKeys = address.getKeys();
//        for (Key addressKey : addressKeys) {
//            assertThat(keys, hasItem(addressKey.getPublicKey()));
//        }
//        assertThat(address.getInvoiceBalance(),is(0L));
//        verify(bitcoinMagicService).watchAddress("address");
//    }
//
//    @Test
//    public void testCreateNewTransaction() throws Exception {
//
//        when(bitcoinMagicService.createTransaction("sourceAddress","targetAddress",1000L)).thenReturn("rawTransaction");
//
//        P2shAddress address = new P2shAddress();
//        address.setAddress("sourceAddress");
//        Transaction transaction = addressesRepository.createNewTransaction(address, "targetAddress", 1000L);
//        assertThat(transaction.getAmount(),is(1000L));
//        assertThat(transaction.getSourceAddress().getAddress(),is(address.getAddress()));
//        assertThat(transaction.getTargetAddress(),is("targetAddress"));
//        assertThat(transaction.getRawTransaction(),is("rawTransaction"));
//
//    }
}
