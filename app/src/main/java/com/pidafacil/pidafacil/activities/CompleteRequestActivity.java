package com.pidafacil.pidafacil.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;

import com.SafeWebServices.PaymentGateway.PGEncrypt;
import com.SafeWebServices.PaymentGateway.PGKeyedCard;
import com.appboy.Appboy;
import com.appboy.models.outgoing.AppboyProperties;
import com.pidafacil.pidafacil.NavigationDrawer;
import com.pidafacil.pidafacil.beans.CardBean;
import com.pidafacil.pidafacil.beans.CashBean;
import com.pidafacil.pidafacil.beans.CategoryBean;
import com.pidafacil.pidafacil.beans.GeneralOptionsBean;
import com.pidafacil.pidafacil.beans.RevenueBean;
import com.pidafacil.pidafacil.beans.RevenueProductBean;
import com.pidafacil.pidafacil.beans.TmoneyBean;
import com.pidafacil.pidafacil.beans.TypeServiceBean;
import com.pidafacil.pidafacil.components.RequestAdapter;
import com.pidafacil.pidafacil.fragments.ExploreFragment;
import com.pidafacil.pidafacil.helper.JsonParser;
import com.pidafacil.pidafacil.helper.WebServiceHelper;
import com.pidafacil.pidafacil.util.Utils;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.*;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.TimePicker;

import com.pidafacil.pidafacil.R;
import com.pidafacil.pidafacil.components.CustomSimpleAdapter;
import com.pidafacil.pidafacil.components.EndDialogMethod;
import com.pidafacil.pidafacil.helper.JsonHelper;
import com.pidafacil.pidafacil.helper.UIHelper;
import com.pidafacil.pidafacil.model.Login;
import com.pidafacil.pidafacil.model.Product;
import com.pidafacil.pidafacil.model.RestaurantRequest;
import com.pidafacil.pidafacil.singleton.UD;
import com.pidafacil.pidafacil.beans.AddressBean;
import com.pidafacil.pidafacil.task.ExecutionMethod;
import com.pidafacil.pidafacil.task.SendRequestTask;
import com.pidafacil.pidafacil.task.VoidActivityTask;
import com.pidafacil.pidafacil.task.VoidTask;
import com.pidafacil.pidafacil.util.Parser;
import com.pidafacil.pidafacil.util.Validator;
import com.pidafacil.pidafacil.fragments.WhatsAppFragment;
import org.json.JSONException;
import org.json.simple.JSONObject;
import org.w3c.dom.Text;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

public class CompleteRequestActivity extends ActionBarActivity implements EndDialogMethod, ExecutionMethod {

    private Integer address;
    private Integer addressId = 0;

    private ImageView cardType;
    private String category;
    private EditText cardNameEdit;
    private EditText cardNumberEdit;
    private EditText cardCodeEdit;
    private EditText customerName;
    private EditText customerPhone;
    private EditText customerPhoneTigo;
    private String id;
    private TextView addrTxt;
    private TextView addrTime;
    private TextView labelEntrega;
    private TextView orderCardCharge;
    private TextView orderTgMoneyCharge;
    private TextView orderChange;
    private TextView orderSubtotal;
    private TextView orderShippingCharge;
    private TextView orderTotal;
    private TextView orderDiscount;
    private TableRow orderDiscountRow;
    private TableRow orderCashRow;
    private TableRow orderChangeRow;
    private TableRow orderShippingChargeRow;
    private TableRow orderCardChargeRow;
    private TableRow orderTgMoneyChargeRow;

    private RadioGroup serviceTypeGroup;
    private RadioGroup paymentMethodGroup;
    private RadioButton serviceTypePickup;
    private RadioButton serviceTypeDelivery;

    private LinearLayout cardPayMethodLayout;
    private LinearLayout tgMoneyPayMethodLayout;
    private LinearLayout lay_entrega;

    private Spinner cashSelect;
    private Spinner cardYear;
    private Spinner cardMonth;
    private Spinner tmoneyBilletera;

    private Button payOrderButton;

    private JSONObject jsonRequest;

    private static TextView txtHourSel;
    public String tarjeta="";

    private Dialog dialogAddress;
    int restaurant_id = 0;

    private Realm r;
    private Login login;
    private Integer zone_id;
    private Integer PAYMENT_METHOD = 1;
    private RevenueBean revenueBean;

    private static String time = "";

    private float change = (float) 0.0;
    private float total = (float) 0.0;
    private float subtotal = (float) 0.0;
    private float shippingPrice = (float) 0.0;

    private float totalAux = (float) 0.0;
    private float totalDisccount = (float) 0.0;
    private float porcDesc = (float) 0.0;

    private int flagDisc = 0;
    private float percentageDisc = (float) 0.0;

    public static int SERVICE_TYPE = 0;
    public static String mex = "";
    public static String tag_id = "";
    public static String tag_name = "";
    public static String section_name = "";
    public static String section_id = "";

    static List<AddressBean> addrbeans = new ArrayList<>();
    static ArrayList<TypeServiceBean> typeServiceBeanList = new ArrayList<>();

    private static final double CARD_CHARGE = 0.0400;
    private static final double TMONEY_CHARGE = 0.0250;
    private UD U = UD.getInstance();

