package com.bitdubai.reference_wallet.crypto_broker_wallet.fragments.home;


import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bitdubai.fermat_android_api.layer.definition.wallet.AbstractFermatFragment;
import com.bitdubai.fermat_api.layer.world.interfaces.Currency;
import com.bitdubai.fermat_cbp_api.layer.wallet.crypto_broker.interfaces.CryptoBrokerStockTransaction;
import com.bitdubai.reference_wallet.crypto_broker_wallet.R;
import com.bitdubai.reference_wallet.crypto_broker_wallet.common.models.TestData;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class StockStatisticsFragment extends AbstractFermatFragment {


    private Currency currency;
    private List<CryptoBrokerStockTransaction> stockTransactions;
    private float limitVal = 0.4f * 100;
    private int lastItemPosition;
    private Map<Integer, CryptoBrokerStockTransaction> map = new HashMap<>();
    private TextView startIndicator;
    private TextView endIndicator;


    public static StockStatisticsFragment newInstance() {
        return new StockStatisticsFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View layout = inflater.inflate(R.layout.cbw_fragment_stock_bar_chart, container, false);

        final TextView currencyTextView = (TextView) layout.findViewById(R.id.currency);
        currencyTextView.setText(currency.getCode());

        final TextView currencyBottomTextView = (TextView) layout.findViewById(R.id.currency_bottom);
        currencyBottomTextView.setText(currency.getFriendlyName());

        final TextView currencyBottomValTextView = (TextView) layout.findViewById(R.id.currency_bottom_value);
        CryptoBrokerStockTransaction transaction = stockTransactions.get(stockTransactions.size() - 1);
        currencyBottomValTextView.setText(String.format("%s %s", DecimalFormat.getInstance().format(transaction.getAmount()), currency.getCode()));

        startIndicator = (TextView) layout.findViewById(R.id.start_indicator_text);
        endIndicator = (TextView) layout.findViewById(R.id.end_indicator_text);

        final BarChart barChart = (BarChart) layout.findViewById(R.id.bar_chart);
        barChart.setDescription("");
        barChart.setData(getChartData());
        barChart.setDragEnabled(true);
        barChart.setDrawGridBackground(false);
        barChart.setPinchZoom(false);
        barChart.setVisibleXRangeMaximum(7);
        barChart.getLegend().setEnabled(false);
        barChart.getAxisRight().setEnabled(false);
        barChart.getAxisLeft().setEnabled(false);
        barChart.getAxisLeft().addLimitLine(getLimitLine());
        barChart.getXAxis().setDrawGridLines(false);
        barChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        barChart.getXAxis().setTextSize(16f);
        barChart.getXAxis().setTextColor(Color.parseColor("#2a2f44"));
        barChart.getXAxis().setSpaceBetweenLabels(0);
        barChart.moveViewToX(lastItemPosition - 4);
        barChart.highlightValue(lastItemPosition, 0);
        barChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry entry, int dataSetIndex, Highlight highlight) {

                putDataInIndicators(entry.getXIndex());
            }

            @Override
            public void onNothingSelected() {

            }
        });

        return layout;
    }

    private void putDataInIndicators(int xIndex) {
        CryptoBrokerStockTransaction transaction = map.get(xIndex);

        if (transaction != null) {
            NumberFormat numberFormat = DecimalFormat.getInstance();
            startIndicator.setText(numberFormat.format(transaction.getPreviousAvailableBalance()));
            endIndicator.setText(numberFormat.format(transaction.getRunningAvailableBalance()));
        }
    }

    @NonNull
    private LimitLine getLimitLine() {
        int limitLineColor = Color.parseColor("#36b7c8");

        LimitLine limitLine = new LimitLine(limitVal, String.format("Target: %s %s", limitVal, currency.getCode()));
        limitLine.setLabelPosition(LimitLine.LimitLabelPosition.LEFT_BOTTOM);
        limitLine.setLineWidth(1f);
        limitLine.setLineColor(limitLineColor);
        limitLine.setTextSize(10f);
        limitLine.setTextColor(limitLineColor);

        return limitLine;
    }

    public void bind(StockStatisticsData data) {
        currency = data.getCurrency();
        stockTransactions = data.getStockTransactions();
    }

    private BarData getChartData() {

        List<BarEntry> entries = new ArrayList<>();
        List<String> xVals = new ArrayList<>();
        ArrayList<Integer> colors = new ArrayList<>();

        Calendar calendar = Calendar.getInstance();

        // creando los dias del mes
        for (int i = 1; i <= 30; i++) {
            BarEntry entry = new BarEntry(0, i);
            entries.add(entry);
            xVals.add(String.valueOf(i));
            colors.add(Color.parseColor("#2A2F44"));
        }

        // poniendo los valores en los dias adecuados
        for (CryptoBrokerStockTransaction transaction : stockTransactions) {
            calendar.setTimeInMillis(transaction.getTimestamp());

            lastItemPosition = calendar.get(Calendar.DAY_OF_MONTH);
            float runningAvailableBalance = transaction.getRunningAvailableBalance().floatValue();
            int index = lastItemPosition - 1;

            entries.get(index).setVal(runningAvailableBalance);

            map.put(lastItemPosition, transaction);

            if (runningAvailableBalance > limitVal)
                colors.set(index, Color.parseColor("#FF3E4664"));
        }

        putDataInIndicators(lastItemPosition);

        // configurando el DataSet
        BarDataSet dataSet = new BarDataSet(entries, "stock");
        dataSet.setDrawValues(false);
        dataSet.setColors(colors);
        dataSet.setHighLightColor(Color.WHITE);
        dataSet.setHighLightAlpha(200);
        dataSet.setBarSpacePercent(50);

        BarData barData = new BarData(xVals, dataSet);
        barData.setHighlightEnabled(true);
        return barData;
    }


}
