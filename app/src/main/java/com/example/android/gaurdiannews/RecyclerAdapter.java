package com.example.android.gaurdiannews;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.RecycleHolder> {

    private List<News> newsData;
    private LayoutInflater inflater;
    private ItemClickCallback itemClickCallback;

    public interface ItemClickCallback{
        void onItemClick (int p);
    }

    public void setItemClickCallback (ItemClickCallback itemClickCallback){
        this.itemClickCallback = itemClickCallback;
    }

    public RecyclerAdapter(List<News> newsData, Context context){
        this.inflater = LayoutInflater.from(context);
        this.newsData = newsData;
    }

    @Override
    public RecycleHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.list_item, parent, false);
        return new RecycleHolder(view);
    }

    @Override
    public void onBindViewHolder(RecycleHolder holder, int position) {
        News currentNews = newsData.get(position);
        holder.section.setText(currentNews.getSection());
        holder.date.setText(currentNews.getDate().substring(0,10));
        holder.title.setText(currentNews.getTitle());
    }

    @Override
    public int getItemCount() {
        return newsData.size();
    }

    public News getItem(int position){
        return newsData.get(position);
    }

    public void addAll(List<News> datas){
        newsData.clear();
        newsData.addAll(datas);
        notifyDataSetChanged();
    }

    public void clear(){
        newsData.clear();
    }

    class RecycleHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private TextView title;
        private TextView section;
        private TextView date;
        private View container;

        public RecycleHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
            section = (TextView) itemView.findViewById(R.id.section);
            date = (TextView) itemView.findViewById(R.id.date);
            container = itemView.findViewById(R.id.container);
            container.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            itemClickCallback.onItemClick(getAdapterPosition());
        }
    }
}
