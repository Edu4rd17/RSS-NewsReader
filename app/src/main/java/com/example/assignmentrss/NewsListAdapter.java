package com.example.assignmentrss;

/**
 * @author Eduard Iacob
 */

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class NewsListAdapter extends RecyclerView.Adapter<NewsListAdapter.ViewHolder> {

    //create a list of Item objects
    private List<Item> newsItems;

    // RecyclerView recyclerView;
    public NewsListAdapter(List<Item> listdata) {
        this.newsItems = listdata;
    }

    @NonNull
    @Override
    public NewsListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.list_item, parent, false);
        //create a view holder object
        ViewHolder viewHolder = new ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull NewsListAdapter.ViewHolder holder, int position) {
        //get the current item
        final Item myListData = newsItems.get(position);
        //set the text of the item
        holder.textView.setText(myListData.getTitle());
        //set the image of the item using Picasso
        Picasso.get().load(myListData.enclosure).into(holder.imageView);
        //set the onClickListener for the item
        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Toast.makeText(view.getContext(), "click on item: " + myListData.getLink(), Toast.LENGTH_LONG).show();
                //create an intent to open the web view activity when clicking on a certain item from the list
                Intent intent = new Intent(holder.itemView.getContext(), WebViewActivity.class);
                //pass the link of the item to the web view activity
                intent.putExtra("URL", myListData.getLink());
                //start the web view activity
                holder.itemView.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return newsItems.size();
    }

    //create a view holder class
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public TextView textView;
        public RelativeLayout relativeLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            //get the reference of the views
            this.imageView = itemView.findViewById(R.id.imageView);
            this.textView = itemView.findViewById(R.id.textView);
            relativeLayout = itemView.findViewById(R.id.relativeLayout);
        }
    }
}
