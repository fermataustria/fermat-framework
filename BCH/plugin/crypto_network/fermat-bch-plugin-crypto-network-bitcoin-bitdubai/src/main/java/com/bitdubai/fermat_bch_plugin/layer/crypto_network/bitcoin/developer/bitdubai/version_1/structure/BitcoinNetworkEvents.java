package com.bitdubai.fermat_bch_plugin.layer.crypto_network.bitcoin.developer.bitdubai.version_1.structure;

import com.bitdubai.fermat_api.layer.all_definition.enums.CryptoCurrency;
import com.bitdubai.fermat_api.layer.all_definition.events.EventSource;
import com.bitdubai.fermat_api.layer.all_definition.events.interfaces.FermatEvent;
import com.bitdubai.fermat_api.layer.all_definition.money.CryptoAddress;
import com.bitdubai.fermat_api.layer.all_definition.transaction_transference_protocol.ProtocolStatus;
import com.bitdubai.fermat_api.layer.all_definition.transaction_transference_protocol.crypto_transactions.CryptoStatus;
import com.bitdubai.fermat_api.layer.all_definition.transaction_transference_protocol.crypto_transactions.CryptoTransaction;
import com.bitdubai.fermat_api.layer.osa_android.database_system.PluginDatabaseSystem;
import com.bitdubai.fermat_bch_api.layer.crypto_network.enums.TransactionTypes;
import com.bitdubai.fermat_bch_plugin.layer.crypto_network.bitcoin.developer.bitdubai.version_1.database.BitcoinCryptoNetworkDatabaseDao;
import com.bitdubai.fermat_bch_plugin.layer.crypto_network.bitcoin.developer.bitdubai.version_1.exceptions.CantExecuteDatabaseOperationException;
import com.bitdubai.fermat_cry_api.layer.definition.enums.EventType;
import com.bitdubai.fermat_pip_api.layer.pip_platform_service.event_manager.interfaces.EventManager;

import org.bitcoinj.core.Address;
import org.bitcoinj.core.Block;
import org.bitcoinj.core.Coin;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.FilteredBlock;
import org.bitcoinj.core.GetDataMessage;
import org.bitcoinj.core.Message;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.Peer;
import org.bitcoinj.core.PeerAddress;
import org.bitcoinj.core.PeerEventListener;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.core.TransactionBag;
import org.bitcoinj.core.TransactionInput;
import org.bitcoinj.core.TransactionOutput;
import org.bitcoinj.core.Wallet;
import org.bitcoinj.core.WalletEventListener;
import org.bitcoinj.script.Script;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.annotation.Nullable;

/**
 * Created by rodrigo on 10/4/15.
 */
public class BitcoinNetworkEvents implements WalletEventListener, PeerEventListener {
    /**
     * Class variables
     */
    BitcoinCryptoNetworkDatabaseDao dao;

    /**
     * Platform variables
     */
    PluginDatabaseSystem pluginDatabaseSystem;
    UUID pluginId;

    /**
     * Constructor
     * @param pluginDatabaseSystem
     */
    public BitcoinNetworkEvents(PluginDatabaseSystem pluginDatabaseSystem, UUID pluginId) {
        this.pluginDatabaseSystem = pluginDatabaseSystem;
        this.pluginId = pluginId;
    }

    @Override
    public void onPeersDiscovered(Set<PeerAddress> peerAddresses) {

    }

    @Override
    public void onBlocksDownloaded(Peer peer, Block block, FilteredBlock filteredBlock, int blocksLeft) {

    }

    @Override
    public void onChainDownloadStarted(Peer peer, int blocksLeft) {

    }

    @Override
    public void onPeerConnected(Peer peer, int peerCount) {
        System.out.println("peer connected: " + peer.toString());
    }

    @Override
    public void onPeerDisconnected(Peer peer, int peerCount) {

    }

    @Override
    public Message onPreMessageReceived(Peer peer, Message m) {
        return null;
    }

    @Override
    public void onTransaction(Peer peer, Transaction t) {

    }

    @Nullable
    @Override
    public List<Message> getData(Peer peer, GetDataMessage m) {
        return null;
    }

