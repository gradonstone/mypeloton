package edu.fsu.cs.mobile.mypeloton;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class DisplayRequestAdapter extends ArrayAdapter<DisplayRequest> {
    private Context context;
    private ArrayList<DisplayRequest> items;

    public DisplayRequestAdapter(Context context, int resource){
        super(context, resource);
        this.context = context;
        items = new ArrayList<>();
    }

    private static class DisplayRequestHolder{
        TextView emailDisplay;
        TextView userDisplay;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent){
        final DisplayRequest item = getItem(position);
        DisplayRequestHolder displayHolder;
        if(view == null)
        {
            displayHolder = new DisplayRequestHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            view = inflater.inflate(R.layout.request_item, parent, false);
            displayHolder.emailDisplay = view.findViewById(R.id.display_email);
            displayHolder.userDisplay = view.findViewById(R.id.display_user);
            view.setTag(displayHolder);
        }
        else
            displayHolder = (DisplayRequestHolder) view.getTag();

        displayHolder.emailDisplay.setText(item.getEmail());
        displayHolder.userDisplay.setText(item.getUserID());
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        return view;
    }
}
