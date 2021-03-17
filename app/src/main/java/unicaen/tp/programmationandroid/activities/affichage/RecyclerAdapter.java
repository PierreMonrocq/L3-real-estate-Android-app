package unicaen.tp.programmationandroid.activities.affichage;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import unicaen.tp.programmationandroid.R;
import unicaen.tp.programmationandroid.activities.listeners.ClickListener;
import unicaen.tp.programmationandroid.classes.Propriete;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerViewHolder> {

    private final ClickListener listener;
    List<Propriete> proprietes;
    Activity activity;

    public RecyclerAdapter(List<Propriete> proprietes, ClickListener listener, Activity activity) {
        this.proprietes = proprietes;
        this.listener = listener;
        this.activity = activity;
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int index) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.activity_liste_items, viewGroup, false);
        RecyclerViewHolder result = new RecyclerViewHolder(view, activity,listener);
        return result;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder viewHolder, int index) {
        viewHolder.bind(proprietes.get(index));
    }

    @Override
    public int getItemCount() {
        return proprietes.size();
    }

}