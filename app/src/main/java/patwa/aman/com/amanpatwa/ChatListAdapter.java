package patwa.aman.com.amanpatwa;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

public class ChatListAdapter extends BaseAdapter {

    private Activity mActivity;
    private DatabaseReference mDataBaseReference;
    private String mDisplayName;
    private ArrayList<DataSnapshot> mSnapShotList;
    private String mtype;

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
    }


    static class ViewHolder{
        TextView authorName;
        TextView body;
        LinearLayout.LayoutParams params;
    }


    @Override
    public int getCount() {
        Log.d("PubChat","getCount() called");
        return mSnapShotList.size();
    }

    @Override
    public InstantMessage getItem(int position) {
        DataSnapshot snapshot = mSnapShotList.get(position);
        Log.d("PubChat","getItem() called");
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
            Log.d("PubChat","Data stored in ConvertView");

            final ViewHolder holder = new ViewHolder();
            holder.authorName = (TextView) ConvertView.findViewById(R.id.author);
            holder.body = (TextView) ConvertView.findViewById(R.id.message);
            holder.params = (LinearLayout.LayoutParams)holder.authorName.getLayoutParams();
            ConvertView.setTag(holder);
        }
        final InstantMessage message = getItem(position);
        final ViewHolder holder = (ViewHolder)ConvertView.getTag();

        boolean isMe = message.getAuthor().equals(mDisplayName);
        setChatRowAppearance(isMe,holder);

        String author = message.getAuthor();
        holder.authorName.setText(author);

        String msg = message.getMessage();
        holder.body.setText(msg);
        Log.d("PubChat","getView() executed");

        return ConvertView;
    }

    private void setChatRowAppearance(boolean isItMe, ViewHolder holder){
        if(mtype.equals("Admin")){
            holder.params.gravity = Gravity.CENTER;
            holder.authorName.setTextColor(Color.BLACK);
        }
        if(isItMe){
            holder.params.gravity = Gravity.END;
            holder.authorName.setTextColor(Color.GREEN);
            //holder.body.setBackgroundResource(R.drawable.bubble2);
        }else{
            holder.params.gravity = Gravity.START;
            holder.authorName.setTextColor(Color.BLUE);
            // holder.body.setBackgroundResource(R.drawable.bubble1);
        }
    }

    public void cleanUp(){
        mDataBaseReference.removeEventListener(listener);
        Log.d("PubChat","cleanUp() called");
    }


}

