package cl.m4uro.stayawarekids;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Estudiante on 09/02/2018.
 */
//parte de efficient home se implementa los estados de los sensores si estan conectados o no donde si se apretan se muestra el grafico
public class SensorAdapter extends BaseAdapter implements ListAdapter {
    private ArrayList<String> list = new ArrayList<String>();
    private Context context;
    private static boolean valido;
    private String id;

    public SensorAdapter(ArrayList<String> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int pos) {
        return list.get(pos);
    }

    @Override
    public long getItemId(int pos) { return pos; }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.sensor_items, null);
        }

        TextView listItemText = (TextView)view.findViewById(R.id.txtSensores);
        listItemText.setText(list.get(position));

        ImageButton imageItem = (ImageButton)view.findViewById(R.id.btnSensor);

        String sensores[] = listItemText.getText().toString().split("-");

        id = sensores[0].trim();

        valido = true;
        if (listItemText.getText().toString().indexOf("-99") > 0){
            valido = false;
        }

        if (valido){
            imageItem.setBackgroundResource(android.R.color.holo_green_dark);
        } else {
            imageItem.setBackgroundResource(android.R.color.holo_red_dark);
        }


        notifyDataSetChanged();

        return view;
    }
}

