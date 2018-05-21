package ml.mhbrgn.schooljournal;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

class ViewTableAdapter extends RecyclerView.Adapter<ViewTableAdapter.ViewHolder> {
    private LessonsTableItem[] mData;

    ViewTableAdapter(LessonsTableItem[] data) {mData = data;}

    class ViewHolder extends  RecyclerView.ViewHolder {
        TextView nameBox; TextView numberBox; View root;
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
        if(mData[position].lesson_id < 1) ((ViewGroup)holder.root).removeAllViews();
    }

    @Override
    public int getItemCount() {
        return mData.length;
    }

}
