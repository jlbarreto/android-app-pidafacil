package com.pidafacil.pidafacil.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.pidafacil.pidafacil.R;
import com.pidafacil.pidafacil.beans.CardBean;
import com.pidafacil.pidafacil.beans.CashBean;
import com.pidafacil.pidafacil.model.RestaurantRequest;
import com.pidafacil.pidafacil.singleton.UD;
import com.pidafacil.pidafacil.task.VoidTask;
import com.pidafacil.pidafacil.util.Parser;
import com.pidafacil.pidafacil.util.Utils;
import com.pidafacil.pidafacil.util.Validator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import io.realm.Realm;

public class PayMethodActivity extends ActionBarActivity {
    private View buttonOk;
    private TextView txtCardNum;
    private ImageView img_card;
    private Spinner spin_payMethod;
    private Spinner spin_years;
    private Spinner spin_month;
    private Spinner spin_cash;
    private Spinner spin_tmoney;
    private HashMap<String, Integer> paymentMethodsMap = new HashMap<>();
    private float total  = (float) 0.0;
    public static int paymth = 1;
    public static final int CASH = 1;
    public static final int CARD = 2;
    public static final int TGMONEY = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_method);
        Bundle bundle = getIntent().getExtras();
        this.total = bundle.getFloat("total");
        configure();
        setTitle("Forma de pago");
    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            setResult(RESULT_CANCELED);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public void configure(){
        buttonOk = findViewById(R.id.button_ok);
        spin_cash = (Spinner) findViewById(R.id.cash_);
        spin_years = (Spinner) findViewById(R.id.spinner_year);
        spin_month = (Spinner) findViewById(R.id.spinner_month);
        spin_payMethod = (Spinner) findViewById(R.id.spin_pay_mth);
        txtCardNum = (TextView) findViewById(R.id.txt_credit_num);
        spin_tmoney = (Spinner) findViewById(R.id.spinner_tgMoney);

        img_card = (ImageView) findViewById(R.id.img_card__);
        UD.getInstance().setCurrentActivity(this);

        ((TextView) findViewById(R.id.txt_to_pay)).setText("$" + Parser.decimalFormatString(this.total));
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(),R.layout.spinner_custom_item, years());
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_custom_item);//android.R.layout.simple_spinner_dropdown_item);
        spin_years.setAdapter(adapter);
        ArrayAdapter<String> adapter1 = new ArrayAdapter<>(getApplicationContext(),R.layout.spinner_custom_item, getResources().getStringArray(R.array.months_arr));
        adapter1.setDropDownViewResource(R.layout.spinner_dropdown_custom_item);//android.R.layout.simple_spinner_dropdown_item);
        spin_month.setAdapter(adapter1);
        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(getApplicationContext(),R.layout.spinner_custom_item, cash());
        adapter2.setDropDownViewResource(R.layout.spinner_dropdown_custom_item);//android.R.layout.simple_spinner_dropdown_item);
        spin_cash.setAdapter(adapter2);

        ArrayAdapter<String> adapter3 = new ArrayAdapter<>(getApplicationContext(),R.layout.spinner_custom_item, getResources().getStringArray(R.array.billetera_arr));
        adapter3.setDropDownViewResource(R.layout.spinner_dropdown_custom_item);//android.R.layout.simple_spinner_dropdown_item);
        spin_tmoney.setAdapter(adapter3);





        VoidTask task0 = new VoidTask("/restaurant/payment-methods");
        task0.addParam("restaurant_id", String.valueOf(Realm.getInstance(this).where(RestaurantRequest.class).findFirst().getId()));
        task0.setPostExecute(new VoidTask.PostExecute() {
            @Override
            public void execute(StringBuilder response) {
                Log.d("paymentM:WS-RESULTS", response.toString());
                try {
                    JSONObject data = new JSONObject(response.toString());
                    if (data.getBoolean("status")) {
                        JSONArray arr = data.getJSONArray("data");
                        String[] strings = new String[arr.length()];
                        for (int i = 0; i < arr.length(); i++) {
                            paymentMethodsMap.put(arr.getJSONObject(i).getString("payment_method"),
                                    arr.getJSONObject(i).getInt("payment_method_id"));
                            strings[i] = arr.getJSONObject(i).getString("payment_method");
                        }
                        ArrayAdapter<String> adapter0 = new ArrayAdapter<>(getApplicationContext(), R.layout.spinner_custom_item, strings);
                        adapter0.setDropDownViewResource(R.layout.spinner_dropdown_custom_item);
                        spin_payMethod.setAdapter(adapter0);
                    }

                    spin_payMethod.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            LinearLayout lay0 = ((LinearLayout) findViewById(R.id.lay_cash));
                            LinearLayout lay1 = ((LinearLayout) findViewById(R.id.lay_card));//lay_card
                            LinearLayout lay2 = ((LinearLayout) findViewById(R.id.lay_tgMoney));//lay_card
                            String key = (String) spin_payMethod.getItemAtPosition(position);

                            if(paymentMethodsMap.get(key).equals(CARD)){
                                lay0.setVisibility(View.GONE);
                                lay2.setVisibility(View.GONE);
                                lay1.setVisibility(View.VISIBLE);
                                paymth = 2;
                            }

                            if(paymentMethodsMap.get(key).equals(CASH)){
                                lay1.setVisibility(View.GONE);
                                lay2.setVisibility(View.GONE);
                                lay0.setVisibility(View.VISIBLE);
                                paymth = 1;
                            }
                            if(paymentMethodsMap.get(key).equals(TGMONEY)){
                                lay0.setVisibility(View.GONE);
                                lay1.setVisibility(View.GONE);
                                lay2.setVisibility(View.VISIBLE);
                                paymth = 1;
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {}
                    });

                } catch (JSONException e) {
                    Log.d("payment-met:PARSE-ERR", e.getMessage());
                }
            }
        });
        task0.execute();
        buttons();
    }

    void buttons(){
        buttonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v1) {
                View v = findViewById(R.id.content_);
                String key = (String) spin_payMethod.getSelectedItem();
                if(paymentMethodsMap == null)
                    return;
                if (paymentMethodsMap.get(key)!=null && paymentMethodsMap.get(key).equals(CARD)) {
                    Spinner spinmonth = (Spinner) v.findViewById(R.id.spinner_month);
                    String name = ((TextView) v.findViewById(R.id.txt_name)).getText().toString();
                    String num = ((TextView) v.findViewById(R.id.txt_credit_num)).getText().toString();
                    String code = ((TextView) v.findViewById(R.id.txt_code)).getText().toString();
                    String month = spinmonth.getSelectedItem().toString();
                    String year = spin_years.getSelectedItem().toString();
                    String monthPosition = String.valueOf((spinmonth).getSelectedItemPosition()+1);

                    if (name.trim().isEmpty()) {
                        showError(getString(R.string.card_owner_error));
                        return;
                    }

                    if (!Validator.isValid(num)) {
                        showError(getString(R.string.card_number_error));
                        return;
                    }

                    if (!Validator.validSecureCode(code)) {
                        showError(getString(R.string.card_code_error));
                        return;
                    }

                    CardBean bean = new CardBean(name, num, code, month, year, monthPosition);
                    UD.getInstance().put(UD.PAYMTH_DETAILS, bean);
                    setResult(RESULT_OK);
                    finish();
                } else if(paymentMethodsMap.get(key)!=null && !paymentMethodsMap.get(key).equals(CARD)){
                    String value = spin_cash.getSelectedItem().toString();
                    if (!value.isEmpty()) {
                        Float amount = Float.valueOf(value);
                        CashBean cashBean = new CashBean();
                        cashBean.setAmmount(amount);
                        cashBean.setChange(amount - total);
                        UD.getInstance().put(UD.PAYMTH_DETAILS, cashBean);
                        setResult(RESULT_OK);
                        finish();
                    }
                }

            }
        });

        spin_cash.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String val = spin_cash.getItemAtPosition(position).toString();
                float cash = Float.parseFloat(val); cash-=total;
                ((TextView) findViewById(R.id.txt_change)).setText("$"+Parser.decimalFormatString(cash));
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        txtCardNum.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String value = s.toString();
                if(value.matches("^4"))
                    img_card.setImageResource(R.drawable.visa);
                if(value.matches("^5[1-5]"))
                    img_card.setImageResource(R.drawable.mastercard);
                else if(value.matches("^5"))
                    img_card.setImageResource(R.drawable.dankort);
                if(value.matches("^3[4|7]"))
                    img_card.setImageResource(R.drawable.amex);
                if(value.matches("^30[0|5]") || value.matches("^[36|37]"))
                    img_card.setImageResource(R.drawable.diners);
                if(value.matches("^6011") || value.matches("^65"))
                    img_card.setImageResource(R.drawable.discover);
                if(value.matches("^2131") || value.matches("^1800") || value.matches("^35"))
                    img_card.setImageResource(R.drawable.jcb);
                if(value.equals(""))
                    img_card.setImageResource(R.drawable.credit);
            }
            @Override
            public void afterTextChanged(Editable s) { }
        });
    }

    void showError(String message) {
        final AlertDialog.Builder alertDialogBuilder =
                new AlertDialog.Builder(PayMethodActivity.this);
        alertDialogBuilder
                .setMessage(message)
                .setCancelable(false)
                .setNegativeButton("Entendido", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialogBuilder.show();
    }

    List<String> years(){
        List<String> years = new ArrayList<String>();
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        for(int i = 0; i < 21; i++){
            years.add(String.valueOf(year+i));
        }
        return years;
    }

    static int values[] = new int[(300/5)];
    static{
        for(int i=0; i<(300/5);i++){
            values[i] = 5 *(i+1);
        }
    }

    List<String> cash(){
        List<String> arr = new ArrayList<>();
        arr.add(Parser.decimalFormatString(total));
        for(int value: values)
            if(value > this.total) arr.add(Parser.decimalFormatString(value));
        return arr;
    }
}