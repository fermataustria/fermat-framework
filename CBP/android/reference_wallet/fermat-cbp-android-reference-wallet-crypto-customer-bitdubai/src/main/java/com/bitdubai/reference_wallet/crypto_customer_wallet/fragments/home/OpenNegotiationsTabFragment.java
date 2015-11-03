package com.bitdubai.reference_wallet.crypto_customer_wallet.fragments.home;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.bitdubai.fermat_android_api.ui.enums.FermatRefreshTypes;
import com.bitdubai.fermat_android_api.ui.expandableRecicler.ExpandableRecyclerAdapter;
import com.bitdubai.fermat_android_api.ui.fragments.FermatWalletExpandableListFragment;
import com.bitdubai.fermat_android_api.ui.interfaces.FermatListItemListeners;
import com.bitdubai.fermat_android_api.ui.util.FermatDividerItemDecoration;
import com.bitdubai.fermat_api.layer.all_definition.navigation_structure.enums.Wallets;
import com.bitdubai.fermat_cbp_api.all_definition.enums.NegotiationStatus;
import com.bitdubai.fermat_cbp_api.layer.cbp_wallet_module.common.CustomerBrokerNegotiationInformation;
import com.bitdubai.fermat_cbp_api.layer.cbp_wallet_module.crypto_customer.exceptions.CantGetCryptoCustomerWalletException;
import com.bitdubai.fermat_cbp_api.layer.cbp_wallet_module.crypto_customer.exceptions.CantGetNegotiationsWaitingForBrokerException;
import com.bitdubai.fermat_cbp_api.layer.cbp_wallet_module.crypto_customer.exceptions.CantGetNegotiationsWaitingForCustomerException;
import com.bitdubai.fermat_cbp_api.layer.cbp_wallet_module.crypto_customer.interfaces.CryptoCustomerWallet;
import com.bitdubai.fermat_cbp_api.layer.cbp_wallet_module.crypto_customer.interfaces.CryptoCustomerWalletModuleManager;
import com.bitdubai.fermat_pip_api.layer.platform_service.error_manager.ErrorManager;
import com.bitdubai.fermat_pip_api.layer.platform_service.error_manager.UnexpectedWalletExceptionSeverity;
import com.bitdubai.reference_wallet.crypto_customer_wallet.R;
import com.bitdubai.reference_wallet.crypto_customer_wallet.common.adapters.OpenNegotiationsExpandableAdapter;
import com.bitdubai.reference_wallet.crypto_customer_wallet.common.models.GrouperItem;
import com.bitdubai.reference_wallet.crypto_customer_wallet.common.models.NegotiationInformationTestData;
import com.bitdubai.reference_wallet.crypto_customer_wallet.session.CryptoCustomerWalletSession;
import com.bitdubai.reference_wallet.crypto_customer_wallet.util.CommonLogger;

import java.util.ArrayList;
import java.util.List;


/**
 * Fragment the show the list of open negotiations waiting for the broker and the customer un the Home activity
 *
 * @author Nelson Ramirez
 * @version 1.0
 * @since 20/10/2015
 */
