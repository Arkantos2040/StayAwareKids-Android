package cl.m4uro.stayawarekids.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.StrictMode;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import cl.m4uro.stayawarekids.Model.ChildrenModel;
import cl.m4uro.stayawarekids.R;

/**
 * Created by m4uro on 07-03-18.
 */

public class ChildrenAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<ChildrenModel> models;

    public ChildrenAdapter(Context context, ArrayList<ChildrenModel> models){
        this.context = context;
        this.models = models;
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);
    }

    @Override
    public int getCount() {
        return models.size();
    }

    @Override
    public Object getItem(int i) {
        return models.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        if (view == null){
            view = View.inflate(context, R.layout.list_view_children, null);
        }

        ImageView images = (ImageView) view.findViewById(R.id.imageView_avatar);
        TextView nombreApellido = (TextView) view.findViewById(R.id.TextView_childrenName);
        TextView rut = (TextView) view.findViewById(R.id.TextView_rut);
        Bitmap bitmap = null;
        ChildrenModel childrenModel = models.get(i);
        try ( InputStream is = new URL(childrenModel.getUrlAvatar()).openStream() ) {
            bitmap = BitmapFactory.decodeStream( is );
            images.setImageBitmap(bitmap);
        } catch (MalformedURLException e) {
            images.setImageResource(R.mipmap.avatar);
        } catch (IOException e) {
            images.setImageResource(R.mipmap.avatar);
        }
        nombreApellido.setText(childrenModel.getNombreApellido());
        rut.setText(childrenModel.getRut());

        return view;
    }
}
