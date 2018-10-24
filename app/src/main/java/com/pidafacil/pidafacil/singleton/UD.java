package com.pidafacil.pidafacil.singleton;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.pidafacil.pidafacil.beans.CategoryBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by victor on 02-11-15.
 */
public class UD {

    public static int VIEW_ = 0;
    public static String CURRENT_USER_RESTAURANT_REQUEST_ = "currentRestaurantRequest";
    public static String USER = "userTest";
    public static String CATEGORY_ = "cat_id";
    public static String DEEP_LINK_ = "";
    public static String RESTAURANT_ = "restaurant_id";
    public static String PRODUCT_ = "product_id";
    public static String SECTION_ = "section_id";
    public static String PRODUCT_NAME_ = "product_name_";
    public static String RESTAURANT_NAME_ = "restaurant_name_";
    public static String SECTION_SELECTED_NAME_ = "section_selected_name_";
    public static String NUM_ATENCION_CLIENTE = "";
    public static String TAG_ID_ = "tag_id_";
    public static String TAG_SELECTED_NAME_ = "tag_selected_name_";

    public static String DIALOG_ = "dialog_";
    public static String DIALOG_VALUE = "dialog_value_";
    public static String DIALOG_END_METHOD = "dialog_end_method_";
    public static String DIALOG_LIST_POSITION = "dialog_list_position_";
    public static String ENCRIPTAR_AMEX_ = "encriptar_amex_";
    public static String DATA_SEL_ADDR;
    public static String DATA_SEL_PAYM;
    public static String DATA_SEL_TSERV;
    public static String PAYMTH_DETAILS;
    public static String GENERAL_CONF_ = "id_conf_general_options";
    public static String ZONE_SHIPPING_ = "com_zones";
    public static int SEL_ADDR = -1;
    public static int SEL_TSER = -1;
    public static int TYPE_VIEW = 0;
    public static String DATA_IMG_RESOURCE_PROFILE = "profile_img_resource_";
    public static String DATA_USER_SOCIAL_NET = "facebook_data_user_";
    private static UD UD;
    private HashMap<String, List<?> > mappingValues;
    private HashMap<String, Object > mappingObject;
    private HashMap<String, byte[] > byteCache;
    public static List<CategoryBean> categories;


    /* static{
         categories = new ArrayList<>();
         categories.add(new CategoryBean(1, "Carnes",1));
         categories.add(new CategoryBean(2, "China",1));
         categories.add(new CategoryBean(3, "Ensalada",1));
         categories.add(new CategoryBean(4, "Hamburguesa",1));
         categories.add(new CategoryBean(5, "Mariscos",1));
         categories.add(new CategoryBean(6, "Mexicano",1));
         categories.add(new CategoryBean(7, "Pasta",1));
         categories.add(new CategoryBean(8, "Pizza",1));
         categories.add(new CategoryBean(9, "Pollo",1));
         categories.add(new CategoryBean(11, "Sandwich",1));
         categories.add(new CategoryBean(12, "Tipica",1));
         categories.add(new CategoryBean(13, "Internacional",1));
         categories.add(new CategoryBean(14, "Postres",1));
         categories.add(new CategoryBean(16, "Café",1));
         categories.add(new CategoryBean(81, "Bebidas",1));
         categories.add(new CategoryBean(59, "Ahorro",2));
         categories.add(new CategoryBean(60, "Amigos",2));
         categories.add(new CategoryBean(61, "Cumpleaños",2));
         categories.add(new CategoryBean(62, "Deportes",2));
         categories.add(new CategoryBean(63, "Ejecutivo",2));
         categories.add(new CategoryBean(65, "Familiar",2));
         categories.add(new CategoryBean(66, "Futbol",2));
         categories.add(new CategoryBean(67, "Infantil",2));
         categories.add(new CategoryBean(68, "Light",2));
         categories.add(new CategoryBean(69, "Romántico",2));
         categories.add(new CategoryBean(70, "Temporada",2));
         categories.add(new CategoryBean(71, "Fiesta",2));
     }
 */
    private Object currentActivity;
    private String currentFragment;
    private Context context;

    public UD(){
        this.mappingValues = new HashMap<>();
        this.mappingObject = new HashMap<>();
        this.byteCache = new HashMap<>();
    }

    public static UD getInstance(){
        if(UD == null){
            UD = new UD();
            UD.put(CURRENT_USER_RESTAURANT_REQUEST_, null);
        }
        return UD;
    }

    public Object getCurrentActivity() {
        return currentActivity;
    }

    public void setCurrentActivity(Object currentActivity) {
        this.currentActivity = currentActivity;
    }

    public String getCurrentFragment() {
        return currentFragment;
    }

    public void setCurrentFragment(String currentFragment) {
        this.currentFragment = currentFragment;
    }

    public void put(String key, Object object){
        this.mappingObject.put(key,object);
    }

    public Object get(String key){
        return this.mappingObject.get(key);
    }

    public void putInCache(String key, byte[] bytes){
        this.byteCache.put(key,bytes);
    }

    public Object getFromCache(String key){
        return this.byteCache.get(key);
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }
    public static List<CategoryBean> getCategories() {
        return categories;
    }

    public static void setCategories(List<CategoryBean> categories) {
        com.pidafacil.pidafacil.singleton.UD.categories = categories;
    }


    public boolean fbSessionIsOpen(){
        if(!FacebookSdk.isInitialized())
            FacebookSdk.sdkInitialize(getContext());
        AccessToken token = AccessToken.getCurrentAccessToken();

        if(token!=null){
            Log.d("INFO", " FB token " + token.getToken());
            Log.d("INFO"," FB UserId "+token.getUserId());
            return true;
        }else{
            Log.d("INFO"," FB session is closed");
            return false;
        }
    }


}