package dchu.core.services.impl;

import dchu.bitcoin.configuration.WalletConfiguration;
import dchu.core.services.P2shAddressesRepository;
import org.bitcoinj.core.Address;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.TransactionOutput;
import dchu.bitcoin.service.BitcoinMagicService;
import dchu.core.entities.Key;
import dchu.core.entities.P2shAddress;
import dchu.core.entities.Transaction;
import dchu.core.services.KeysRepository;
import dchu.core.services.P2shAddressesRepositoryCustom;
import org.bitcoinj.core.Coin;
import org.bitcoinj.params.RegTestParams;
import org.bitcoinj.params.TestNet3Params;
import org.bitcoinj.wallet.Wallet;
import org.bitcoinj.wallet.listeners.WalletCoinsReceivedEventListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Lazy;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Jiri on 11. 7. 2014.
 */
@Service
public class P2shAddressesRepositoryImpl implements P2shAddressesRepositoryCustom, WalletCoinsReceivedEventListener {
    BitcoinMagicService bitcoinMagicService;
    KeysRepository keysRepository;
    Wallet wallet;

    @Autowired
    public P2shAddressesRepositoryImpl(Wallet wallet,  BitcoinMagicService bitcoinMagicService, KeysRepository keysRepository) {
        this.bitcoinMagicService = bitcoinMagicService;
        this.keysRepository = keysRepository;
        this.wallet = wallet;
    }

    @Override
    public P2shAddress createNew(List<String> publicKeys, Integer requiredKeys) {
        P2shAddress address = new P2shAddress();
        address.setRedeemScript(bitcoinMagicService.createMultiSignatureRedeemScript(publicKeys, requiredKeys));
        address.setAddress(bitcoinMagicService.getAddressFromRedeemScript(address.getRedeemScript()));
        List<Key> keys = new ArrayList<>(publicKeys.size());
        for (String publicKey : publicKeys) {
            Key byPublicKey = keysRepository.findByPublicKey(publicKey);
            if (byPublicKey != null) {
                keys.add(byPublicKey);
            } else {
                Key key = new Key();
                key.setPublicKey(publicKey);
                keys.add(key);
            }

        }
        address.setKeys(keys);
        bitcoinMagicService.watchAddress(address.getAddress());
        wallet.addCoinsReceivedEventListener(this);
        return address;
    }

    @Override
    public Transaction createNewTransaction(P2shAddress address, String targetAddress, Long amount) {
        Transaction transaction = new Transaction();
        transaction.setAmount(amount);
        transaction.setRawTransaction(bitcoinMagicService.createTransaction(address.getAddress(), targetAddress, amount));
        transaction.setTargetAddress(targetAddress);
        transaction.setSourceAddress(address);
        return transaction;
    }

    @Override
    public void onCoinsReceived(Wallet wallet, org.bitcoinj.core.Transaction tx, Coin prevBalance, Coin newBalance) {
        List<Address> addressesToUpdate = new ArrayList<>(tx.getOutputs().size());
        //NetworkParameters params = TestNet3Params.get();
        NetworkParameters params = TestNet3Params.get();
        for (TransactionOutput output : tx.getOutputs()) {
            Address toAddress = output.getScriptPubKey().getToAddress(params);
            if (wallet.isAddressWatched(toAddress)) {
                addressesToUpdate.add(toAddress);
            }
        }
        updateAddresses(wallet, addressesToUpdate);
    }


    private void updateAddresses(Wallet wallet, List<Address> addressesToUpdate) {
        List<TransactionOutput> watchedOutputs = wallet.getWatchedOutputs(true);
        Map<Address, Coin> balances = new HashMap<>();
        // NetworkParameters params = TestNet3Params.get();
        NetworkParameters params = TestNet3Params.get();
        for (TransactionOutput watchedOutput : watchedOutputs) {
            Address toAddress = watchedOutput.getScriptPubKey().getToAddress(params);
            if (addressesToUpdate.contains(toAddress)) {
                Coin coin = balances.get(toAddress);
                if (coin == null) {
                    coin = Coin.ZERO;
                }

                balances.put(toAddress,coin.add(Coin.valueOf(watchedOutput.getValue().longValue())));
            }
        }
        for (Address address : balances.keySet()) {
            P2shAddress byAddress = ((P2shAddressesRepository) this).findByAddress(address.toString());
            //log.error("byAddress is " + byAddress.getAddress());
            byAddress.setInvoiceBalance(byAddress.getInvoiceBalance() - balances.get(address).longValue());
            if (byAddress.getInvoiceBalance() <= 0L) {
                byAddress.setInvoiceStatus(P2shAddress.INVOICE_STATUS.PAID);
            } else {
                byAddress.setInvoiceStatus(P2shAddress.INVOICE_STATUS.PARTIALLY_PAID);
            }
            Long now = System.currentTimeMillis();
            // check for past due
            if (now < byAddress.getDueDate().getTime()) {
                byAddress.setInvoiceStatus(P2shAddress.INVOICE_STATUS.EXPIRED);
            }
            //addressesRepository.save(byAddress);
            ((P2shAddressesRepository) this).save(byAddress);
        }
    }



}