    @Override
    public void onCoinsReceived(Wallet wallet, Transaction tx, Coin prevBalance, Coin newBalance) {
        /**
         * Register the new incoming transaction into the database
         */
        try {
            getDao().saveNewIncomingTransaction(tx.getHashAsString(),
                    getTransactionCryptoStatus(tx),
                    tx.getConfidence().getDepthInBlocks(),
                    getIncomingTransactionAddressTo(wallet, tx),
                    getIncomingTransactionAddressFrom(tx),
                    tx.getValue(wallet).getValue(),
                    tx.getFee().getValue(),
                    ProtocolStatus.TO_BE_NOTIFIED);
        } catch (CantExecuteDatabaseOperationException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCoinsSent(Wallet wallet, Transaction tx, Coin prevBalance, Coin newBalance) {
        /**
         * Register the new outgoing transaction into the database
         */
        try {
            getDao().saveNewOutgoingTransaction(tx.getHashAsString(),
                    getTransactionCryptoStatus(tx),
                    tx.getConfidence().getDepthInBlocks(),
                    getOutgoingTransactionAddressTo(tx),
                    getOutgoingTransactionAddressFrom(wallet, tx),
                    tx.getValue(wallet).getValue(),
                    tx.getFee().getValue(),
                    ProtocolStatus.TO_BE_NOTIFIED);
        } catch (CantExecuteDatabaseOperationException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onReorganize(Wallet wallet) {

    }

    @Override
    public void onTransactionConfidenceChanged(Wallet wallet, Transaction tx) {
        /**
         * Depending this is a outgoing or incoming transaction, I will set the CryptoStatus
         */
        try {
            if (isIncomingTransaction(tx.getHashAsString()))
                setTransactionCryptoStatus(TransactionTypes.INCOMING, wallet, tx);
            else
                setTransactionCryptoStatus(TransactionTypes.OUTGOING, wallet, tx);
        } catch (CantExecuteDatabaseOperationException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onWalletChanged(Wallet wallet) {

    }

    @Override
    public void onScriptsChanged(Wallet wallet, List<Script> scripts, boolean isAddingScripts) {

    }

    @Override
    public void onKeysAdded(List<ECKey> keys) {
        // I may need to reset the wallet in this case?
    }

    /**
     * instantiates the database object
     * @return
     */
    private BitcoinCryptoNetworkDatabaseDao getDao(){
        if (dao == null)
            dao = new BitcoinCryptoNetworkDatabaseDao(this.pluginId, this.pluginDatabaseSystem);
        return dao;
    }

    /**
     * Gets the Crypto Status of the transaction by calculating the transaction depth
     * I need to check if the transaction had another Crypto Status to verify if this is a reversion.
     * Example, if it was under 1 block (ON_BLOCKCHAIN) and now is 0 (ON_CRYPTO_NETWORK), is a Reversion on Blockchain.
     * @param tx
     * @return
     */
    private CryptoStatus getTransactionCryptoStatus(Transaction tx){
        int depth = tx.getConfidence().getDepthInBlocks();

        if (depth == 0)
            return CryptoStatus.ON_CRYPTO_NETWORK;
        else if(depth == 1)
            return CryptoStatus.ON_BLOCKCHAIN;
        else if (depth >= 2)
            return CryptoStatus.IRREVERSIBLE;
        else
            return CryptoStatus.PENDING_SUBMIT;
    }

    /**
     * Extracts the Address To from an Incoming Transaction
     * @param tx
     * @return
     */
    private CryptoAddress getIncomingTransactionAddressTo (Wallet wallet, Transaction tx){
        Address address = null;

        /**
         * I will loop from the outputs that include keys that are in my wallet
         */
        for (TransactionOutput output : tx.getWalletOutputs(wallet)){
            /**
             * get the address from the output
             */
            address = output.getAddressFromP2PKHScript(wallet.getNetworkParameters());
        }
        CryptoAddress cryptoAddress = new CryptoAddress(address.toString(), CryptoCurrency.BITCOIN);
        return cryptoAddress;
    }

    /**
     * Extracts the Address From from an Incoming Transaction
     * @param tx
     * @return
     */
    private CryptoAddress getIncomingTransactionAddressFrom (Transaction tx){
        Address address = null;
        for (TransactionInput input : tx.getInputs()){
            if (input.getFromAddress() != null)
                address = input.getFromAddress();
        }

        CryptoAddress cryptoAddress = new CryptoAddress(address.toString(), CryptoCurrency.BITCOIN);
        return cryptoAddress;
    }

    /**
     * Extracts the Address From from an Outgoing Transaction
     * @param tx
     * @return
     */
    private CryptoAddress getOutgoingTransactionAddressFrom (Wallet wallet, Transaction tx){
        return getIncomingTransactionAddressTo(wallet, tx);
    }

    /**
     * Extracts the Address To from an outgoing Transaction
     * @param tx
     * @return
     */
    private CryptoAddress getOutgoingTransactionAddressTo (Transaction tx){
        return getIncomingTransactionAddressFrom(tx);
    }

    /**
     * determines if the passed transaction is incoming or outgoing transaction
     * @param txHash
     * @return true if is an IncomingTransactin, false if is outgoing.
     */
    private boolean isIncomingTransaction(String txHash) throws CantExecuteDatabaseOperationException {
        return getDao().isIncomingTransaction(txHash);
    }

    /**
     * Creates a new platform transaction with the new crypto Status
     * @param transactionType
     * @param wallet
     * @param tx
     */
    private void setTransactionCryptoStatus(TransactionTypes transactionType , Wallet wallet, Transaction tx) {
        /**
         * I will get the previous CryptoStatus of the transaction to see if it is a Reversion
         */
        CryptoStatus storedCryptoStatus = null;
        try {
            storedCryptoStatus = getStoredTransactionCryptoStatus(transactionType, tx.getHashAsString());
        } catch (CantExecuteDatabaseOperationException e) {
            e.printStackTrace();
        }
        /**
         * Also get the current CryptoStatus that triggered the event.
         */
        CryptoStatus currentCryptoStatus = getTransactionCryptoStatus(tx);
        CryptoStatus cryptoStatusToSet;

        /**
         * if the stored CryptoStatus is the same as the current one (for example IRREVERSIBLE)
         * then there is nothing left to do
         */
        if (storedCryptoStatus == currentCryptoStatus)
            return;

        switch (storedCryptoStatus) {
            case ON_BLOCKCHAIN:
                /**
                 * If it was as ON_BLOCKCHAIN and now is ON_CRYPTO_NETWORK, then Is a reversion to ON CRYPTO_NETWORK
                 */
                if (currentCryptoStatus == CryptoStatus.ON_CRYPTO_NETWORK)
                    cryptoStatusToSet = CryptoStatus.REVERSED_ON_CRYPTO_NETWORK;
                else
                    cryptoStatusToSet = currentCryptoStatus;
                break;
            case IRREVERSIBLE:
                /**
                 * If the transaction was in irreversible and now is something different, is a reversion.
                 */
                if (currentCryptoStatus == CryptoStatus.ON_CRYPTO_NETWORK)
                    cryptoStatusToSet = CryptoStatus.REVERSED_ON_CRYPTO_NETWORK;
                else if (currentCryptoStatus == CryptoStatus.ON_BLOCKCHAIN)
                    cryptoStatusToSet = CryptoStatus.REVERSED_ON_BLOCKCHAIN;
                else
                    cryptoStatusToSet = currentCryptoStatus;
                break;
            default:
                cryptoStatusToSet = currentCryptoStatus;
        }

        /**
         * Now I will set the new transaction on the incoming or outgoing tables
         */
        switch (transactionType) {
            case INCOMING:
                /**
                 * Register the new incoming transaction into the database
                 */
                try {
                    getDao().saveNewIncomingTransaction(tx.getHashAsString(),
                            cryptoStatusToSet,
                            tx.getConfidence().getDepthInBlocks(),
                            getIncomingTransactionAddressTo(wallet, tx),
                            getIncomingTransactionAddressFrom(tx),
                            tx.getValue(wallet).getValue(),
                            tx.getFee().getValue(),
                            ProtocolStatus.TO_BE_NOTIFIED);
                } catch (CantExecuteDatabaseOperationException e) {
                    e.printStackTrace();
                }
                break;
            case OUTGOING:
                /**
                 * Register the new incoming transaction into the database
                 */
                try {
                    getDao().saveNewOutgoingTransaction(tx.getHashAsString(),
                            cryptoStatusToSet,
                            tx.getConfidence().getDepthInBlocks(),
                            getIncomingTransactionAddressTo(wallet, tx),
                            getIncomingTransactionAddressFrom(tx),
                            tx.getValue(wallet).getValue(),
                            tx.getFee().getValue(),
                            ProtocolStatus.TO_BE_NOTIFIED);
                } catch (CantExecuteDatabaseOperationException e) {
                    e.printStackTrace();
                }
                break;

        }
    }

    /**
     * gets the stored crypto status from the specified transaction.
     * @param transactionType
     * @param txHash
     * @return
     * @throws CantExecuteDatabaseOperationException
     */
    private CryptoStatus getStoredTransactionCryptoStatus(TransactionTypes transactionType, String txHash) throws CantExecuteDatabaseOperationException {
        return getDao().getStoredTransactionCryptoStatus(transactionType, txHash);
    }
}

