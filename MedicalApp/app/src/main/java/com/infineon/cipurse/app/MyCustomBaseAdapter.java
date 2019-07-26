package com.infineon.cipurse.app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.infineon.cipurse.app.PrescriptionDetails;
import com.infineon.cipurse.app.R;

import java.util.ArrayList;

public class MyCustomBaseAdapter extends BaseAdapter {
    private static ArrayList<PrescriptionDetails> prescriptionDetailsList;

    private LayoutInflater mInflater;

    public MyCustomBaseAdapter(Context context, ArrayList<PrescriptionDetails> results) {
        prescriptionDetailsList = results;
        mInflater = LayoutInflater.from(context);
    }

    public int getCount() {
        return prescriptionDetailsList.size();
    }

    public Object getItem(int position) {
        return prescriptionDetailsList.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.custom_row_view, null);
            holder = new ViewHolder();
            holder.txtName = (TextView) convertView.findViewById(R.id.medicationname);
            holder.txtDate = (TextView) convertView.findViewById(R.id.medicationdate);
            holder.txtRefills = (TextView) convertView.findViewById(R.id.medicationrefills);
            holder.txtDirections = (TextView) convertView.findViewById(R.id.medicationdirections);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.txtName.setText(prescriptionDetailsList.get(position).getPrescriptionName());
        holder.txtDate.setText(prescriptionDetailsList.get(position).getPrescriptionDate());
        holder.txtRefills.setText(prescriptionDetailsList.get(position).getprescriptionRefills());
        holder.txtDirections.setText(prescriptionDetailsList.get(position).getPrescriptionDirections());

        return convertView;
    }

    static class ViewHolder {
        TextView txtName;
        TextView txtDate;
        TextView txtRefills;
        TextView txtDirections;
    }
}