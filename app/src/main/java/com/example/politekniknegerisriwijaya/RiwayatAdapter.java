package com.example.politekniknegerisriwijaya;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class RiwayatAdapter extends RecyclerView.Adapter<RiwayatAdapter.ViewHolder> {

    private List<Absensi> absensiList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Absensi absensi);
    }

    public RiwayatAdapter(List<Absensi> absensiList) {
        this.absensiList = absensiList;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_riwayat, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Absensi absensi = absensiList.get(position);

        // Set tipe absensi
        String tipeAbsensi = absensi.getTipeAbsensi().toUpperCase();
        holder.tvTipeAbsensi.setText(tipeAbsensi);

        // Set nama
        holder.tvNama.setText(absensi.getNama());

        // Format tanggal
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
            Date date = inputFormat.parse(absensi.getTanggal());
            holder.tvTanggal.setText(outputFormat.format(date));
        } catch (ParseException e) {
            holder.tvTanggal.setText(absensi.getTanggal());
        }

        // Set waktu
        holder.tvWaktu.setText(absensi.getWaktu());

        // Set lokasi
        holder.tvLokasi.setText(absensi.getLokasi());

        // Set keterangan
        holder.tvKeterangan.setText(absensi.getKeterangan());

        // Set foto jika ada
        if (absensi.getFoto() != null) {
            holder.imgFoto.setImageBitmap(absensi.getFoto());
            holder.imgFoto.setVisibility(View.VISIBLE);
        } else {
            holder.imgFoto.setVisibility(View.GONE);
        }

        // Set warna berdasarkan tipe absensi
        if (absensi.getTipeAbsensi().equals("masuk")) {
            holder.cardView.setCardBackgroundColor(holder.itemView.getContext()
                    .getResources().getColor(android.R.color.holo_green_light));
            holder.tvTipeAbsensi.setTextColor(holder.itemView.getContext()
                    .getResources().getColor(android.R.color.holo_green_dark));
        } else {
            holder.cardView.setCardBackgroundColor(holder.itemView.getContext()
                    .getResources().getColor(android.R.color.holo_red_light));
            holder.tvTipeAbsensi.setTextColor(holder.itemView.getContext()
                    .getResources().getColor(android.R.color.holo_red_dark));
        }

        // Click listener
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(absensi);
            }
        });
    }

    @Override
    public int getItemCount() {
        return absensiList.size();
    }

    public void updateData(List<Absensi> newList) {
        this.absensiList = newList;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView tvTipeAbsensi, tvNama, tvTanggal, tvWaktu, tvLokasi, tvKeterangan;
        ImageView imgFoto;

        ViewHolder(View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardView);
            tvTipeAbsensi = itemView.findViewById(R.id.tvTipeAbsensi);
            tvNama = itemView.findViewById(R.id.tvNama);
            tvTanggal = itemView.findViewById(R.id.tvTanggal);
            tvWaktu = itemView.findViewById(R.id.tvWaktu);
            tvLokasi = itemView.findViewById(R.id.tvLokasi);
            tvKeterangan = itemView.findViewById(R.id.tvKeterangan);
            imgFoto = itemView.findViewById(R.id.imgFoto);
        }
    }
}