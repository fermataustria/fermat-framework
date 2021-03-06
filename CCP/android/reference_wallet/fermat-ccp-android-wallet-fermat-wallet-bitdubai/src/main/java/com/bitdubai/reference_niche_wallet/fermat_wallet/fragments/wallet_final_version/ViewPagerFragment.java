package com.bitdubai.reference_niche_wallet.fermat_wallet.fragments.wallet_final_version;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bitdubai.android_fermat_ccp_wallet_fermat.R;
import com.bitdubai.fermat_android_api.layer.definition.wallet.AbstractFermatFragment;
import com.bitdubai.fermat_android_api.layer.definition.wallet.interfaces.ReferenceAppFermatSession;
import com.bitdubai.fermat_android_api.ui.interfaces.FermatWorkerCallBack;
import com.bitdubai.fermat_android_api.ui.util.FermatWorker;
import com.bitdubai.fermat_api.layer.all_definition.common.system.interfaces.ErrorManager;
import com.bitdubai.fermat_api.layer.all_definition.common.system.interfaces.error_manager.enums.UnexpectedWalletExceptionSeverity;
import com.bitdubai.fermat_api.layer.all_definition.enums.CryptoCurrency;
import com.bitdubai.fermat_api.layer.all_definition.enums.FiatCurrency;
import com.bitdubai.fermat_api.layer.all_definition.exceptions.InvalidParameterException;
import com.bitdubai.fermat_api.layer.all_definition.navigation_structure.enums.Wallets;
import com.bitdubai.fermat_api.layer.pip_engine.interfaces.ResourceProviderManager;
import com.bitdubai.fermat_ccp_api.layer.wallet_module.fermat_wallet.interfaces.FermatWallet;
import com.bitdubai.fermat_cer_api.all_definition.interfaces.ExchangeRate;
import com.bitdubai.reference_niche_wallet.fermat_wallet.common.utils.WalletUtils;
import com.bitdubai.reference_niche_wallet.fermat_wallet.fragments.FermatWalletSettings;
import com.bitdubai.reference_niche_wallet.fermat_wallet.session.SessionConstant;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;




/**
 * Created by root on 28/06/16.
 */
public class ViewPagerFragment extends AbstractFermatFragment<ReferenceAppFermatSession<FermatWallet>,ResourceProviderManager> {
    // Store instance variables
    private String providerName;
    private UUID providerId;
    private int page;

    //Managers
    FermatWallet fermatWalletManager;
    FermatWalletSettings fermatWalletSettings;
    ErrorManager errorManager;
    String fiatCurrency;
    private TextView tvLabelRate;
    private ListView lstCurrencyView;
    private static ReferenceAppFermatSession<FermatWallet> fermatSession;
    //newInstance constructor for creating fragment with arguments
    public static ViewPagerFragment newInstance(int page, String providerName,UUID providerId,ReferenceAppFermatSession<FermatWallet> fermatWalletSession ) {
        ViewPagerFragment fragmentFirst = new ViewPagerFragment();
        Bundle args = new Bundle();
        args.putInt("someInt", page);
        args.putString("providerName", providerName);
        args.putString("providerId", String.valueOf(providerId));
        fermatSession = fermatWalletSession;
        fragmentFirst.setArguments(args);
        return fragmentFirst;
    }
    // Store instance variables based on arguments passed
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {

            fermatWalletManager = fermatSession.getModuleManager();
            errorManager = fermatSession.getErrorManager();

            page = getArguments().getInt("someInt", 0);
            providerName = getArguments().getString("providerName");
            providerId = UUID.fromString(getArguments().getString("providerId"));

            if(fermatSession.getData(SessionConstant.FIAT_CURRENCY) != null)
                fiatCurrency = (String) fermatSession.getData(SessionConstant.FIAT_CURRENCY);
            else
                fiatCurrency = FiatCurrency.US_DOLLAR.getCode();

            getAndShowMarketExchangeRateData();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    // Inflate the view for the fragment based on layout XML
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        try {
            View view = inflater.inflate(R.layout.view_pager, container, false);
            tvLabelRate = (TextView) view.findViewById(R.id.txt_rate_amount);
            lstCurrencyView = (ListView) view.findViewById(R.id.currency_list);
            //WheelView wheel = (WheelView) view.findViewById(R.id.wheel_picker);

            List<String> lstCurrencies = new ArrayList<>();
            lstCurrencies.add(FiatCurrency.VENEZUELAN_BOLIVAR.getCode());
            lstCurrencies.add(FiatCurrency.US_DOLLAR.getCode());
            lstCurrencies.add(FiatCurrency.BITCOIN.getCode());

            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                    getActivity(),
                    R.layout.wheel_item,
                    lstCurrencies);

            lstCurrencyView.setAdapter(arrayAdapter);

            lstCurrencyView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    TextView item = (TextView) view.findViewById(R.id.itemText);

                    item.setTextColor(Color.parseColor("#2481BA"));
                    item.setTextSize(16);

                    fiatCurrency = String.valueOf(item.getText());
                    try {
                        fermatSession.setData(SessionConstant.FIAT_CURRENCY, FiatCurrency.getByCode(fiatCurrency).getCode());
                    } catch (InvalidParameterException e) {
                        e.printStackTrace();
                    }
                    getAndShowMarketExchangeRateData();

                }
            });



            return view;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private void getAndShowMarketExchangeRateData() {
        final int MAX_DECIMAL_FOR_RATE = 2;
        final int MIN_DECIMAL_FOR_RATE = 2;
        FermatWorker fermatWorker = new FermatWorker(getActivity()) {
            @Override
            protected Object doInBackground() {
                ExchangeRate rate = null;
                try{
                    rate = fermatWalletManager.getCurrencyExchange(providerId,FiatCurrency.getByCode(fiatCurrency));
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
                return rate;
            }
        };
        fermatWorker.setCallBack(new FermatWorkerCallBack() {
            @Override
            public void onPostExecute(Object... result) {
                if (result != null && result.length > 0) {
                    ExchangeRate rate = (ExchangeRate) result[0];
                    if (rate != null) {
// progressBar.setVisibility(View.GONE);
                        tvLabelRate.setText(String.valueOf(
                                WalletUtils.formatAmountStringWithDecimalEntry(
                                        rate.getPurchasePrice(),
                                        MAX_DECIMAL_FOR_RATE,
                                        MIN_DECIMAL_FOR_RATE)));
                    } else {
//ErrorExchangeRateConnectionDialog dialog_error = new ErrorExchangeRateConnectionDialog(getActivity());
// dialog_error.show();
                        Toast.makeText(getActivity(), "Cant't Get Exhange Rate Info, check your internet connection.", Toast.LENGTH_SHORT).show();
                    }
                } else {
// ErrorExchangeRateConnectionDialog dialog_error = new ErrorExchangeRateConnectionDialog(getActivity());
//dialog_error.show();
                    Toast.makeText(getActivity(), "Cant't Get Exhange Rate Info, check your internet connection.", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onErrorOccurred(Exception ex) {
// progressBar.setVisibility(View.GONE);

                if (errorManager != null)
                    errorManager.reportUnexpectedWalletException(Wallets.CWP_WALLET_RUNTIME_WALLET_BITCOIN_WALLET_ALL_BITDUBAI,
                            UnexpectedWalletExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_FRAGMENT, ex);
                else
                    Log.e("Exchange Rate", ex.getMessage(), ex);
//ErrorExchangeRateConnectionDialog dialog_error = new ErrorExchangeRateConnectionDialog(getActivity());
// dialog_error.show();
            }
        });
        fermatWorker.execute();
    }
}
