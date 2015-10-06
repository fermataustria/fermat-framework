package com.bitdubai.fermat_dap_plugin.layer.digital_asset_transaction.asset_distribution.developer.bitdubai.version_1.structure.events;

import com.bitdubai.fermat_api.layer.all_definition.enums.ServiceStatus;
import com.bitdubai.fermat_api.layer.all_definition.events.interfaces.FermatEventHandler;
import com.bitdubai.fermat_api.layer.all_definition.events.interfaces.FermatEventListener;
import com.bitdubai.fermat_dap_api.layer.all_definition.exceptions.CantSetObjectException;
import com.bitdubai.fermat_dap_api.layer.dap_transaction.AssetTransactionService;
import com.bitdubai.fermat_dap_api.layer.dap_transaction.CantStartServiceException;
import com.bitdubai.fermat_dap_plugin.layer.digital_asset_transaction.asset_distribution.developer.bitdubai.version_1.structure.database.AssetDistributionDao;
import com.bitdubai.fermat_pip_api.layer.pip_platform_service.event_manager.enums.EventType;
import com.bitdubai.fermat_pip_api.layer.pip_platform_service.event_manager.interfaces.DealsWithEvents;
import com.bitdubai.fermat_pip_api.layer.pip_platform_service.event_manager.interfaces.EventManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Manuel Perez (darkpriestrelative@gmail.com) on 05/10/15.
 */
public class AssetDistributionRecorderService implements DealsWithEvents, AssetTransactionService {
    /**
     * DealsWithEvents Interface member variables.
     */
    private EventManager eventManager;
    private List<FermatEventListener> listenersAdded = new ArrayList<>();
    //Asset Issuing database registry
    AssetDistributionDao assetDistributionDao;
    /**
     * TransactionService Interface member variables.
     */
    private ServiceStatus serviceStatus = ServiceStatus.CREATED;

    public AssetDistributionRecorderService(AssetDistributionDao assetDistributionDao) throws CantStartServiceException {
        try {
            setAssetDistributionDao(assetDistributionDao);
        } catch (CantSetObjectException exception) {
            throw new CantStartServiceException(exception, "Cannot set the asset distribution database handler","The database handler is null");
        }
    }

    private void setAssetDistributionDao(AssetDistributionDao assetDistributionDao)throws CantSetObjectException{
        if(assetDistributionDao==null){
            throw new CantSetObjectException("The AssetIssuingDao is null");
        }
        this.assetDistributionDao=assetDistributionDao;
    }

    @Override
    public void setEventManager(EventManager eventManager) {
        this.eventManager = eventManager;
    }

    //TODO: save the proper event here
    /*
    public void incomingAssetOnCryptoNetworkWaitingTransferenceAssetIssuerEvent(IncomingAssetOnCryptoNetworkWaitingTransferenceAssetIssuerEvent event) throws CantSaveEventException {
        //Logger LOG = Logger.getGlobal();
        //LOG.info("EVENT TEST, I GOT AN EVENT:\n"+event);
        this.assetIssuingTransactionDao.saveNewEvent(event.getEventType().getCode(), event.getSource().getCode());
        //LOG.info("CHECK THE DATABASE");
    }
    */

    @Override
    public void start() throws CantStartServiceException {
//TODO: finish this
        //try{
            /**
             * I will initialize the handling of com.bitdubai.platform events.
             */
            FermatEventListener fermatEventListener;
            FermatEventHandler fermatEventHandler;
            //TODO: change for the proper event
            fermatEventListener = eventManager.getNewListener(EventType.INCOMING_ASSET_ON_CRYPTO_NETWORK_WAITING_TRANSFERENCE_ASSET_ISSUER);
            //fermatEventHandler = new IncomingAssetOnCryptoNetworkWaitingTransferenceAssetIssuerEventHandler();
            //((IncomingAssetOnCryptoNetworkWaitingTransferenceAssetIssuerEventHandler) fermatEventHandler).setAssetIssuingRecorderService(this);
            //fermatEventListener.setEventHandler(fermatEventHandler);
            eventManager.addListener(fermatEventListener);
            listenersAdded.add(fermatEventListener);
            //Logger LOG = Logger.getGlobal();
            //LOG.info("ASSET ISSUING EVENT RECORDER STARTED");
            this.serviceStatus=ServiceStatus.STARTED;
        /*} catch (CantSetObjectException exception){
            throw new CantStartServiceException(exception,"Starting the AssetIssuingRecorderService", "The AssetIssuingRecorderService is probably null");
        }*/

    }

    @Override
    public void stop() {
        removeRegisteredListeners();
        this.serviceStatus = ServiceStatus.STOPPED;
    }

    private void removeRegisteredListeners(){
        for (FermatEventListener fermatEventListener : listenersAdded) {
            eventManager.removeListener(fermatEventListener);
        }
        listenersAdded.clear();
    }

    @Override
    public ServiceStatus getStatus() {
        return this.serviceStatus;
    }
}
