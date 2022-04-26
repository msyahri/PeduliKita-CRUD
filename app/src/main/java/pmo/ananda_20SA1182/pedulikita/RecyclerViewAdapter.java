package pmo.ananda_20SA1182.pedulikita;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> implements Filterable {


    private ArrayList<data_penyumbang> listPenyumbang;
    private ArrayList<data_penyumbang> listPenyumbangSearch;
    private Context context;

    public RecyclerViewAdapter(ArrayList<data_penyumbang> listPenyumbang, Context context) {
        this.listPenyumbang = listPenyumbang;
        this.listPenyumbangSearch = listPenyumbang;
        this.context = context;
        listener = (ListDataActivity)context;
    }

    @Override
    public Filter getFilter() {
        return pencarian;
    }

    private Filter pencarian = new Filter(){
        @Override
        protected FilterResults performFiltering(CharSequence constraint){
            ArrayList<data_penyumbang> FilteredList = new ArrayList<>();
            if (constraint == null || constraint.length() == 0){
                FilteredList.addAll(listPenyumbangSearch);
            }else{
                String FilterPattern = constraint.toString().toLowerCase().trim();
                for(data_penyumbang item : listPenyumbangSearch){
                    if (item.getNama().toLowerCase().contains(FilterPattern)){
                        FilteredList.add(item);
                    }

                }
            }
            FilterResults results = new FilterResults();
            results.values = FilteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            listPenyumbang.clear();
            listPenyumbang.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };

    public interface dataListener{
        void onDeleteData(data_penyumbang data,int position);
    }
    dataListener listener;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View V = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_design,parent,false);
        return new ViewHolder(V);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        //mengambil nilai/value yang terdapat pdada recycleview berdasarkan posisi tertentu

        final String Nama = listPenyumbang.get(position).getNama();
        final String Bank = listPenyumbang.get(position).getBank();
        final String Nope = listPenyumbang.get(position).getNope();
        final String Email = listPenyumbang.get(position).getEmail();
        final String Kategori = listPenyumbang.get(position).getKategori();
        final String JenisKelamin = listPenyumbang.get(position).getJeniskelamin();
        final String Gambar = listPenyumbang.get(position).getGambar();

        //memasukan nilai kedalam view

        holder.Nama.setText     (": "+Nama);
        holder.Email.setText    (": "+Email);
        holder.Nope.setText     (": "+ Nope);
        holder.Kategori.setText(": "+Kategori);
        holder.jk.setText (": "+JenisKelamin);
        holder.Bank.setText (": "+Bank);
        if(Gambar==null) {
            holder.Gambar.setImageResource(R.drawable.ic_person);
        } else {
            Glide.with(holder.itemView.getContext())
                    .load(Gambar.trim())
                    .into(holder.Gambar);
        }

        holder.ListItem.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                final  String[] action = {"Update Data","Hapus Data"};
                AlertDialog.Builder alert = new AlertDialog.Builder(v.getContext());
                alert.setTitle("Pilih");
                alert.setItems(action, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        switch (i){
                            case 0:
                            /*
                            berpindah activity pada halaman layout Update Data
                            dan mengambil data pada listmahasiswa, berdasarkan posisinya
                            untuk dikirim pada activity selanjutnya
                             */

                                Bundle bundle = new Bundle();

                                bundle.putString("dataNama", listPenyumbang.get(position).getNama());
                                bundle.putString("dataBank", listPenyumbang.get(position).getBank());
                                bundle.putString("dataNope", listPenyumbang.get(position).getNope());
                                bundle.putString("dataEmail", listPenyumbang.get(position).getEmail());
                                bundle.putString("dataKategori", listPenyumbang.get(position).getKategori());
                                bundle.putString("dataJeniskelamin", listPenyumbang.get(position).getJeniskelamin());
                                bundle.putString("getPrimaryKey", listPenyumbang.get(position).getKey());
                                bundle.putString("dataGambar", listPenyumbang.get(position).getGambar());
                                Intent intent = new Intent(v.getContext(), ActivityUpdate.class);
                                intent.putExtras(bundle);
                                context.startActivity(intent);

                                break;

                            case 1:
                                AlertDialog.Builder konfirm = new AlertDialog.Builder(v.getContext());
                                konfirm.setTitle("Konfirmasi");
                                konfirm.setMessage("Anda yakin untuk menghapus?");

                                konfirm.setNegativeButton("Batal", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                });

                                konfirm.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        listener.onDeleteData(listPenyumbang.get(position),position);
                                    }
                                });
                                konfirm.show();
                                break;
                        }
                    }
                });
                alert.create();
                alert.show();
                return true;
            }
        });

    }

    @Override
    public int getItemCount() {
        return listPenyumbang.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {


        private TextView Nama, Bank,Nope,Email,jk,Kategori;
        private ImageView Gambar;
        private LinearLayout ListItem;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            Nama =itemView.findViewById(R.id.nama);
            Bank=itemView.findViewById(R.id.Bank);
            Nope=itemView.findViewById(R.id.nope);
            Email=itemView.findViewById(R.id.email);
            Kategori = itemView.findViewById(R.id.Kategori);
            jk=itemView.findViewById(R.id.jk);
            Gambar = itemView.findViewById(R.id.Gambar);
            //  darah=itemView.findViewById(R.id.)

            ListItem = itemView.findViewById(R.id.list_item);
        }
    }
}