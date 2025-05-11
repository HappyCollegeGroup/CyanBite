package fcu.app.cyanbite.adapter;

import static fcu.app.cyanbite.util.Util.open;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import fcu.app.cyanbite.R;
import fcu.app.cyanbite.model.Group;
import fcu.app.cyanbite.ui.OrderGroupActivity;

public class OrderGroupListAdapter extends RecyclerView.Adapter<OrderGroupListAdapter.ViewHolder> {
    private Context context;
    private List<Group> groupList;
    private boolean isHorizontal;

    public OrderGroupListAdapter(Context context, List<Group> groupList) {
        this.context = context;
        this.groupList = groupList;
        this.isHorizontal = false;
    }

    public OrderGroupListAdapter(Context context, List<Group> groupList, boolean isHorizontal) {
        this.context = context;
        this.groupList = groupList;
        this.isHorizontal = true;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (isHorizontal) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_order_group_small, parent, false);
            return new ViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_order_group, parent, false);
            return new ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Group group = groupList.get(position);
        holder.imgGroup.setImageBitmap(group.getImage());
        holder.tvName.setText(group.getName());;
        holder.tvDescription.setText(group.getDescription());;
        holder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(context, OrderGroupActivity.class);
            intent.putExtra("name", group.getName());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return groupList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgGroup;
        TextView tvName;
        TextView tvDescription;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgGroup = itemView.findViewById(R.id.img_group);
            tvName = itemView.findViewById(R.id.tv_name);
            tvDescription = itemView.findViewById(R.id.tv_description);
        }
    }
}
