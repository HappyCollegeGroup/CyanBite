package fcu.app.cyanbite.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import fcu.app.cyanbite.R;
import fcu.app.cyanbite.model.GroupName;

public class OrderGroupNameListAdapter extends RecyclerView.Adapter<OrderGroupNameListAdapter.ViewHolder> {
    private Context context;
    private List<GroupName> groupNameList;

    public OrderGroupNameListAdapter(Context context, List<GroupName> groupNameList) {
        this.context = context;
        this.groupNameList = groupNameList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_restaurant, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        GroupName groupName = groupNameList.get(position);
        holder.tvGroupName.setText(groupName.getGroupName());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener != null) {
                    listener.onItemClick(groupName);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return groupNameList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvGroupName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvGroupName = itemView.findViewById(R.id.tv_order_rv_group_name);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(GroupName group);
    }

    private OnItemClickListener listener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

}
