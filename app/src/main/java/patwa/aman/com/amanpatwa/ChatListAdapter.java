package patwa.aman.com.amanpatwa;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ChatListAdapter extends BaseAdapter {

    private Activity mActivity;
    private DatabaseReference mDataBaseReference;
    private String mDisplayName;
    private ArrayList<DataSnapshot> mSnapShotList;
    private String mtype,type,messtype;
    String msg;



    ChildEventListener listener = new ChildEventListener() {
        @Override
        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            mSnapShotList.add(dataSnapshot);
            notifyDataSetChanged();
        }

        @Override
        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

        }

        @Override
        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

        }

        @Override
        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };


    public ChatListAdapter(Activity activity, DatabaseReference ref, String name, String type){
        mActivity = activity;
        mDataBaseReference = ref.child("messages");
        mDisplayName = name;
        mDataBaseReference.addChildEventListener(listener);
        mSnapShotList = new ArrayList<>();
        mtype=type;

        mDataBaseReference.keepSynced(true);
    }


    static class ViewHolder{
        TextView authorName;
        TextView body;
        ImageView imagemess;
        LinearLayout.LayoutParams params;
        LinearLayout.LayoutParams params2,imageparams;
    }


    @Override
    public int getCount() {
        return mSnapShotList.size();
    }

    @Override
    public InstantMessage getItem(int position) {
        DataSnapshot snapshot = mSnapShotList.get(position);
        return snapshot.getValue(InstantMessage.class);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View ConvertView, ViewGroup parent) {
        if(ConvertView == null){
            LayoutInflater inflater = (LayoutInflater)mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            ConvertView = inflater.inflate(R.layout.chat_msg_row, parent, false);

            final ViewHolder holder = new ViewHolder();
            holder.authorName = (TextView) ConvertView.findViewById(R.id.author);
            holder.body = (TextView) ConvertView.findViewById(R.id.message);
            holder.imagemess=(ImageView) ConvertView.findViewById(R.id.chat_image);
            holder.params = (LinearLayout.LayoutParams)holder.authorName.getLayoutParams();
            holder.params2= (LinearLayout.LayoutParams)holder.body.getLayoutParams();
            holder.imageparams=(LinearLayout.LayoutParams)holder.imagemess.getLayoutParams();

            ConvertView.setTag(holder);
        }

        final InstantMessage message = getItem(position);
        final ViewHolder holder = (ViewHolder)ConvertView.getTag();
        type=message.getMtype();
        messtype = message.getMesstype();

        boolean isMe = message.getAuthor().equals(mDisplayName);
        setChatRowAppearance(isMe,holder,type,messtype);


        String author = message.getAuthor();
        holder.authorName.setText(author);

        msg= message.getMessage();

        if(messtype.equals("image")){
            holder.body.setVisibility(View.INVISIBLE);

            Picasso.with(mActivity).load(msg)
                    .networkPolicy(NetworkPolicy.OFFLINE)
                    .fit()
                    .centerCrop()
                    .into(holder.imagemess, new Callback() {
                        @Override
                        public void onSuccess() {
                            Log.d("OnSuccess","picasso");
                        }

                        @Override
                        public void onError() {
                            Log.d("OnError","picasso");
                            Picasso.with(mActivity).load(msg)
                                    .placeholder(R.drawable.ic_launcher_background)
                                    .fit()
                                    .centerCrop()
                                    .into(holder.imagemess);
                        }
                    });
        }

        else if(messtype.equals("text")) {
            holder.imagemess.setVisibility(View.INVISIBLE);
            holder.body.setText(msg);

        }
        return ConvertView;
    }

    private void setChatRowAppearance(boolean isItMe, ViewHolder holder, String type, String messtype){
        System.out.println("Type:"+type);

        if (type.equals("Admin")) {
            holder.params.gravity = Gravity.CENTER;
            holder.params2.gravity = Gravity.CENTER;
            holder.imageparams.gravity = Gravity.CENTER;
            holder.authorName.setTextColor(Color.BLACK);
            holder.body.setBackgroundColor(Color.WHITE);
        } else if (isItMe) {
            holder.params.gravity = Gravity.END;
            holder.params2.gravity = Gravity.END;
            holder.imageparams.gravity = Gravity.END;
            holder.authorName.setTextColor(Color.GREEN);
            holder.body.setBackgroundResource(R.drawable.bubble2);
        } else {
            holder.params.gravity = Gravity.START;
            holder.params2.gravity = Gravity.START;
            holder.imageparams.gravity = Gravity.START;
            holder.authorName.setTextColor(Color.BLUE);
            holder.body.setBackgroundResource(R.drawable.bubble1);
        }
    }

    public void cleanUp(){
        mDataBaseReference.removeEventListener(listener);
    }


}

