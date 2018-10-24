package com.pidafacil.pidafacil.fragments;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.appboy.Appboy;
import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.pidafacil.pidafacil.R;
import com.pidafacil.pidafacil.activities.ShoppingCartActivity;
import com.pidafacil.pidafacil.beans.ConditionBean;
import com.pidafacil.pidafacil.beans.IngredientBean;
import com.pidafacil.pidafacil.beans.OptionBean;
import com.pidafacil.pidafacil.beans.ProductBean;
import com.pidafacil.pidafacil.beans.ReqOptionBean;
import com.pidafacil.pidafacil.beans.UserRequestBean;
import com.pidafacil.pidafacil.components.CustomLinearLayoutManager;
import com.pidafacil.pidafacil.components.OptionsAdapter;
import com.pidafacil.pidafacil.helper.WebServiceHelper;
import com.pidafacil.pidafacil.helper.RealmHelper;
import com.pidafacil.pidafacil.helper.UIHelper;
import com.pidafacil.pidafacil.model.Ingredient;
import com.pidafacil.pidafacil.model.Option;
import com.pidafacil.pidafacil.model.Product;
import com.pidafacil.pidafacil.model.RestaurantRequest;
import com.pidafacil.pidafacil.singleton.UD;
import com.pidafacil.pidafacil.task.ExecutionMethod;
import com.pidafacil.pidafacil.task.VoidActivityTask;
import com.pidafacil.pidafacil.util.Parser;
import com.pidafacil.pidafacil.util.Resource;
import com.pidafacil.pidafacil.util.Utils;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;
import it.neokree.materialnavigationdrawer.MaterialNavigationDrawer;

/**
 * Created by victor on 05-29-15.
 * Fragmento de detalle de producto.
 */
public class DetailFragment extends Fragment implements ExecutionMethod{

    private RecyclerView recyclerViewOptions;
    private List<IngredientBean> ingredientBeans;
    private List<ConditionBean> conditionBeans;
    private UserRequestBean userRequest;
    private Integer productId = null;
    private RealmList<Ingredient> ingredients;
    private RealmList<Option> options;
    private TextView txtProdname;
    private TextView productDetails;
    private Spinner quantitySpin;
    private CircularProgressView loading_;
    private Realm r;
    private Integer quantity;
    private String prodName;
    private String prodNameVisitado;
    private Float price = ((float) 0);
    private String commet;
    private String imageUri;
    private View view;
    private UD U = UD.getInstance();
    private ProductBean productBean;
    private ImageView img;
    CallbackManager callbackManager;
    ShareDialog shareDialog;

    private static final String TAG = DetailFragment.class.getSimpleName();

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = inflater.inflate(R.layout.fragment_detail, container, false);
        viewBackground();
        U.setContext(view.getContext());
        UD.VIEW_ = 4;
        loading_ = (CircularProgressView) view.findViewById(R.id.loading_);
        loading_.startAnimation();
        ingredients = new RealmList<Ingredient>();
        options = new RealmList<Option>();
        ingredientBeans = new ArrayList<IngredientBean>();
        r = Realm.getInstance(view.getContext());


        if(UD.getInstance().get(UD.PRODUCT_NAME_).toString() != null){
            getActivity().setTitle(UD.getInstance().get(UD.PRODUCT_NAME_).toString());
        }else{ getActivity().setTitle("PidaFacil Products");}

        configure(view);
        UD.getInstance().setCurrentActivity(this);

        userRequest =(UserRequestBean) UD.getInstance()
                .get(UD.CURRENT_USER_RESTAURANT_REQUEST_);
        productId = Parser.toInt(UD.getInstance().get(UD.PRODUCT_));
        VoidActivityTask taskActivity = WebServiceHelper.getDetailQuery(this, 0);
        taskActivity.addParam("product_id", productId.toString());
        taskActivity.execute();

        FacebookSdk.sdkInitialize(getActivity());
        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(this);


