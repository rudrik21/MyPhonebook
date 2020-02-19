package com.rudrik.simplephonebook;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.tr4android.recyclerviewslideitem.SwipeAdapter;
import com.tr4android.recyclerviewslideitem.SwipeConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

public class AdptPersons extends SwipeAdapter {

    private Context context;
    private List<Person> list = new ArrayList<>();
    private PersonDatabase db;

    private Executor bg, main;

    public AdptPersons(Context context, List<Person> list) {
        this.context = context;
        this.list = list;
        db = PersonDatabase.getInstance(context);

        bg = AppExecutors.getInstance().diskIO();
        main = AppExecutors.getInstance().mainThread();
    }

    public void refreshData(){
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateSwipeViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_person, parent, true);

        return new PersonViewHolder(v);
    }

    @Override
    public void onBindSwipeViewHolder(RecyclerView.ViewHolder vh, int i) {
        if (!list.isEmpty()){
            Person p = list.get(i);

            if (vh instanceof PersonViewHolder){
                PersonViewHolder v = (PersonViewHolder) vh;
                v.tvName.setText(p.getFirstName() + " "+ p.getLastName());
                v.tvPhone.setText(p.getPhone());
                v.tvAddr.setText(p.getAddr());
            }
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public SwipeConfiguration onCreateSwipeConfiguration(Context context, int position) {
        if (!list.isEmpty()) {
            return new SwipeConfiguration.Builder(context)
                    .setRightDrawableResource(R.drawable.ic_edit)
                    .setLeftDrawableResource(R.drawable.ic_delete)
                    .setSwipeBehaviour(10, null)
                    .build();
        }

        return null;
    }

    @Override
    public void onSwipe(final int position, int direction) {
        if (!list.isEmpty()) {
            if (direction == -1) {
                //  delete
                System.out.println("delete");
                bg.execute(new Runnable() {
                    @Override
                    public void run() {
                        int rows = db.personDao().deletePerson(list.get(position));

                        if (rows > 0){
                            main.execute(new Runnable() {
                                @Override
                                public void run() {
                                    list.remove(position);
                                    notifyDataSetChanged();
                                }
                            });
                        }
                    }
                });

                refreshData();
            }

            if (direction == 1) {
                //  edit
                refreshData();
//                Intent i = new Intent(context, MapsActivity.class);
//                String strPlace = new Gson().toJson(myPlaces.get(position));
//                i.putExtra("PLACE", strPlace);
//                context.startActivity(i);


            }
        }
    }
}

class PersonViewHolder extends RecyclerView.ViewHolder {

    CardView cardPerson;
    TextView tvName;
    TextView tvAddr;
    TextView tvPhone;

    public PersonViewHolder(@NonNull View cv) {
        super(cv);

        cardPerson = (CardView) cv.findViewById(R.id.cardPerson);
        tvName = (TextView) cv.findViewById(R.id.tvName);
        tvAddr = (TextView) cv.findViewById(R.id.tvAddr);
        tvPhone = (TextView) cv.findViewById(R.id.tvPhone);

    }

}