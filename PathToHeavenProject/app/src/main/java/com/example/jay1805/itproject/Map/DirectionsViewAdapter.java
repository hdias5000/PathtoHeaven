package com.example.jay1805.itproject.Map;

import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.jay1805.itproject.R;

import java.util.ArrayList;
import java.util.HashMap;

public class DirectionsViewAdapter extends RecyclerView.Adapter<DirectionsViewAdapter.DirectionsViewHolder> {

    private ArrayList<HashMap> stepInformation;

    public DirectionsViewAdapter(ArrayList<HashMap> stepInformation) {
        this.stepInformation = stepInformation;
    }

    @NonNull
    @Override
    public DirectionsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_directions, null, false);
        DirectionsViewAdapter.DirectionsViewHolder rcv = new DirectionsViewAdapter.DirectionsViewHolder(layoutView);
        return rcv;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onBindViewHolder(@NonNull DirectionsViewHolder holder, int position) {
//        R.drawable.direction_turn_left;
//        String str = stepInformation.get(position).get("Maneuver").toString().replaceAll("-","_");
//        Uri otherPath = Uri.parse("android.resource://"+R.class.getPackage().getName()+"/" +"res/drawable/direction_close.png");

//        File imgFile = new File(otherPath.toString());
//        Log.d("Path","./drawable/direction_"+str+".png");

//        String drawable = "direction_turn_left.png";
//        AppCompatActivity activity = new AppCompatActivity();
        System.out.println("Hasitha is a shit: " + Integer.parseInt((String) stepInformation.get(position).get("manRes")));
//        int resID = ((AppCompatActivity)activity).getResources().getIdentifier(drawable, "drawable", "com.example.jay1805.itproject");
        holder.maneuver.setImageResource(Integer.parseInt((String) stepInformation.get(position).get("manRes")));
        holder.maneuver.setBackgroundResource(R.color.white);
//        holder.maneuver.set

//        if(imgFile.exists()){
//            Log.d("Path","IN");
//
//            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
//
//            holder.maneuver.setImageBitmap(myBitmap);
//
//        }

        holder.instruction.setText(Html.fromHtml((String) stepInformation.get(position).get("Instructions"),Html.FROM_HTML_MODE_LEGACY).toString());
    }

    @Override
    public int getItemCount() {
        return stepInformation.size();
    }

    public class DirectionsViewHolder extends RecyclerView.ViewHolder {
        public TextView instruction;
        public ImageView maneuver;
        public DirectionsViewHolder(View view) {
            super(view);
            instruction = view.findViewById(R.id.instructions);
            maneuver = view.findViewById(R.id.maneuver_icon);
        }
    }
}