public class OpenNegotiationsTabFragment extends FermatWalletExpandableListFragment<GrouperItem>
        implements FermatListItemListeners<CustomerBrokerNegotiationInformation> {

    // Fermat Managers
    private CryptoCustomerWalletModuleManager moduleManager;
    private ErrorManager errorManager;

    // Data
    private List<GrouperItem> openNegotiationList;
    private CryptoCustomerWallet cryptoCustomerWallet;


    public static OpenNegotiationsTabFragment newInstance() {
        return new OpenNegotiationsTabFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            moduleManager = ((CryptoCustomerWalletSession) walletSession).getModuleManager();
            errorManager = walletSession.getErrorManager();
        } catch (Exception ex) {
            CommonLogger.exception(TAG, ex.getMessage(), ex);
            if (errorManager != null)
                errorManager.reportUnexpectedWalletException(Wallets.CBP_CRYPTO_BROKER_WALLET,
                        UnexpectedWalletExceptionSeverity.DISABLES_THIS_FRAGMENT, ex);
        }

        openNegotiationList = (ArrayList) getMoreDataAsync(FermatRefreshTypes.NEW, 0);
    }

    @Override
    protected void initViews(View layout) {
        super.initViews(layout);

        RecyclerView.ItemDecoration itemDecoration = new FermatDividerItemDecoration(getActivity(), R.drawable.ccw_divider_shape);
        recyclerView.addItemDecoration(itemDecoration);

        if (openNegotiationList.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            View emptyListViewsContainer = layout.findViewById(R.id.empty);
            emptyListViewsContainer.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected boolean hasMenu() {
        return false;
    }

    @Override
    public ExpandableRecyclerAdapter getAdapter() {
        if (adapter == null) {
            adapter = new OpenNegotiationsExpandableAdapter(getActivity(), openNegotiationList);
            // setting up event listeners
            adapter.setChildItemFermatEventListeners(this);
        }
        return adapter;
    }

    @Override
    public RecyclerView.LayoutManager getLayoutManager() {
        if (layoutManager == null)
            layoutManager = new LinearLayoutManager(getActivity());

        return layoutManager;
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.ccw_fragment_open_negotiations_tab;
    }

    @Override
    protected int getRecyclerLayoutId() {
        return R.id.ccw_open_negotiations_recycler_view;
    }

    @Override
    protected int getSwipeRefreshLayoutId() {
        return R.id.swipe_refresh;
    }

    @Override
    public List<GrouperItem> getMoreDataAsync(FermatRefreshTypes refreshType, int pos) {
        ArrayList<GrouperItem> data = new ArrayList<>();
        String grouperText;

        if (moduleManager != null) {
            try {
                cryptoCustomerWallet = moduleManager.getCryptoCustomerWallet("crypto_customer_wallet");



                grouperText = getActivity().getString(R.string.waiting_for_you);
                List<CustomerBrokerNegotiationInformation> waitingForCustomer = new ArrayList<>();
                waitingForCustomer.addAll(cryptoCustomerWallet.getNegotiationsWaitingForCustomer(0, 10));
                GrouperItem<CustomerBrokerNegotiationInformation> waitingForCustomerGrouper = new GrouperItem<>(grouperText, waitingForCustomer, true);
                data.add(waitingForCustomerGrouper);

                grouperText = getActivity().getString(R.string.waiting_for_broker);
                List<CustomerBrokerNegotiationInformation> waitingForBroker = new ArrayList<>();
                waitingForBroker.addAll(cryptoCustomerWallet.getNegotiationsWaitingForBroker(0, 10));
                GrouperItem<CustomerBrokerNegotiationInformation> waitingForBrokerGrouper = new GrouperItem<>(grouperText, waitingForBroker, true);
                data.add(waitingForBrokerGrouper);

            } catch (CantGetCryptoCustomerWalletException | CantGetNegotiationsWaitingForBrokerException | CantGetNegotiationsWaitingForCustomerException ex) {
                CommonLogger.exception(TAG, ex.getMessage(), ex);
                if (errorManager != null) {
                    errorManager.reportUnexpectedWalletException(Wallets.CBP_CRYPTO_BROKER_WALLET,
                            UnexpectedWalletExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_FRAGMENT, ex);
                }
            }

        } else {
            NegotiationInformationTestData child;

            grouperText = getActivity().getString(R.string.waiting_for_you);
            List<CustomerBrokerNegotiationInformation> waitingForCustomer = new ArrayList<>();
            child = new NegotiationInformationTestData("nelsonalfo", "USD", "Crypto Transfer", "BTC", NegotiationStatus.WAITING_FOR_BROKER);
            waitingForCustomer.add(child);
            child = new NegotiationInformationTestData("jorgeegonzalez", "BTC", "Cash in Hand", "USD", NegotiationStatus.WAITING_FOR_BROKER);
            waitingForCustomer.add(child);
            GrouperItem<CustomerBrokerNegotiationInformation> waitingForBrokerGrouper = new GrouperItem<>(grouperText, waitingForCustomer, true);
            data.add(waitingForBrokerGrouper);

            grouperText = getActivity().getString(R.string.waiting_for_broker);
            List<CustomerBrokerNegotiationInformation> waitingForBroker = new ArrayList<>();
            child = new NegotiationInformationTestData("neoperol", "USD", "Cash in Hand", "BsF", NegotiationStatus.WAITING_FOR_BROKER);
            waitingForCustomer.add(child);
            child = new NegotiationInformationTestData("Nelson Orlando", "USD", "Bank Transfer", "BTC", NegotiationStatus.WAITING_FOR_CUSTOMER);
            waitingForBroker.add(child);
            child = new NegotiationInformationTestData("Customer 5", "BsF", "Cash Delivery", "BTC", NegotiationStatus.WAITING_FOR_CUSTOMER);
            waitingForBroker.add(child);
            GrouperItem<CustomerBrokerNegotiationInformation> waitingForCustomerGrouper = new GrouperItem<>(grouperText, waitingForBroker, true);
            data.add(waitingForCustomerGrouper);
        }

        return data;
    }

    @Override
    protected boolean recyclerHasFixedSize() {
        return true;
    }

    @Override
    public void onItemClickListener(CustomerBrokerNegotiationInformation data, int position) {
        //TODO abrir actividad de detalle de contrato abierto
    }

    @Override
    public void onLongItemClickListener(CustomerBrokerNegotiationInformation data, int position) {
    }

    @Override
    public void onPostExecute(Object... result) {
        isRefreshing = false;
        if (isAttached) {
            swipeRefreshLayout.setRefreshing(false);
            if (result != null && result.length > 0) {
                openNegotiationList = (ArrayList) result[0];
                if (adapter != null)
                    adapter.changeDataSet(openNegotiationList);
            }
        }
    }

    @Override
    public void onErrorOccurred(Exception ex) {
        isRefreshing = false;
        if (isAttached) {
            swipeRefreshLayout.setRefreshing(false);
            CommonLogger.exception(TAG, ex.getMessage(), ex);
        }
    }
}
