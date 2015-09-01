package com.bitdubai.fermat_android_api.layer.definition.wallet;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;

import com.bitdubai.fermat_android_api.layer.definition.wallet.interfaces.SubAppsSession;
import com.bitdubai.fermat_android_api.layer.definition.wallet.interfaces.WalletSession;
import com.bitdubai.fermat_android_api.layer.definition.wallet.interfaces.WizardConfiguration;
import com.bitdubai.fermat_api.layer.all_definition.navigation_structure.enums.WizardTypes;
import com.bitdubai.fermat_api.layer.dmp_middleware.wallet_settings.interfaces.SubAppSettings;
import com.bitdubai.fermat_api.layer.dmp_middleware.wallet_settings.interfaces.WalletSettings;
import com.bitdubai.fermat_api.layer.dmp_network_service.wallet_resources.WalletResourcesProviderManager;
import com.bitdubai.fermat_pip_api.layer.pip_network_service.subapp_resources.SubAppResourcesProviderManager;

/**
 * Created by Matias Furszyfer on 2015.26.21..
 */
public class FermatWalletFragment extends Fragment{

    /**
     * FLAGS
     */
    protected boolean isAttached;

    /**
     * Platform
     */
    protected WalletSession walletSession;
    protected WalletSettings walletSettings;
    protected WalletResourcesProviderManager walletResourcesProviderManager;

    /**
     * REFERENCES
     */
    protected WizardConfiguration context;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            context = (WizardConfiguration) getActivity();
        } catch (Exception ex) {
            throw new ClassCastException("cannot convert the current context to FermatActivity");
        }
    }

    protected void startWizard(WizardTypes key) {
        if (context != null && isAttached) {
            context.showWizard(key);
        }
    }

    protected void dismissWizard() {
        if (context != null && isAttached) {
            context.dismissWizard();
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        isAttached = true;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        isAttached = false;
    }

    public void setWalletSession(WalletSession walletSession) {
        this.walletSession = walletSession;
    }

    public void setWalletSettings(WalletSettings walletSettings) {
        this.walletSettings = walletSettings;
    }

    public void setWalletResourcesProviderManager(WalletResourcesProviderManager walletResourcesProviderManager) {
        this.walletResourcesProviderManager = walletResourcesProviderManager;
    }
}
