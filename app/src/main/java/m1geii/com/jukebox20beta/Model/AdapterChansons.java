package m1geii.com.jukebox20beta.Model;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import m1geii.com.jukebox20beta.R;

public class AdapterChansons extends BaseAdapter{

    private ArrayList<Chanson> chansons;
    private LayoutInflater songInf;

    public AdapterChansons(Context c, ArrayList<Chanson> lesChansons){
        chansons=lesChansons;
        songInf=LayoutInflater.from(c);
    }

    @Override
    public int getCount() {
        return chansons.size();
    }

    @Override
    public Object getItem(int position) {
        return chansons.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    static class ViewHolder{
        TextView titreChanson;
        TextView artisteChanson;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder=new ViewHolder();

        if(convertView==null){
            convertView=songInf.inflate(R.layout.chansons, parent, false);
            holder.titreChanson = (TextView)convertView.findViewById(R.id.song_title);
            holder.artisteChanson = (TextView)convertView.findViewById(R.id.song_artist);
            convertView.setTag(holder);
        }

        else {
            holder=(ViewHolder)convertView.getTag();
        }

        Chanson chansonCourrante = chansons.get(position);
        holder.titreChanson.setText(chansonCourrante.getTitle());
        holder.artisteChanson.setText(chansonCourrante.getArtist());

        return convertView;
    }


}
