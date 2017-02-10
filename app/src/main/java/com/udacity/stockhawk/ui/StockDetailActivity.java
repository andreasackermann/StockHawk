package com.udacity.stockhawk.ui;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;
import com.udacity.stockhawk.util.Helper;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StockDetailActivity extends AppCompatActivity implements OnChartValueSelectedListener {

    @BindView(R.id.chart) LineChart mLineChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_detail);
        ButterKnife.bind(this);

        mLineChart.setBackgroundColor(Color.WHITE);
        mLineChart.getAxisRight().setEnabled(false);
        mLineChart.setDrawGridBackground(false);
        mLineChart.getXAxis().setValueFormatter(new DateAxisFormatter());
        mLineChart.setDescription(null);
        mLineChart.setOnChartValueSelectedListener(this);

        Intent intent = getIntent();
        String symbol = intent.getStringExtra(MainActivity.KEY_SYMBOL);
        if (symbol !=null) {
            String[] projection = new String[] {Contract.Quote.COLUMN_HISTORY };

            Cursor historyCursor = getContentResolver().query(
                    Contract.Quote.makeUriForStock(symbol),
                    projection,
                    null,
                    null,
                    null);

            if (historyCursor != null && historyCursor.moveToFirst() && historyCursor.getCount()>0) {
                String history = historyCursor.getString(0);
                if (!history.isEmpty()) {
                    List<Entry> vals = new ArrayList<Entry>();
                    String[] rows = history.split("\n");
                    for (int i = rows.length -1 ; i > 0; i--) {
                        String[] cols = rows[i].split(", ");
                        long quoteTimeMillis = Long.valueOf(cols[0]);
                        float quotePrice = Float.valueOf(cols[1]);

                        Entry entry = new Entry(
                                quoteTimeMillis,
                                quotePrice);
                        vals.add(entry);
                    }
                    LineDataSet lineDataSet = new LineDataSet(vals, getResources().getString(R.string.legend_chart, symbol));
                    lineDataSet.setColor(Color.BLACK);
                    float max = lineDataSet.getYMax();
                    float min = lineDataSet.getYMin();

                    mLineChart.setData(new LineData(lineDataSet));
                    mLineChart.setContentDescription(getResources().getString(R.string.legend_chart_talkback, symbol, Helper.formatDollar(max), Helper.formatDollar(min) ));
                    mLineChart.invalidate();
                }
                historyCursor.close();
            }


        }
    }


    private class DateAxisFormatter implements IAxisValueFormatter {

        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            return Helper.formatDate(value);
        }
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {
        Toast.makeText(this, getResources().getString(R.string.toast_quote_details, Helper.formatDate(e.getX()), Helper.formatDollar(e.getY())), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected() {

    }
}
