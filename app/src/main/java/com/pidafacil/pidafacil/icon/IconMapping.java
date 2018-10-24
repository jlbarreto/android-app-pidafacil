package com.pidafacil.pidafacil.icon;

import com.pidafacil.pidafacil.R;
import com.pidafacil.pidafacil.beans.CategoryBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by victor on 03-03-15.
 */
public class IconMapping {
    static List<CategoryIcon> beans = new ArrayList<CategoryIcon>();

    static{
        beans.add(new CategoryIcon(R.drawable.carnes,1));
        beans.add(new CategoryIcon(R.drawable.chino,2));
        beans.add(new CategoryIcon(R.drawable.ensalada,3));
        beans.add(new CategoryIcon(R.drawable.burger,4));
        beans.add(new CategoryIcon(R.drawable.mariscos,5));
        beans.add(new CategoryIcon(R.drawable.mexicano,6));
        beans.add(new CategoryIcon(R.drawable.pastas,7));
        beans.add(new CategoryIcon(R.drawable.pizza,8));
        beans.add(new CategoryIcon(R.drawable.pollo,9));
        beans.add(new CategoryIcon(R.drawable.sandwich,11));
        beans.add(new CategoryIcon(R.drawable.tipicos,12));
        beans.add(new CategoryIcon(R.drawable.internacional,13));
        beans.add(new CategoryIcon(R.drawable.pastel,14));
        beans.add(new CategoryIcon(R.drawable.cafe,16));
        beans.add(new CategoryIcon(R.drawable.bebida,81));

        beans.add(new CategoryIcon(R.drawable.ahorro,59));
        beans.add(new CategoryIcon(R.drawable.amigos,60));
        beans.add(new CategoryIcon(R.drawable.cumpleanios,61));
        beans.add(new CategoryIcon(R.drawable.deportes,62));
        beans.add(new CategoryIcon(R.drawable.ejecutivo,63));
        beans.add(new CategoryIcon(R.drawable.familiar,65));
        beans.add(new CategoryIcon(R.drawable.football,66));
        beans.add(new CategoryIcon(R.drawable.infantil,67));
        beans.add(new CategoryIcon(R.drawable.light,68));
        beans.add(new CategoryIcon(R.drawable.romance,69));
        beans.add(new CategoryIcon(R.drawable.temporada,70));
        beans.add(new CategoryIcon(R.drawable.fiesta,71));
    }

    public static List<CategoryIcon> getCategoryIcons(List<CategoryBean> list){
        List<CategoryIcon> rbeans = new ArrayList<CategoryIcon>();

        for( CategoryBean bean : list){
            for(int i = 0; i < beans.size() ; i++){
                if(bean.getTagId() == beans.get(i).getId()){
                    rbeans.add(beans.get(i));
                }
            }
        }

        return rbeans;
    }

}