    private static final String TAG = CompleteRequestActivity.class.getSimpleName();
    private Appboy appboy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_complete_request);

        r = Realm.getInstance(this.getApplicationContext());

        login = r.allObjects(Login.class).first();
        appboy = Appboy.getInstance(this);
        configure();

        load();
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

    /**
     * Funcion para calcular costo de envio basado en zona de cobertura
     * de la direccion del usuario
     * @param value
     */
    private void addressShippingCharge(Integer value){
        zone_id = addrbeans.get(value).getIdZone();
        VoidTask task0 = new VoidTask("/order/shipping_charge");
        task0.addParam("restaurant_id", String.valueOf(restaurant_id));
        task0.addParam("zone_id", String.valueOf(zone_id));
        task0.setPostExecute(new VoidTask.PostExecute() {
            @Override
            public void execute(StringBuilder response) {
                try {
                    Log.d("ShiCharge:WS-RESULT", " Charges " + response.toString());
                    org.json.JSONObject data = new org.json.JSONObject(response.toString());
                    if (data.getString("status").equals("true")) {
                        data = data.getJSONObject("data");
                        shippingPrice = Float.parseFloat(data.getString("shipping_charge"));
                        calculateTotal();
                    }
                } catch (JSONException e) {
                    Log.d("ShiCharge:PARSE-ERR", e.getMessage());
                }
            }
        });
        task0.execute();
    }

    /**
     * Peticiones a servidor para obtener datos del restaurante
     */
    private void load() {
        RestaurantRequest restaurant = r.allObjects(RestaurantRequest.class).first();
        restaurant_id = restaurant.getId();

        VoidTask typeServicesTask = new VoidTask("/restaurant/service-types");
        typeServicesTask.addParam("restaurant_id", String.valueOf(restaurant_id));
        typeServicesTask.setPostExecute(new VoidTask.PostExecute() {
            @Override
            public void execute(StringBuilder response) {
                try {
                    Log.d("Respuesta",response.toString());
                    JsonParser parser = new JsonParser();
                    parser.setResponse(response);
                    typeServiceBeanList.clear();
                    typeServiceBeanList.addAll(parser.parseServiceTypes());

                    if (typeServiceBeanList.size() > 0) {
                        if (typeServiceBeanList.get(0) != null) {
                            Integer serviceType = typeServiceBeanList.get(0).getType();
                            if (serviceType == 3 || serviceType == 1) {
                                SERVICE_TYPE = serviceType;
                                serviceTypePickup.setVisibility(View.GONE);
                                serviceTypeDelivery.setChecked(true);
                            } else {
                                serviceTypePickup.setVisibility(View.VISIBLE);
                                serviceTypeDelivery.setVisibility(View.GONE);
                                serviceTypePickup.setChecked(true);
                                SERVICE_TYPE = serviceType;
                            }
                        }

                        if (typeServiceBeanList.size() > 1) {
                            if (typeServiceBeanList.get(1) != null) {
                                serviceTypePickup.setVisibility(View.VISIBLE);
                            } else {
                                serviceTypePickup.setVisibility(View.GONE);
                            }
                        }
                    }

                    configureServiceType(SERVICE_TYPE);
                    calculateTotal();

                } catch (JSONException e) {
                    Log.d("service-types:PARSE-ERR", e.getMessage());
                }
            }
        });
        typeServicesTask.execute();

        id = (String) UD.getInstance().get(UD.GENERAL_CONF_);
        VoidActivityTask task = new VoidActivityTask("/generalOption", CompleteRequestActivity.this, 3) {
            @Override
            public void execute(StringBuilder result) {
                Log.d("INFO", "CONFIGURACIONES GENERALES PF " + result);
                this.resultList = new ArrayList();
                try {
                    GeneralOptionsBean b = new GeneralOptionsBean()
                            .parseObject(new org.json.JSONObject(result.toString()));
                    this.resultList.add(b);
                } catch (JSONException e) { }
            }
        };
        task.addParam("id_conf_general_options", id);
        task.execute();

        String a = String.valueOf(UD.getInstance().get("MEX"));
        if(!a.isEmpty() && a.length()!=0 && a !=null) {Log.d("MEX fuera", a);}





    }

    /**
     * Inicializacion de controles y variables
     */
    public void configure(){

        orderCashRow = (TableRow) findViewById(R.id.orderCashRow);
        orderChangeRow = (TableRow) findViewById(R.id.orderChangeRow);
        orderShippingChargeRow = (TableRow) findViewById(R.id.orderShippingChargeRow);
        orderCardChargeRow = (TableRow) findViewById(R.id.orderCardChargeRow);
        orderTgMoneyChargeRow = (TableRow) findViewById(R.id.orderTgMoneyChargeRow);
        orderDiscountRow = (TableRow) findViewById(R.id.orderDiscountRow);
        orderCardCharge = (TextView) findViewById(R.id.txt_order_card_charge);
        orderTgMoneyCharge = (TextView) findViewById(R.id.txt_order_tgMoney_charge);
        orderChange = (TextView) findViewById(R.id.txt_order_change);
        orderSubtotal = (TextView) findViewById(R.id.txt_order_subtotal);
        orderShippingCharge = (TextView) findViewById(R.id.txt_order_shipping);
        orderDiscount   = (TextView) findViewById(R.id.txt_order_discount);
        orderTotal = (TextView) findViewById(R.id.txt_order_total);
        addrTxt = ((TextView) findViewById(R.id.txt_addr));
        addrTime = ((TextView) findViewById(R.id.txt_tiempo_entrega));
        labelEntrega = ((TextView) findViewById(R.id.label_entrega));

        payOrderButton = (Button) findViewById(R.id.payOrderButton);
        txtHourSel = (TextView) findViewById(R.id.txt_hour_sel);
        paymentMethodGroup = (RadioGroup) findViewById(R.id.rdgPaymentGroup);
        serviceTypeGroup = (RadioGroup) findViewById(R.id.rdgServiceTypeGroup);
        cardType = (ImageView) findViewById(R.id.img_card__);
        cardPayMethodLayout = (LinearLayout) findViewById(R.id.lay_card);
        tgMoneyPayMethodLayout = (LinearLayout) findViewById(R.id.lay_tgMoney);
        lay_entrega = (LinearLayout) findViewById(R.id.lay_entrega);

        cardNameEdit = (EditText) findViewById(R.id.editCardName);
        cardNumberEdit = (EditText) findViewById(R.id.editCardNumber);
        cardCodeEdit = (EditText) findViewById(R.id.editCardCode);
        customerName = (EditText) findViewById(R.id.editCustomerName);
        customerPhone = (EditText) findViewById(R.id.editCustomerPhone);
        customerPhoneTigo = (EditText) findViewById(R.id.txt_tgMoney_num);
        serviceTypePickup = (RadioButton) findViewById(R.id.rdbPickup);
        serviceTypeDelivery = (RadioButton) findViewById(R.id.rdbDelivery);

        cashSelect = (Spinner) findViewById(R.id.spCashSelect);
        cardYear = (Spinner) findViewById(R.id.spinner_year);
        cardMonth = (Spinner) findViewById(R.id.spinner_month);
        tmoneyBilletera = (Spinner) findViewById(R.id.spinner_tgMoney);

        ArrayAdapter<String> yearsAdapter =
                new ArrayAdapter<>(getApplicationContext(),R.layout.spinner_custom_item, years());
        yearsAdapter.setDropDownViewResource(R.layout.spinner_dropdown_custom_item);
        cardYear.setAdapter(yearsAdapter);

        ArrayAdapter<String> monthsAdapter =//getResources().getStringArray(R.array.months_num_arr)
                new ArrayAdapter<>(getApplicationContext(),R.layout.spinner_custom_item,getResources().getStringArray(R.array.months_num_arr) );
        monthsAdapter.setDropDownViewResource(R.layout.spinner_dropdown_custom_item);
        cardMonth.setAdapter(monthsAdapter);

        ArrayAdapter<String> tgmoneyAdapter =
                new ArrayAdapter<>(getApplicationContext(),R.layout.spinner_custom_item, getResources().getStringArray(R.array.billetera_arr));
        tgmoneyAdapter.setDropDownViewResource(R.layout.spinner_dropdown_custom_item);
        tmoneyBilletera.setAdapter(tgmoneyAdapter);



        UD.getInstance().setCurrentActivity(this);

        r = Realm.getInstance(getApplicationContext());

        RestaurantRequest request = r.where(RestaurantRequest.class).findFirst();
        RealmList<Product> l = request.getProducts();

        subtotal = (float) 0.0;

        for (Product p : l) {
            float value = p.getValue();
            int quantity = p.getQuantity();
            subtotal = subtotal + (value * quantity);
        }

        if(r.allObjects(Login.class).size() > 0) {
            login = r.allObjects(Login.class).first();
            if (login.getUserName().isEmpty() || login.getUserName().trim().equals("") || login.getUserName().toString().length()==0 || login.getUserName()==null || login.getUserName().equals("null")) {
                customerName.setText("");
            }else {
                customerName.setText(login.getUserName() + " " + login.getUserLastName());
            }
            if (login.getPhoneNumber().isEmpty() || login.getPhoneNumber().trim().equals("") || login.getPhoneNumber().toString().length()==0 || login.getPhoneNumber()==null || login.getPhoneNumber().equals("null")) {
                customerPhone.setText("");
            }else {
                customerPhone.setText(login.getPhoneNumber());
            }

            if (login.getPhoneNumber().isEmpty() || login.getPhoneNumber().trim().equals("") || login.getPhoneNumber().toString().length()==0 || login.getPhoneNumber()==null || login.getPhoneNumber().equals("null")) {
                customerPhoneTigo.setText("");
            }else {
                customerPhoneTigo.setText(login.getPhoneNumber());
            }



        }

        buttons();

    }

    /**
     * Inicializacion de eventos de controles
     */
    private void buttons(){

        UD.getInstance().put(UD.DIALOG_END_METHOD, this);

        cardNumberEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String value = s.toString();
                if (value.matches("^4"))
                    cardType.setImageResource(R.drawable.visa);
                if (value.matches("^5[1-5]"))
                    cardType.setImageResource(R.drawable.mastercard);
                else if (value.matches("^5"))
                    cardType.setImageResource(R.drawable.dankort);
                if (value.matches("^3[4|7]")){
                    cardType.setImageResource(R.drawable.amex);
                    tarjeta="amex";
                }
                if (value.matches("^30[0|5]") || value.matches("^[36|37]"))
                    cardType.setImageResource(R.drawable.diners);
                if (value.matches("^6011") || value.matches("^65"))
                    cardType.setImageResource(R.drawable.discover);
                if (value.matches("^2131") || value.matches("^1800") || value.matches("^35"))
                    cardType.setImageResource(R.drawable.jcb);
                if (value.equals(""))
                    cardType.setImageResource(R.drawable.credit);
                if(s.length() == 6){ //Validar si tarjeta posee descuento
                    getParamDiscount(value);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        cashSelect.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String val = cashSelect.getItemAtPosition(position).toString();
                float cash = Float.parseFloat(val);
                change = cash - ((float)Math.round(total * 100) / 100);
                orderChange.setText("$ " + Parser.decimalFormatString(change));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        paymentMethodGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                if (checkedId == R.id.rdbPayCash) {
                    // Pago en efectivo
                    PAYMENT_METHOD = 1;
                } else if (checkedId == R.id.rdbPayCard) {
                    // Pago con tarjeta
                    PAYMENT_METHOD = 2;
                } else if (checkedId == R.id.rdbPayTgMoney){
                    //Pago con Tigo Money
                    PAYMENT_METHOD = 3;
                }

                calculateTotal();
            }
        });

        serviceTypeGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                /*
                    Si eligio servicio a domicilio se evalua si es domicilio pidafacil o
                    domicilio de restaurante.
                 */
                if (checkedId == R.id.rdbDelivery) {
                    // Obtener valor de servicio a domicilio
                    SERVICE_TYPE = typeServiceBeanList.get(0).getType();
                } else if (checkedId == R.id.rdbPickup) {
                    // Obtener valor de servicio para llevar
                    if (typeServiceBeanList.size() > 1) {
                        SERVICE_TYPE = typeServiceBeanList.get(1).getType();
                    } else {
                        SERVICE_TYPE = typeServiceBeanList.get(0).getType();
                    }
                }

                configureServiceType(SERVICE_TYPE);

            }
        });

        addrTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (addrbeans == null) {
                    startActivityForResult(new Intent(getApplicationContext(), AddAddressActivity.class), AddAddressActivity.ADD_ADDRESS_REQ_CODE);
                } else if (addrbeans.size() == 0) {
                    startActivityForResult(new Intent(getApplicationContext(), AddAddressActivity.class), AddAddressActivity.ADD_ADDRESS_REQ_CODE);
                }

                dialogAddress = new Dialog(CompleteRequestActivity.this);
                dialogAddress.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialogAddress.setContentView(R.layout.dialog_list_address_custom);
                dialogAddress.findViewById(R.id.button_add_addr).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivityForResult(new Intent(getApplicationContext(), AddAddressActivity.class), AddAddressActivity.ADD_ADDRESS_REQ_CODE);
                    }
                });

                if (SERVICE_TYPE == 2)
                    dialogAddress.findViewById(R.id.button_add_addr).setVisibility(View.GONE);
                else
                    dialogAddress.findViewById(R.id.button_add_addr).setVisibility(View.VISIBLE);

                RecyclerView rec = (RecyclerView) dialogAddress.findViewById(R.id.rec_simple_list_items);
                ArrayList<String> list = new ArrayList<>();

                for (AddressBean bean : addrbeans) {
                    list.add(bean.getName());
                }

                rec.setHasFixedSize(true);
                LinearLayoutManager llm = new LinearLayoutManager(v.getContext());
                llm.setOrientation(LinearLayoutManager.VERTICAL);
                rec.setLayoutManager(llm);
                rec.setAdapter(new CustomSimpleAdapter(list, 2));
                dialogAddress.show();
                UD.getInstance().put(UD.DIALOG_, dialogAddress);
            }
        });

        payOrderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public synchronized void onClick(View v) {

                try {
                    // Obtener datos de restaurante
                    RestaurantRequest request = r.allObjects(RestaurantRequest.class).first();
                    String customer = customerName.getText().toString();
                    String phone = customerPhone.getText().toString();

                    // Obtener datos de pago en efectivo o tarjeta
                    if (PAYMENT_METHOD == 1) {
                        if (customer.isEmpty() || customer.trim().equals("") || customer.toString().length()==0 || customer==null) {
                            errorMessage(getString(R.string.no_customer_name));
                            return;
                        }
                        if (phone.isEmpty() || phone.trim().equals("") || phone.toString().length()==0 || phone==null || phone.equals("null")) {
                            errorMessage(getString(R.string.no_phone_number));
                            return;
                        }
                        if (addressId == 0) {
                            errorMessage(getString(R.string.no_order_address));
                            return;
                        }
                        String cash = cashSelect.getSelectedItem().toString();
                        if (!cash.isEmpty()) {
                            Float amount = Float.valueOf(cash);
                            CashBean cashBean = new CashBean();
                            cashBean.setAmmount(amount);
                            cashBean.setChange(change);
                            UD.getInstance().put(UD.PAYMTH_DETAILS, cashBean);
                        }
                    } else if (PAYMENT_METHOD == 2) {
                        if (customer.isEmpty() || customer.trim().equals("") || customer.toString().length()==0 || customer==null) {
                            errorMessage(getString(R.string.no_customer_name));
                            return;
                        }
                        if (phone.isEmpty() || phone.trim().equals("") || phone.toString().length()==0 || phone==null || phone.equals("null")) {
                            errorMessage(getString(R.string.no_phone_number));
                            return;
                        }
                        if (addressId == 0) {
                            errorMessage(getString(R.string.no_order_address));
                            return;
                        }
                        String name = cardNameEdit.getText().toString();
                        String number = cardNumberEdit.getText().toString();
                        String code = cardCodeEdit.getText().toString();
                        String month = cardMonth.getSelectedItem().toString();
                        String year = cardYear.getSelectedItem().toString();

                        Calendar calendar = Calendar.getInstance();
                        int mesInicio = calendar.get(Calendar.MONTH) + 1;

                        int anioActual = calendar.get(Calendar.YEAR);

                        if (name.trim().isEmpty()) {
                            errorMessage(getString(R.string.card_owner_error));
                            return;
                        }

                        if (number.trim().isEmpty()) {
                            errorMessage(getString(R.string.card_number_error));
                            return;
                        }

                        if (!Validator.validSecureCode(code)) {
                            errorMessage(getString(R.string.card_code_error));
                            return;
                        }

                        year=year.substring(2,4);
                        if(month.matches("^[0-9]")){
                            month="0"+month;
                        }else {month=month;}


                        ////Integrando BAC Payment Gateway
                        PGEncrypt pg;
                        String transInfo;
                        pg = new PGEncrypt();
                        pg.setKey("***2767|MIIERzCCAy+gAwIBAgIBADANBgkqhkiG9w0BAQUFADCBvTELMAkGA1UE" +
                                "BhMCVVMxETAPBgNVBAgMCElsbGlub2lzMRMwEQYDVQQHDApTY2hhdW1idXJnMRgw" +
                                "FgYDVQQKDA9TYWZlV2ViU2VydmljZXMxHjAcBgNVBAsMFUVuZC10by1lbmQgZW5j" +
                                "cnlwdGlvbjEgMB4GA1UEAwwXd3d3LnNhZmV3ZWJzZXJ2aWNlcy5jb20xKjAoBgkq" +
                                "hkiG9w0BCQEWG3N1cHBvcnRAc2FmZXdlYnNlcnZpY2VzLmNvbTAeFw0xNjAyMTYx" +
                                "NDEwNTJaFw0xNjAyMTcxNDEwNTJaMIG9MQswCQYDVQQGEwJVUzERMA8GA1UECAwI" +
                                "SWxsaW5vaXMxEzARBgNVBAcMClNjaGF1bWJ1cmcxGDAWBgNVBAoMD1NhZmVXZWJT" +
                                "ZXJ2aWNlczEeMBwGA1UECwwVRW5kLXRvLWVuZCBlbmNyeXB0aW9uMSAwHgYDVQQD" +
                                "DBd3d3cuc2FmZXdlYnNlcnZpY2VzLmNvbTEqMCgGCSqGSIb3DQEJARYbc3VwcG9y" +
                                "dEBzYWZld2Vic2VydmljZXMuY29tMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIB" +
                                "CgKCAQEAnp3bfVG2dq3fSmutZsSqk3U/0ljvF3kBtHT0WSRp7NeitGRN8GaeVovt" +
                                "SUVNeCMlKmjYSsqAPgcKP8V3dFvA7tqzTMdvucPkJKcqbKNu8Q3NKFFVCqwXJx1M" +
                                "DWnqreolH3mXsEfwD8DR0Nwm5davZTbz/ybOC2TYOVFcZwZqf7VO1JINof3fhVwJ" +
                                "BJTafIFTOORJj4w79S/31H5UZmnvr9qxxK8JVrokpGw/HAwYtDvRWM+t/zJvHyKt" +
                                "opDvVGyRHA57blq1nSd6JE/Qagxik0NHRY+3KoeqHsOm7b0r8G/IU+swgrtEDKGD" +
                                "RcAU4GQQ7JClG72GCSbxO7IZwGrxGwIDAQABo1AwTjAdBgNVHQ4EFgQU20yxL8dA" +
                                "bIIKIgWnhxYd8jJEYUowHwYDVR0jBBgwFoAU20yxL8dAbIIKIgWnhxYd8jJEYUow" +
                                "DAYDVR0TBAUwAwEB/zANBgkqhkiG9w0BAQUFAAOCAQEACQpQyM/AlvxvIdKBzRhE" +
                                "+S9ofJEyb0ERsYEXRt2jPwHKiDGSn5HmSfjuzXsBSHwImjEGAL/b+9PQiQcm6KC1" +
                                "ZQscA7/ar57W0VV7K4xpCJKyRWLUpIw76SlfWZLcC46mTR2+Dfz7k+QrnVTd/PUT" +
                                "TRZAwcU3p0oBKEV03j7MUTzQRV2OzoehMGlD1AbqlkdpVJvMYZGfO8/UL09ChXY8" +
                                "0qG/c4l+EoXJkMPL89LwgzqEAWj6E1AFMoYWMjW9caSJOpn/OwZi1k4bpreqwe7j" +
                                "q7b08Po4YK1JiwO60NzN7T1Ag/VDqAkilk/iwNEWEi7EUPpUT4Rd/hXIMThE+ao7" +
                                "TA==***");

                        PGKeyedCard card = new PGKeyedCard(number, month+year, code);

                        String crypted = pg.encrypt(card, true);
                        if(!mex.isEmpty() && mex.length()!=0 && mex != null) {Log.d("MEX fuera",""+ mex);}
                        //else {mex = "1";}
                        Log.d("Numero de la tarjeta: ",number);


                        //////Fin BAC

                        //bin
                        String bin = number.substring(0,6);

                        if (tarjeta.equals("amex")&& mex.equals("1")){
                            CardBean bean = new CardBean(name, number, code, month, year, month, bin);
                            UD.getInstance().put(UD.PAYMTH_DETAILS, bean);
                        } else {
                            CardBean bean = new CardBean(name, crypted, code, month, year, month, bin);
                            UD.getInstance().put(UD.PAYMTH_DETAILS, bean);
                        }


                    } else if (PAYMENT_METHOD == 3){
                        if (customer.isEmpty() || customer.trim().equals("") || customer.toString().length()==0 || customer==null) {
                            errorMessage(getString(R.string.no_customer_name));
                            return;
                        }
                        if (phone.isEmpty() || phone.trim().equals("") || phone.toString().length()==0 || phone==null || phone.equals("null")) {
                            errorMessage(getString(R.string.no_phone_number));
                            return;
                        }
                        if (addressId == 0) {
                            errorMessage(getString(R.string.no_order_address));
                            return;
                        }
                        String PhoneTigo = customerPhoneTigo.getText().toString();
                        String billetera =  tmoneyBilletera.getSelectedItem().toString();

                        if (PhoneTigo.isEmpty()) {
                            errorMessage(getString(R.string.tgmoney_number_error));
                            return;
                        }


                        TmoneyBean tmoneyBean = new TmoneyBean();
                        tmoneyBean.setNum_tigo_money(PhoneTigo);
                        tmoneyBean.setBilletera_user(billetera);
                        UD.getInstance().put(UD.PAYMTH_DETAILS, tmoneyBean);


                    }

                    if (addressId == 0) {
                        errorMessage(getString(R.string.no_order_address));
                        return;
                    }

                    if (SERVICE_TYPE == 2) {
                        if (time.isEmpty()) {
                            errorMessage(getString(R.string.no_order_time));
                            return;
                        }
                    }

                    // Obtener datos de pago almacenados
                    Object paymentDetails = UD.getInstance().get(UD.PAYMTH_DETAILS);

                    int loginId = login.getId();
                    //  String customer = customerName.getText().toString();
                    //  String phone = customerPhone.getText().toString();

                    if (customer.isEmpty() || customer.trim().equals("") || customer.toString().length()==0 || customer==null) {
                        errorMessage(getString(R.string.no_customer_name));
                        return;
                    }

                    if (phone.isEmpty() || phone.trim().equals("") || phone.toString().length()==0 || phone==null || phone.equals("null")) {
                        errorMessage(getString(R.string.no_phone_number));
                        return;
                    }

                    r.beginTransaction();
                    request.setServiceType(SERVICE_TYPE);
                    request.setPayMethod(PAYMENT_METHOD);
                    request.setAddressId(addressId);
                    r.commitTransaction();

                    jsonRequest = JsonHelper.parseRequest(
                            request,
                            loginId,
                            paymentDetails,
                            time.split(":"),
                            customer,
                            phone);

                    Log.d("COMPRA", jsonRequest.toString());

                    findViewById(R.id._content_).setVisibility(View.GONE);
                    findViewById(R.id.sending_).setVisibility(View.VISIBLE);

                    saveAppboyRevenue(request);
                    sendRequest();

                } catch (Exception ex) {
                    Log.e("ERROR", ex.getMessage());
                }
            }
        });

        txtHourSel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new TimePickerFragment();
                newFragment.show(getSupportFragmentManager(), "timePicker");
            }
        });
    }

    /**
     *   Este metodo configura los valores y controles necesarios para el tipo de servicio
     correspondiente.
     * */
    private void configureServiceType(Integer service) {
        if(service == 2){
            // Tipo de servicio: Para llevar
            showCardControls(5);
            findViewById(R.id.lay_hour_details).setVisibility(View.VISIBLE);
            RestaurantRequest restaurant = r.allObjects(RestaurantRequest.class).first();
            orderShippingChargeRow.setVisibility(View.GONE);
            shippingPrice = (float) 0.0;
            // Obtener las sucursales del restaurante
            restaurant_id = restaurant.getId();

            Log.d("Datos resta:", String.valueOf(restaurant) );
            Log.d("ID rest: ",String.valueOf(restaurant_id));
            VoidActivityTask taskAddressRestaurant = WebServiceHelper.addressRestaurant(CompleteRequestActivity.this, 0);
            taskAddressRestaurant.clearParams();
            taskAddressRestaurant.addParam("restaurant_id", String.valueOf(restaurant_id));
            taskAddressRestaurant.execute();
        } else {
            showCardControls(4);
            // Tipo de servicio: Domicilio o Domicilio PidaFacil
            findViewById(R.id.lay_hour_details).setVisibility(View.GONE);
            // Obtener las direcciones del usuario
            Login login = r.allObjects(Login.class).first();
            VoidActivityTask taskAddressUser = WebServiceHelper.userAddress(CompleteRequestActivity.this, 1);
            taskAddressUser.clearParams();
            taskAddressUser.addParam("user_id", String.valueOf(login.getId()));
            taskAddressUser.execute();
        }

        calculateTotal();
    }

    /**
     *   Este metodo realiza todos los calculos de pago y costo de la orden
     * */
    private void calculateTotal() {
        if (SERVICE_TYPE == 3) {
            // Servicio a domicilio PidaFacil
            orderShippingChargeRow.setVisibility(View.VISIBLE);
            orderShippingCharge.setText("$ " + Parser.decimalFormatString(shippingPrice));
        } else {
            // Para llevar o domicilio de restaurante
            orderShippingChargeRow.setVisibility(View.GONE);
        }

        if (PAYMENT_METHOD == 1) {
            // Pago en efectivo
            showCardControls(1);
            orderChange.setText("$ " + Parser.decimalFormatString(change));
            total = subtotal + shippingPrice;

            ArrayAdapter<String> adapter =
                    new ArrayAdapter<>(getApplicationContext(),R.layout.spinner_custom_item, cash());
            adapter.setDropDownViewResource(R.layout.spinner_dropdown_custom_item);
            cashSelect.setAdapter(adapter);

        } else if(PAYMENT_METHOD == 2){
            // Pago con tarjeta
            showCardControls(2);
            float cardCharge = (float) 0.0000;

            if (SERVICE_TYPE == 3) {
                cardCharge = (float) ((subtotal + shippingPrice) * CARD_CHARGE);
                String sCardCharge = Float.toString(cardCharge);

                String[] dotSub=sCardCharge.split("\\.|,");
                System.out.println("Split: "+dotSub[1]);

                if(dotSub[1].toString().length()<=2){

                    dotSub[1]=dotSub[1]+"000";
                    System.out.println("Cargo con dos decimales; "+  dotSub[1]);
                }

                String[] dotSub2=dotSub[1].toString().split("");


                System.out.println("Dotsun2: " + dotSub2[3]);

                if(dotSub2[3].toString().matches("^[1-9]")){
                    System.out.println("Diferente de Cero");
                    cardCharge=cardCharge+0;
                    System.out.println(sCardCharge);
                    cardCharge=cardCharge+0;
                    DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.US);
                    otherSymbols.setDecimalSeparator('.');
                    otherSymbols.setGroupingSeparator('.');
                    DecimalFormat df = new DecimalFormat("#.##",otherSymbols);
                    df.setRoundingMode(RoundingMode.CEILING);
                    cardCharge = Float.valueOf(df.format(cardCharge));
                    System.out.println(df.format(cardCharge));
                    total = subtotal + shippingPrice + cardCharge;
                }
                else{
                    cardCharge=cardCharge+0;
                    System.out.println(sCardCharge);
                    cardCharge=cardCharge+0;
                    DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.US);
                    otherSymbols.setDecimalSeparator('.');
                    otherSymbols.setGroupingSeparator('.');
                    DecimalFormat df = new DecimalFormat("#.###",otherSymbols);
                    df.setRoundingMode(RoundingMode.CEILING);
                    cardCharge = Float.valueOf(df.format(cardCharge));
                    System.out.println(df.format(cardCharge));
                    total = subtotal + shippingPrice + cardCharge;
                }
                orderCardCharge.setText("$ " + Parser.decimalFormatString(cardCharge));
            } else {
                cardCharge = (float) ((subtotal + shippingPrice) * CARD_CHARGE);
                String sCardCharge = Float.toString(cardCharge);
                String[] dotSub=sCardCharge.split("\\.|,");
                System.out.println("Split: "+dotSub[1]);
                if(dotSub[1].toString().length()<=2){

                    dotSub[1]=dotSub[1]+"000";
                    System.out.println("Cargo con dos decimales; "+  dotSub[1]);
                }
                String[] dotSub2=dotSub[1].toString().split("");
                System.out.println("Dotsun2: " + dotSub2[3]);
                if(dotSub2[3].toString().matches("^[1-9]")){
                    System.out.println("Diferente de Cero");
                    cardCharge=cardCharge+0;
                    System.out.println(sCardCharge);
                    cardCharge=cardCharge+0;
                    DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.US);
                    otherSymbols.setDecimalSeparator('.');
                    otherSymbols.setGroupingSeparator('.');
                    DecimalFormat df = new DecimalFormat("#.##",otherSymbols);
                    df.setRoundingMode(RoundingMode.CEILING);
                    cardCharge = Float.valueOf(df.format(cardCharge));
                    System.out.println(df.format(cardCharge));
                    total = subtotal  + cardCharge;
                }
                else{
                    cardCharge=cardCharge+0;
                    System.out.println(sCardCharge);
                    cardCharge=cardCharge+0;
                    DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.US);
                    otherSymbols.setDecimalSeparator('.');
                    otherSymbols.setGroupingSeparator('.');
                    DecimalFormat df = new DecimalFormat("#.###",otherSymbols);
                    df.setRoundingMode(RoundingMode.CEILING);
                    cardCharge = Float.valueOf(df.format(cardCharge));
                    System.out.println(df.format(cardCharge));
                    total = subtotal  + cardCharge;
                }

                orderCardCharge.setText("$ " + Parser.decimalFormatString(cardCharge));
                orderCardChargeRow.setVisibility(View.VISIBLE);
                //total = subtotal ;
            }

        } else if (PAYMENT_METHOD == 3){
            // Pago con Tigo Money
            showCardControls(3);

            float tgmoneyCharge =(float) 0.00;

            if (SERVICE_TYPE == 3) {
                tgmoneyCharge = (float) ((subtotal + shippingPrice) * TMONEY_CHARGE);

                String sCardCharge = Float.toString(tgmoneyCharge);
                String[] dotSub=sCardCharge.split("\\.|,");
                System.out.println("Split: "+dotSub[1]);
                if(dotSub[1].toString().length()<=2){

                    dotSub[1]=dotSub[1]+"000";
                    System.out.println("Cargo con dos decimales; "+  dotSub[1]);
                }
                String[] dotSub2=dotSub[1].toString().split("");
                System.out.println("Dotsun2: " + dotSub2[3]);


                if(dotSub2[3].toString().matches("^[1-9]")){
                    DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.US);
                    otherSymbols.setDecimalSeparator('.');
                    otherSymbols.setGroupingSeparator('.');
                    DecimalFormat df = new DecimalFormat("#.##",otherSymbols);
                    df.setRoundingMode(RoundingMode.CEILING);
                    tgmoneyCharge = Float.valueOf(df.format(tgmoneyCharge));
                    System.out.println(df.format(tgmoneyCharge));
                    total = subtotal + shippingPrice + tgmoneyCharge;
                    orderTgMoneyCharge.setText("$ " + Parser.decimalFormatString(tgmoneyCharge));
                }
                else{

                    DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.US);
                    otherSymbols.setDecimalSeparator('.');
                    otherSymbols.setGroupingSeparator('.');
                    DecimalFormat df = new DecimalFormat("#.###",otherSymbols);
                    df.setRoundingMode(RoundingMode.CEILING);
                    tgmoneyCharge = Float.valueOf(df.format(tgmoneyCharge));
                    System.out.println(df.format(tgmoneyCharge));
                    total = subtotal + shippingPrice + tgmoneyCharge;
                    orderTgMoneyCharge.setText("$ " + Parser.decimalFormatString(tgmoneyCharge));
                }

            } else {
                tgmoneyCharge = (float) ((subtotal + shippingPrice) * TMONEY_CHARGE);

                String sCardCharge = Float.toString(tgmoneyCharge);
                String[] dotSub=sCardCharge.split("\\.|,");
                System.out.println("Split: "+dotSub[1]);
                if(dotSub[1].toString().length()<=2){

                    dotSub[1]=dotSub[1]+"000";
                    System.out.println("Cargo con dos decimales; "+  dotSub[1]);
                }
                String[] dotSub2=dotSub[1].toString().split("");
                System.out.println("Dotsun2: " + dotSub2[3]);


                if(dotSub2[3].toString().matches("^[1-9]")){
                    DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.US);
                    otherSymbols.setDecimalSeparator('.');
                    otherSymbols.setGroupingSeparator('.');
                    DecimalFormat df = new DecimalFormat("#.##",otherSymbols);
                    df.setRoundingMode(RoundingMode.CEILING);
                    tgmoneyCharge = Float.valueOf(df.format(tgmoneyCharge));
                    System.out.println(df.format(tgmoneyCharge));
                    total = subtotal + shippingPrice + tgmoneyCharge;
                    orderTgMoneyCharge.setText("$ " + Parser.decimalFormatString(tgmoneyCharge));
                }
                else{

                    DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.US);
                    otherSymbols.setDecimalSeparator('.');
                    otherSymbols.setGroupingSeparator('.');
                    DecimalFormat df = new DecimalFormat("#.##",otherSymbols);
                    df.setRoundingMode(RoundingMode.CEILING);
                    tgmoneyCharge = Float.valueOf(df.format(tgmoneyCharge));
                    System.out.println(df.format(tgmoneyCharge));
                    total = subtotal + shippingPrice + tgmoneyCharge;
                    orderTgMoneyCharge.setText("$ " + Parser.decimalFormatString(tgmoneyCharge));
                }

            }
        }

        // Mostrar costo de envio
        if (shippingPrice > (0.0) && SERVICE_TYPE == 3) {
            orderShippingChargeRow.setVisibility(View.VISIBLE);
            orderShippingCharge.setText("$ " + Parser.decimalFormatString(shippingPrice));
        } else {
            orderShippingChargeRow.setVisibility(View.GONE);
            shippingPrice = (float) 0.0;
        }

        //Mostrar descuento si el bin aplica.
        if (flagDisc == 1){
            //Aplica descuento al monto total.
            totalAux = total;
            porcDesc = (percentageDisc / 100);

            totalDisccount = (totalAux * porcDesc); //Total a descontar
            totalAux = totalAux - totalDisccount; //Monto Total con descuento

            orderSubtotal.setText("$ " + Parser.decimalFormatString(subtotal));
            orderDiscount.setText("$ " + Parser.decimalFormatString(totalDisccount));
            orderDiscountRow.setVisibility(View.VISIBLE);
            orderTotal.setText("$ " + Parser.decimalFormatString(totalAux));
        }else {
            orderSubtotal.setText("$ " + Parser.decimalFormatString(subtotal));
            orderDiscountRow.setVisibility(View.GONE);
            orderTotal.setText("$ " + Parser.decimalFormatString(total));
        }
    }

    static int values[] = new int[(300/5)];
    static {
        for(int i=0; i<(300/5);i++){
            values[i] = 5 *(i+1);
        }
    }

    /**
     * Esta funcion devuelve un arreglo de valores para seleccionar en la cantidad de efectivo
     * @return cashOptions
     */
    List<String> cash(){
        List<String> cashOptions = new ArrayList<>();
        cashOptions.add(Parser.decimalFormatString(total));
        for(int value: values)
            if(value > this.total) cashOptions.add(Parser.decimalFormatString(value));
        return cashOptions;
    }

    /**
     * Años disponible para tarjeta de credito/debito
     * @return years
     */
    List<String> years(){
        List<String> years = new ArrayList<String>();
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        for(int i = 0; i < 35; i++){
            years.add(String.valueOf(year + i));
        }
        return years;
    }

    List<String> getmonths(){
        List<String> months = new ArrayList<String>();
        Calendar calendar = Calendar.getInstance();
        int month = calendar.get(Calendar.MONTH);
        for(int i = 0; i < 12; i++){
            months.add(String.valueOf(month + i));
        }
        return months;
    }


    /**
     * Funcion para aplicar duescuento al monto total segun convenio
     * con tarjeta de credito o debito
     * @param bin
     */
    private void getParamDiscount(String bin){
        //bin = "431063";
        VoidTask task0 = new VoidTask("/order/bin-verify");
        task0.addParam("bin", String.valueOf(bin));
        task0.setPostExecute(new VoidTask.PostExecute() {
            @Override
            public void execute(StringBuilder response) {
                try {
                    Log.d("binVerify:WS-RESULT", " Reponse " + response.toString());
                    org.json.JSONObject data = new org.json.JSONObject(response.toString());
                    if (data.getString("status").equals("true")) {
                        percentageDisc = Float.parseFloat(data.getString("porcentaje"));
                        flagDisc = Integer.parseInt(data.getString("active"));
                        calculateTotal();
                    }
                } catch (JSONException e) {
                    Log.d("binVerify:PARSE-ERR", e.getMessage());
                }
            }
        });
        task0.execute();
    }

    /**
     * Funcion para almacenar datos de compra del usuario en Appboy
     * @param req
     */
    private void    saveAppboyRevenue(RestaurantRequest req){
        revenueBean = new RevenueBean();
        //String category = UD.getInstance().get(UD.CATEGORY_).toString();
        //String section = UD.getInstance().get(UD.SECTION_SELECTED_NAME_).toString();


        Log.d("nombre tag: ",""+U.get(UD.TAG_SELECTED_NAME_).toString());
        Log.d("id tag: ",""+U.get(UD.TAG_ID_).toString());



        String auxTipoPago= "";
        if (req.getPayMethod()==1){
            auxTipoPago= "Cash";
        }else if (req.getPayMethod()==2){
            auxTipoPago= "Credit Card";
        }else if (req.getPayMethod()==3){
            auxTipoPago= "Tigo Money";
        }else {auxTipoPago= "Other";}

        String TypeService ="";
        if (req.getServiceType() == 1){
            TypeService ="Domicilio";
        }else if(req.getServiceType() == 2){
            TypeService ="Para_llevar";
        }else if(req.getServiceType() == 3){
            TypeService ="Domicilio_Pidafacil";
        }else {TypeService ="OtherServiceType";}

        revenueBean.setPaymentType(auxTipoPago);
        revenueBean.setRestaurant(req.getName());
        revenueBean.setTypeService(TypeService);
        revenueBean.setId_seccion(U.get(UD.SECTION_).toString());
        revenueBean.setSeccion_nombre(U.get(UD.SECTION_SELECTED_NAME_).toString());

        revenueBean.setId_tag(U.get(UD.TAG_ID_).toString());
        revenueBean.setTag_nombre(U.get(UD.TAG_SELECTED_NAME_).toString());

        revenueBean.setShippingPrice(String.valueOf(shippingPrice));
        revenueBean.setSubtotal(String.valueOf(subtotal));
        for(int i=0;i<req.getProducts().size();i++){
            Product p = req.getProducts().get(i);
            RevenueProductBean rp = new RevenueProductBean();
            rp.setCurrencyCode("USD");
            rp.setPrice(p.getValue());
            rp.setQuantity(p.getQuantity());
            rp.setProductId(String.valueOf(p.getId()));
            rp.setProductName(p.getNombre());
            revenueBean.add(rp);
        }
    }

    /**
     * Funcion para capturar evento de compra en Appboy
     */
    private void sendAppboyRevenue(){
        Appboy appboy = Appboy.getInstance(this);
        for(int i=0;i<revenueBean.getProds().size();i++){
            RevenueProductBean prod = revenueBean.getProds().get(i);
            AppboyProperties properties = new AppboyProperties();
            properties.addProperty("restaurant", revenueBean.getRestaurant());
            properties.addProperty("type_service", revenueBean.getTypeService());
            properties.addProperty("payment_type", revenueBean.getPaymentType());
            properties.addProperty("subtotal", revenueBean.getSubtotal());
            properties.addProperty("shipping_price", revenueBean.getShippingPrice());
            appboy.logPurchase("ID: "+prod.getProductId() + " Name: "+prod.getProductName(), prod.getCurrencyCode(),
                    new BigDecimal(prod.getPrice()), prod.getQuantity(), properties);
        }
        Utils.appboyEvent(appboy, "Order Complete", new Object[]{});
    }


    private void sendAppboyCustomAttributesRevenue(){
        String sospechoso="";
        if(login.getSospecha().equals("1")){sospechoso ="SI";}else{sospechoso="NO";}


        Appboy.getInstance(CompleteRequestActivity.this).getCurrentUser().addToCustomAttributeArray(
                "Restaurante:", "ID: " + restaurant_id + "\nNombre: " + revenueBean.getRestaurant());
        Appboy.getInstance(CompleteRequestActivity.this).getCurrentUser().addToCustomAttributeArray(
                "Tipo pago:", revenueBean.getPaymentType()
        );
        Appboy.getInstance(CompleteRequestActivity.this).getCurrentUser().addToCustomAttributeArray(
                "Tipo Servicio:", revenueBean.getTypeService()
        );
        Appboy.getInstance(CompleteRequestActivity.this).getCurrentUser().addToCustomAttributeArray(
                "Seccion:", "ID "+ revenueBean.getId_seccion()+ "\nNombre: " + revenueBean.getSeccion_nombre()
        );
        Appboy.getInstance(CompleteRequestActivity.this).getCurrentUser().addToCustomAttributeArray(
                "Tag:", "ID:"+ revenueBean.getId_tag()+ "\nNombre: " + revenueBean.getTag_nombre()
        );
        Appboy.getInstance(CompleteRequestActivity.this).getCurrentUser().addToCustomAttributeArray(
                "Cliente sospechoso:",sospechoso
        );


    }




    /**
     * Mostrar/Ocultar campos de tarjeta de credito/debito
     * @param value
     */
    private void showCardControls(int value) {
        if (value == 1) {
            cardPayMethodLayout.setVisibility(View.GONE);
            tgMoneyPayMethodLayout.setVisibility(View.GONE);
            orderCashRow.setVisibility(View.VISIBLE);
            orderChangeRow.setVisibility(View.VISIBLE);
            orderCardChargeRow.setVisibility(View.GONE);
            orderTgMoneyChargeRow.setVisibility(View.GONE);
        } else if(value == 2) {
            cardPayMethodLayout.setVisibility(View.VISIBLE);
            tgMoneyPayMethodLayout.setVisibility(View.GONE);
            orderCashRow.setVisibility(View.GONE);
            orderChangeRow.setVisibility(View.GONE);
            orderCardChargeRow.setVisibility(View.VISIBLE);
            orderTgMoneyChargeRow.setVisibility(View.GONE);

        } else if (value == 3){
            tgMoneyPayMethodLayout.setVisibility(View.VISIBLE);
            cardPayMethodLayout.setVisibility(View.GONE);
            orderCashRow.setVisibility(View.GONE);
            orderChangeRow.setVisibility(View.GONE);
            orderCardChargeRow.setVisibility(View.GONE);
            orderTgMoneyChargeRow.setVisibility(View.VISIBLE);
        } else if(value == 4){
            labelEntrega.setVisibility(View.VISIBLE);
            lay_entrega.setVisibility(View.VISIBLE);
            addrTime.setVisibility(View.VISIBLE);

        }else if(value == 5){
            labelEntrega.setVisibility(View.GONE);
            lay_entrega.setVisibility(View.GONE);
            addrTime.setVisibility(View.GONE);

        }
    }

    /**
     * METODO ASINCRONO Q ENVIA LA DATA
     * */
    private void sendRequest(){
        SendRequestTask task = new SendRequestTask(this, 2);
        task.execute(jsonRequest.toJSONString());
    }

    /**
     * FUNCION PARA MOSTRAR MENSAJES DE DIALOGO
     * */
    private void errorMessage(String msg){
        AlertDialog.Builder builder =
                UIHelper.aceptAlert(CompleteRequestActivity.this,
                        getString(R.string.missing_data),
                        msg);
        AlertDialog d = builder.create();
        d.requestWindowFeature(Window.FEATURE_NO_TITLE);
        d.show();
    }

    /**
     * Funcion para mostrar pantalla principal de la aplicacion
     */
    private void goToMain() {
        Intent homeIntent = new Intent(getApplicationContext(), NavigationDrawer.class);
        homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(homeIntent);
        finish();
    }


    private void goToseccion(){
        Intent homeIntent = new Intent(getApplicationContext(), OrderActivity.class);
        startActivity(homeIntent);
        finish();
    }


    /**
     * Eliminar los productos en el carrito
     */
    private void deleteCartProducts(){
        RealmResults<RestaurantRequest> restaurant = r.allObjects(RestaurantRequest.class);
        if (restaurant.size() > 0) {
            r.beginTransaction();
            RestaurantRequest bean = restaurant.first();
            bean.removeFromRealm();
            r.commitTransaction();
        }
    }
    /**
     * RESULTADOS DE DIALOGOS EMERGENTES
     * */
    @Override
    public synchronized void endDialog(Object value, int operation) {

        if(operation == 2){
            if(value != null ){
                addrTxt.setText(value.toString());
                addrTime.setText(value.toString());
                address = Integer.valueOf(UD.getInstance().get(UD.DIALOG_LIST_POSITION).toString());
                addrTxt.setText(addrbeans.get(address).getName());
                addrTime.setText(addrbeans.get(address).getTime());
                addressId = addrbeans.get(address).getId();//Cuando se selecciona desde del dialog addres
                addressShippingCharge(address);
            }
        }
    }

    /**
     * RESPUESTAS DEL WEBSERVICE QUE AFECTAN LA ACTIVIDAD.
     * */
    @Override
    public void executeResult(List list, int operationCode) {
        if(operationCode == 2){
            boolean isOk = (boolean) list.iterator().next();
            if(isOk){
                sendAppboyRevenue();
                sendAppboyCustomAttributesRevenue();
                findViewById(R.id.sending_).setVisibility(View.GONE);
                AlertDialog.Builder b = UIHelper.alert(CompleteRequestActivity.this,
                        getString(R.string.order_comple),
                        "¡Gracias por tu compra!, ve a tu perfil para conocer el estado de tus pedidos.");
                b.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        deleteCartProducts();
                        goToseccion();

                    }
                });
                b.show();
            } else {
                findViewById(R.id.sending_).setVisibility(View.GONE);
                AlertDialog.Builder b = UIHelper.alert(CompleteRequestActivity.this,
                        null, list.get(1).toString());
                if(list.get(1).toString().equals("Lo sentimos, Su tarjeta fue rechazada por el Banco. Intente con otro método de pago.")){
                    Utils.appboyEvent(appboy, getString(R.string.appboy_tarjeta_rechazada), new Object[]{});
                }
                b.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        Intent homeIntent = new Intent(getApplicationContext(), CompleteRequestActivity.class);
                        startActivity(homeIntent);
                        finish();
                    }
                });
                b.show();
            }
        } else {
            if (operationCode == 0 || operationCode == 1) {
                // Almacenar direcciones en arreglo local
                CompleteRequestActivity.addrbeans = new ArrayList<AddressBean>();
                if (list != null) {
                    for (Object object : list) {
                        addrbeans.add((AddressBean) object);
                    }

                    if (addrbeans.size() > 0) {
                        // Agregar primera direccion de usuario de forma predeterminada
                        addrTxt.setText(addrbeans.get(0).getName()); // seteo al entrar a proceder a la orden
                        addrTime.setText(addrbeans.get(0).getTime());
                        address = addrbeans.get(0).getId();
                        addressId = address;
                        addressShippingCharge(0);
                    }
                }
            }else if(operationCode==3){
                String a =UD.getInstance().get("MEX").toString();
                if(!mex.isEmpty() && mex.length()!=0 && mex != null) {
                    Log.d("MEX dentro", ""+mex);
                    mex=a;
                }else {
                    mex = "0";
                }
            }
        }
    }

    /**
     * RESPUESTAS DE OTRAS ACTIVIDADES QUE CAMBIAN ELEMENTOS
     * INTERNOS.
     * */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case (AddAddressActivity.ADD_ADDRESS_REQ_CODE) : {
                if (resultCode == Activity.RESULT_OK) {
                    String id = data.getStringExtra("address_id");
                    addressId = Integer.valueOf(id);
                    Log.d(TAG, "Address ID: " + id);
                    if(id!=null){
                        Integer idaddr = Integer.valueOf(id);
                        zone_id = Integer.valueOf(data.getExtras().get("zone_id").toString());
                        AddressBean bean = new AddressBean(idaddr, data.getStringExtra("name"));
                        bean.setIdZone(zone_id);
                        CompleteRequestActivity.addrbeans.add(bean);
                        address = CompleteRequestActivity.addrbeans.size()-1; //cuando agregar direccion
                        addrTxt.setText(addrbeans.get(address).getName());
                        addrTime.setText(addrbeans.get(address).getTime());///Viene nulo porq al agregar dir no esta ese campo habra q consumilo para presentarlo.
                        finish();
                        startActivity(getIntent());
                        dialogAddress.dismiss();
                        addressShippingCharge(address);
                    }
                }
                break;
            }
        }
    }

    /**
     * Objeto que permite elegir una hora del dia
     */
    public static class TimePickerFragment extends DialogFragment
            implements android.app.TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);
            android.app.TimePickerDialog dialog = new android.app.TimePickerDialog(getActivity(),
                    this, hour, minute,DateFormat.is24HourFormat(getActivity()));
            dialog.setTitle(null);
            dialog.requestWindowFeature(STYLE_NO_TITLE);
            return dialog;
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            // Do something with the time chosen by the user
            try {
                String hour = String.valueOf(hourOfDay);
                String min = String.valueOf(minute);
                if (hour.length() == 1) hour = "0".concat(hour);
                if (min.length() == 1) min = "0".concat(min);
                String selectedTime = String.format("%s:%s", hour, min);
                time = selectedTime;
                SimpleDateFormat inputFormat = new SimpleDateFormat("HH:mm");
                SimpleDateFormat outputFormat = new SimpleDateFormat("hh:mm a");
                String timeFormated = outputFormat.format(inputFormat.parse(selectedTime));
                txtHourSel.setText(timeFormated);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }
    @Override
    protected void onPause() {
        super.onPause();
        Utils.appboyEvent(appboy, getString(R.string.appboy_order_abandoned), new Object[]{});
    }
}
