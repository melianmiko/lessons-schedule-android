package ml.mhbrgn.LessonsSchedule;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

class ViewTableAdapter extends RecyclerView.Adapter<ViewTableAdapter.ViewHolder> {
    private final LessonsTableItem[] mData;
    int active;

    ViewTableAdapter(LessonsTableItem[] data, Context context) {
        mData = data;
        active = LessonsStatusProvider.getCurrentLesson(context);
    }

    class ViewHolder extends  RecyclerView.ViewHolder {
        final TextView nameBox; final TextView numberBox; final View root;
        ViewHolder(View v) {
            super(v);
            root = v;
            nameBox = v.findViewById(R.id.name_text);
            numberBox = v.findViewById(R.id.num_text);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.main_list_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.nameBox.setText(mData[position].lesson);
        holder.numberBox.setText(mData[position].getTime().getTablePrefix());

        if(position == active && mData[position].day == TableTools.getCurrentDay()) holder.root.findViewById(R.id.active_flag).setVisibility(View.VISIBLE);
        if(mData[position].lesson_id < 0) holder.root.setVisibility(View.INVISIBLE);
    }

    @Override
    public int getItemCount() {
        return mData.length;
    }

}
