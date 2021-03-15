package dchu.bitcoin.service.impl;

import org.bitcoinj.params.RegTestParams;
import org.bitcoinj.params.TestNet3Params;
import org.bitcoinj.wallet.Wallet;
import org.bitcoinj.core.*;
import org.bitcoinj.script.Script;
import org.bitcoinj.core.Coin;
import dchu.core.entities.P2shAddress;
import dchu.core.services.P2shAddressesRepository;
import org.bitcoinj.wallet.listeners.WalletCoinsReceivedEventListener;
import org.bitcoinj.wallet.listeners.WalletCoinsSentEventListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Lazy;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.*;
import org.slf4j.*;

/**
 * Created by Jiri on 21. 7. 2014.
 */
@Service
public class BitcoinJEventListener implements WalletCoinsReceivedEventListener, WalletCoinsSentEventListener {

    private static final Logger log = LoggerFactory.getLogger(dchu.bitcoin.service.impl.BitcoinJEventListener.class);

    P2shAddressesRepository addressesRepository;
    private NetworkParameters params;

    @Autowired
    public BitcoinJEventListener(P2shAddressesRepository addressesRepository) {
        this.addressesRepository = addressesRepository;
        //this.params = TestNet3Params.get();
        this.params = TestNet3Params.get();
    }

    @Override
    public void onCoinsReceived(Wallet wallet, Transaction tx, Coin prevBalance, Coin newBalance) {
        List<Address> addressesToUpdate = new ArrayList<>(tx.getOutputs().size());
        for (TransactionOutput output : tx.getOutputs()) {
            Address toAddress = output.getScriptPubKey().getToAddress(params);
            if (wallet.isAddressWatched(toAddress)) {
                addressesToUpdate.add(toAddress);
            }
        }
        updateAddresses(wallet, addressesToUpdate);
    }


    private void updateAddresses(Wallet wallet, List<Address> addressesToUpdate) {
        log.error("updateing address");
        List<TransactionOutput> watchedOutputs = wallet.getWatchedOutputs(true);
        Map<Address, Coin> balances = new HashMap<>();
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
            P2shAddress byAddress = addressesRepository.findByAddress(address.toString());
            log.error("byAddress is " + byAddress.getAddress());
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
            addressesRepository.save(byAddress);
            log.error("byAddress invoice balance is: " + byAddress.getInvoiceBalance());
        }
    }

    @Override
    public void onCoinsSent(Wallet wallet, Transaction tx, Coin prevBalance, Coin newBalance) {

    }


    public void onKeysAdded(List<ECKey> keys) {

    }

}