        return view;
    }

    public void configure(View view){
        ArrayAdapter adapter = ArrayAdapter.createFromResource(view.getContext(),
                R.array.products_quantity, R.layout.spinner_dropdown_item_custom_number);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_custom_item);
        ((Spinner) view.findViewById(R.id.spinner_quantity)).setAdapter(adapter);

        recyclerViewOptions = (RecyclerView) view.findViewById(R.id.rec_detail_options);
        recyclerViewOptions.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(view.getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerViewOptions.setLayoutManager(new CustomLinearLayoutManager
                (view.getContext(), LinearLayoutManager.VERTICAL, false));
        buttons();
    }

    public void show(ProductBean bean){
        try {
            img = (ImageView) view.findViewById(R.id.img_product);
            TextView productName = (TextView) view.findViewById(R.id.text_productName);
            productDetails = (TextView) view.findViewById(R.id.text_productDetail);
            TextView price0 = (TextView) view.findViewById(R.id.txt_price_);
            view.findViewById(R.id.loading_).setVisibility(View.GONE);
            view.findViewById(R.id._content_).setVisibility(View.VISIBLE);

            Log.d("Producto", "" + productId + bean.getName());
            prodNameVisitado=bean.getName();
            sendAppboyCustomAttributesRevenue();

            if (bean.getImageUri() != null) {
                if (!bean.getImageUri().equals("null")) {
                    imageUri = Resource.RESOURCE_URI + bean.getImageUri();
                    Picasso.with(getActivity().getApplicationContext()).load(imageUri).into(img);
                } else {
                    try{
                        imageUri = Resource.RESOURCE_URI + UD.getInstance().get("logo_uri__").toString();
                        Picasso.with(getActivity().getApplicationContext()).load(imageUri).into(img);
                    }catch (NullPointerException e){ Log.d("EXCEPTION", "logo no existe");}
                }
            }

            productName.setText(bean.getName());
            price = bean.getValue();
            price0.setText("$" + Parser.decimalFormatString(price));
            this.productDetails.setText(bean.getDescription());
            this.conditionBeans = bean.getConditions();
            this.ingredientBeans = bean.getIngredients();

            if (this.conditionBeans != null) {
                if (this.conditionBeans.size() > 0) {
                    this.recyclerViewOptions.setAdapter(new OptionsAdapter(this.conditionBeans));
                }
            }

            ingredients.clear();
            for (IngredientBean b : this.ingredientBeans) {
                Ingredient ingredient = new Ingredient();
                ingredient.setName(b.getDescription());
                ingredient.setId(b.getId());
                ingredient.setSelected(1);
                ingredients.add(ingredient);
            }

            if (ingredientBeans.size() == 0) {
                view.findViewById(R.id.txt_ingredients).setVisibility(View.GONE);
            }

            view.findViewById(R.id.linearviewcontent).setVisibility(View.VISIBLE);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    ReqOptionBean getOptionBean(String selectedOption, Integer selectedValue){
        for(ConditionBean bean : conditionBeans){
            for(OptionBean bean0 : bean.getOptions()){
                if(selectedOption.equals(bean0.getDescription())){
                    if (!bean0.getConditionOptionId().equals(selectedValue)) {
                        return new ReqOptionBean(bean.getId(), bean0.getConditionOptionId());
                    }
                }
            }
        }
        return null;
    }

    void buttons(){
        TextView txtIngredients = (TextView) view.findViewById(R.id.txt_ingredients);
        txtIngredients.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                LayoutInflater inflater = getActivity().getLayoutInflater();
                ScrollView lay_ = (ScrollView) inflater.inflate(R.layout.dialog_list_ingredient_dialog_lay, null);
                dialog.setView(lay_);
                LinearLayout lay_checkboxes = (LinearLayout) lay_.getChildAt(0);

                for(Ingredient b : ingredients){
                    CheckBox checkBox = new CheckBox(getActivity());
                    LinearLayout.LayoutParams params =
                            new LinearLayout.LayoutParams(
                                    ViewGroup.LayoutParams.MATCH_PARENT,
                                    ViewGroup.LayoutParams.WRAP_CONTENT);
                    params.setMargins(0,5,0,25);
                    checkBox.setLayoutParams(params);
                    checkBox.setTextSize(20);
                    checkBox.setChecked(b.getSelected() == 1 ? true : false);
                    checkBox.setText(b.getName());
                    lay_checkboxes.addView(checkBox);
                }

                UD.getInstance().put("listView_ingr", lay_checkboxes);

                dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        LinearLayout lay_checkboxes = (LinearLayout) UD.getInstance().get("listView_ingr");
                        for (int i = 0; i < lay_checkboxes.getChildCount(); i++) {
                            CheckBox checkBox = (CheckBox) lay_checkboxes.getChildAt(i);
                            for(int x = 0; x < ingredients.size() ; x++){
                                if(ingredients.get(x).getName().equals(checkBox.getText().toString())){
                                    ingredients.get(x).setSelected(checkBox.isChecked()?1:0);
                                }
                            }
                        }
                    }
                });

                dialog.show();
            }
        });

        Button button0 = (Button) view.findViewById(R.id.add_button);
        button0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                quantitySpin = ((Spinner)view.findViewById(R.id.spinner_quantity));
                quantity = Integer.valueOf(quantitySpin.getSelectedItem().toString());
                quantity = (quantity == null ? 0: quantity);
                txtProdname = (TextView) view.findViewById(R.id.text_productName);
                prodName = (txtProdname).getText().toString();
                TextView txt_commet = (TextView) view.findViewById(R.id.txt_comments_);
                commet = txt_commet.getText().toString();

                Integer lastOptionId = 0;

                for(int e=0; e < recyclerViewOptions.getChildCount(); e++){

                    LinearLayout linearLayout = (LinearLayout) recyclerViewOptions.getChildAt(e);
                    LinearLayout spinnerLayout = (LinearLayout) linearLayout.getChildAt(1);
                    Spinner spinner = (Spinner) spinnerLayout.getChildAt(1);
                    Object object = spinner.getSelectedItem();
                    String selectedOption = object.toString();
                    ReqOptionBean o = getOptionBean(selectedOption, lastOptionId);

                    if(o!=null){
                        Option option = new Option();
                        option.setId(UUID.randomUUID().toString());
                        option.setName(object.toString());
                        option.setConditionOptionId(o.getCondition_option_id());
                        option.setConditionId(o.getCondition_id());
                        options.add(option);
                        lastOptionId = o.getCondition_option_id();
                    }else{
                        errorMessage();
                        return;
                    }
                }

                RealmResults<RestaurantRequest> requests =  r.allObjects(RestaurantRequest.class);

                if(requests.size()>0){
                    RestaurantRequest restaurant = requests.first();
                    if(userRequest.getRestaurant_id() != restaurant.getId()){
                        AlertDialog.Builder alertDialogBuilder =
                                UIHelper.alert(getActivity(),
                                        "Solo puedes ordenar de un restaurante a la vez",
                                        "Si agregas este producto se removera la orden actual de tu Carrito de Compras.");
                        alertDialogBuilder
                                .setCancelable(false)
                                .setPositiveButton("Agregar", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        RealmHelper helper = new RealmHelper();
                                        helper.clearRequest(r, r.allObjects(RestaurantRequest.class));
                                        newProduct();
                                        dialog.dismiss();
                                    }
                                }).setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        alertDialogBuilder.show();
                        return;
                    }
                    addProduct();
                }else{
                    newProduct();
                }
            }
        });
        FrameLayout shareItemFacebok = (FrameLayout) view.findViewById(R.id.frmShareItemFacebook);
        shareItemFacebok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Utils.appboyEvent(Appboy.getInstance(getActivity().getApplicationContext()),
                            getActivity().getString(R.string.appboy_share_product),
                            new Object[]{productBean.getName()});
                    String slug = productBean.getImageUri().replace("/restaurants", "");
                    String realSlug = slug.replace("/image_web.jpeg", "");
                     if (ShareDialog.canShow(ShareLinkContent.class)) {
                        ShareLinkContent linkContent = new ShareLinkContent.Builder()
                                .setContentTitle("PidaFacil")
                                .setContentDescription(
                                        productBean.getName())
                                .setImageUrl(Uri.parse(imageUri))
                                .setContentUrl(Uri.parse("http://pidafacil.com" + realSlug))
                                .build();
                             shareDialog.show(linkContent);
                    }
                } catch (Exception ex) {
                    Log.e(TAG, ex.getMessage());
                }
            }
        });


        FrameLayout shareItemInstagram = (FrameLayout) view.findViewById(R.id.frmShareItemInstagram);
        shareItemInstagram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Utils.appboyEvent(Appboy.getInstance(getActivity().getApplicationContext()),
                            getActivity().getString(R.string.appboy_share_product),
                            new Object[]{productBean.getName()});
                    String slug = productBean.getImageUri().replace("/restaurants", "");
                    String realSlug = slug.replace("/image_web.jpeg", "");
                    Uri uri = getLocalBitmapUri(img);
                    Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
                    shareIntent.setType("image/*");
                    //shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,  "PidaFacil" + " - " + productBean.getName());
                    shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, productBean.getName() + " " +  "http://pidafacil.com" + realSlug);
                    shareIntent.putExtra(Intent.EXTRA_STREAM, uri);

                    boolean resolved = false;
                    PackageManager pm = v.getContext().getPackageManager();
                    List<ResolveInfo> activityList = pm.queryIntentActivities(shareIntent, 0);
                    for (final ResolveInfo app : activityList)
                    {
                        if ((app.activityInfo.name).contains("instagram"))
                        {
                            final ActivityInfo activity = app.activityInfo;
                            final ComponentName name = new ComponentName(activity.applicationInfo.packageName, activity.name);
                            shareIntent.addCategory(Intent.CATEGORY_LAUNCHER);
                            shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                            shareIntent.setComponent(name);
                            v.getContext().startActivity(shareIntent);
                            resolved = true;
                            break;
                        }
                    }
                    if(!resolved){
                        Toast.makeText(getActivity().getApplicationContext(), "Debes instalar Instagram para compartir.", Toast.LENGTH_LONG).show();
                    }

                } catch (Exception ex) {
                    Log.e(TAG, ex.getMessage());
                }
            }
        });


        FrameLayout shareItemWhatsapp = (FrameLayout) view.findViewById(R.id.frmShareItemWhatsapp);
        shareItemWhatsapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Utils.appboyEvent(Appboy.getInstance(getActivity().getApplicationContext()),
                            getActivity().getString(R.string.appboy_share_product),
                            new Object[]{productBean.getName()});
                    String slug = productBean.getImageUri().replace("/restaurants", "");
                    String realSlug = slug.replace("/image_web.jpeg", "");

                   /* Utils.shareContent(
                            getActivity(),
                            productBean.getName(),
                            "PidaFacil",
                            "http://pidafacil.com" + realSlug,
                            img);

                    Context ctx, String message, String title, String url, ImageView image*/
                    Uri uri = getLocalBitmapUri(img);
                    Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
                    shareIntent.setType("image/*");
                    shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,  "PidaFacil" + " - " + productBean.getName());
                    shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, productBean.getName() + " " +  "http://pidafacil.com" + realSlug);
                    shareIntent.putExtra(Intent.EXTRA_STREAM, uri);

                    boolean resolved = false;
                    PackageManager pm = v.getContext().getPackageManager();
                    List<ResolveInfo> activityList = pm.queryIntentActivities(shareIntent, 0);
                    for (final ResolveInfo app : activityList) {
                        if ((app.activityInfo.name).contains("com.whatsapp")) {
                            final ActivityInfo activity = app.activityInfo;
                            final ComponentName name = new ComponentName(
                                    activity.applicationInfo.packageName, activity.name);
                            shareIntent.addCategory(Intent.CATEGORY_LAUNCHER);
                            shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                                    | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                            shareIntent.setComponent(name);
                            v.getContext().startActivity(shareIntent);
                            resolved = true;
                            break;
                        }
                    }
                    if(!resolved){
                        Toast.makeText(getActivity().getApplicationContext(), "Debes instalar WhatsApp para compartir", Toast.LENGTH_LONG).show();
                    }
                } catch (Exception ex) {
                    Log.e(TAG, ex.getMessage());
                }
            }
        });


        FrameLayout shareItemTwitter = (FrameLayout) view.findViewById(R.id.frmShareItemTwitter);
        shareItemTwitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Utils.appboyEvent(Appboy.getInstance(getActivity().getApplicationContext()),
                            getActivity().getString(R.string.appboy_share_product),
                            new Object[]{productBean.getName()});
                    String slug = productBean.getImageUri().replace("/restaurants", "");
                    String realSlug = slug.replace("/image_web.jpeg", "");

                    Uri uri = getLocalBitmapUri(img);
                    Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,  "PidaFacil" + " - " + productBean.getName());
                    shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, productBean.getName() + " " +  "http://pidafacil.com" + realSlug);
                    shareIntent.putExtra(Intent.EXTRA_STREAM, uri);

                    boolean resolved = false;
                    PackageManager pm = v.getContext().getPackageManager();
                    List<ResolveInfo> activityList = pm.queryIntentActivities(shareIntent, 0);
                    for (final ResolveInfo app : activityList)
                    {
                        if ((app.activityInfo.name).contains("twitter"))
                        {
                            final ActivityInfo activity = app.activityInfo;
                            final ComponentName name = new ComponentName(activity.applicationInfo.packageName, activity.name);
                            shareIntent.addCategory(Intent.CATEGORY_LAUNCHER);
                            shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                            shareIntent.setComponent(name);
                            v.getContext().startActivity(shareIntent);
                            resolved = true;
                            break;
                        }
                    }
                    if(!resolved){
                        Toast.makeText(getActivity().getApplicationContext(), "Debes instalar Twitter para compartir", Toast.LENGTH_LONG).show();
                    }

                } catch (Exception ex) {
                    Log.e(TAG, ex.getMessage());
                }
            }
        });



    }
    public static Uri getLocalBitmapUri(ImageView imageView) {
        // Extract Bitmap from ImageView drawable
        Drawable drawable = imageView.getDrawable();
        Bitmap bmp = null;
        if (drawable instanceof BitmapDrawable){
            bmp = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        } else {
            return null;
        }
        // Store image to default external storage directory
        Uri bmpUri = null;
        try {
            File file =  new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS), "share_image_" + System.currentTimeMillis() + ".png");
            file.getParentFile().mkdirs();
            FileOutputStream out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.close();
            bmpUri = Uri.fromFile(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bmpUri;
    }

    private void errorMessage() {
        AlertDialog.Builder b = UIHelper.aceptAlert(getActivity(), null, "Debes seleccionar todas las condiciones.");
        AlertDialog d = b.create();
        d.requestWindowFeature(Window.FEATURE_NO_TITLE);
        d.show();
    }

    void message(){
        Utils.appboyEvent(Appboy.getInstance(getActivity().getApplicationContext()), "Add To Cart", new Object[]{});
        Log.d("Add_cart", "Nombre_Produc_add" + prodName);
        Log.d("RESTAURANT_", "RESTAURANT_" + UD.getInstance()
                .get(UD.RESTAURANT_).toString());
        Log.d("SECTION_SELECTED_NAME_", "SECTION_SELECTED_NAME_" + UD.getInstance()
                .get(UD.SECTION_SELECTED_NAME_).toString());
        Log.d("RESTAURANT_NAME_", "RESTAURANT_NAME_" + UD.getInstance()
                .get(UD.RESTAURANT_NAME_).toString());

        sendAppboyCustomAttributesRevenue2();

        AlertDialog.Builder b = UIHelper.alert(getActivity(), "¡Producto agregado!", "El producto '" + prodName + "' ha sido agregado al carrito");
        b.setPositiveButton("Ir al carrito", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(getActivity().getApplicationContext(), ShoppingCartActivity.class));
            }
        });
        b.setNegativeButton("Seguir comprando", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                UD.VIEW_ = 4;
                toParent();
            }
        });
        b.show();
    }

    void toParent(){
        MaterialNavigationDrawer parent = (MaterialNavigationDrawer) getActivity();
        if(UD.VIEW_ == 4)
            parent.onBackPressed();
        else
            parent.setFragmentChild(new PromoProductFragment(), UD.getInstance().get(UD.RESTAURANT_NAME_).toString());
    }

    void addProduct() {
        RestaurantRequest restaurant = r.allObjects(RestaurantRequest.class).first();
        r.beginTransaction();
        Product product0 = r.createObject(Product.class);
        product0.setId(productId);

        for(Ingredient ingr : ingredients){
            Ingredient ingr0 = r.createObject(Ingredient.class);
            ingr0.setName(ingr.getName());
            ingr0.setId(ingr.getId());
            ingr0.setSelected(ingr.getSelected());

            product0.getIngredients().add(ingr0);
        }

        for(Option op: options){
            Option op0 = r.createObject(Option.class);
            op0.setId(op.getId());
            op0.setConditionId(op.getConditionId());
            op0.setConditionOptionId(op.getConditionOptionId());
            op0.setName(op.getName());

            product0.getOptions().add(op0);

        }

        product0.setQuantity(quantity);
        product0.setNombre(prodName);
        product0.setDetails(productDetails.getText().toString());
        product0.setValue(price);
        product0.setComment(commet);
        product0.setImageUri(imageUri);

        restaurant.getProducts().add(product0);
        r.copyToRealm(restaurant);
        r.commitTransaction();
        message();
    }

    void newProduct(){
        Product product = new Product();
        product.setId(productId);
        product.setIngredients(ingredients);
        product.setOptions(options);
        product.setQuantity(quantity);
        product.setNombre(prodName);
        product.setDetails(productDetails.getText().toString());
        product.setValue(price);
        product.setComment(commet);
        product.setImageUri(imageUri);

        RestaurantRequest req = new RestaurantRequest();
        req.setId(userRequest.getRestaurant_id());
        req.setName(UD.getInstance().get(UD.RESTAURANT_NAME_).toString());
        req.getProducts().add(product);

        r.beginTransaction();
        r.copyToRealm(req);
        r.commitTransaction();
        message();
    }

    @Override
    public void executeResult(List list, int operationCode) {
        if(operationCode == 0){
            if(list!=null)
                if(list.size()>0){
                    productBean = (ProductBean) list.iterator().next();
                    show(productBean);
                }
        }
    }

    private Target targetBackground = new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            view.setBackgroundDrawable(new BitmapDrawable(getResources(), bitmap));
        }
        @Override
        public void onBitmapFailed(Drawable errorDrawable) { }
        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) { }
    };

    private void viewBackground(){
        try{
            Point size = new Point();
            ((WindowManager) view.getContext()
                    .getSystemService(Context.WINDOW_SERVICE))
                    .getDefaultDisplay().getSize(size);
            Picasso.with(getActivity().getApplicationContext())
                    .load(Resource.RESOURCE_URI + U.get("uri_restaurant_background").toString())
                    .resize(size.x, size.y).centerCrop().into(targetBackground);
        }catch (NullPointerException e){ }
    }


    private void sendAppboyCustomAttributesRevenue(){
        Appboy.getInstance(UD.getInstance().getContext()).getCurrentUser().addToCustomAttributeArray(
                "Producto_Visitado:", "ID:" + productId + "\nNombre: " +prodNameVisitado
        );
    }


    private void sendAppboyCustomAttributesRevenue2(){
        Appboy.getInstance(UD.getInstance().getContext()).getCurrentUser().addToCustomAttributeArray(
                "Producto_Add_Carrito:", "ID:" + productId + "\nNombre: " +prodName +"\nCantidad:" + quantity
        );
        Appboy.getInstance(UD.getInstance().getContext()).getCurrentUser().addToCustomAttributeArray(
                "Seccion_Add_Carrito:", "ID:" + UD.getInstance()
                        .get(UD.SECTION_).toString() + "\nNombre: " + UD.getInstance()
                        .get(UD.SECTION_SELECTED_NAME_).toString()
        );
        Appboy.getInstance(UD.getInstance().getContext()).getCurrentUser().addToCustomAttributeArray(
                "Restaurante_Add_Carrito:", "ID:" + UD.getInstance()
                        .get(UD.RESTAURANT_).toString() + "\nNombre: " + UD.getInstance()
                        .get(UD.RESTAURANT_NAME_).toString()
        );


    }

}
