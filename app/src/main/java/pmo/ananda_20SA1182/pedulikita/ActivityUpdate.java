package pmo.ananda_20SA1182.pedulikita;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.UUID;


public class ActivityUpdate extends AppCompatActivity {

    //deklarasi variable
    private ProgressBar progressbar;
    private EditText namaBaru,nopeBaru,emailBaru;
    private Spinner bankBaru;
    private Button update, btn_updateImg;
    private TextView kategoribaru;
    private CheckBox Sosial1, Keagamaan1;
    private DatabaseReference database;
    private ImageView updateImg;
    private String cekNama, cekBank, cekNope, cekEmail, cekJeniskelamin, cekKategori;
    private RadioButton jkBaru, new_radioPria, new_radioWanita;
    private RadioGroup rgjeniskelaminBaru;
    FirebaseStorage storage;
    private StorageReference storageReference;
    private static final int REQUEST_CODE_CAMERA = 1;
    private static final int REQUEST_CODE_GALLERY = 2;


    DatePickerDialog datePickerDialog;
    SimpleDateFormat dateFormatter;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);

        namaBaru = findViewById(R.id.new_nama);
        bankBaru= findViewById(R.id.new_bank);
        nopeBaru=findViewById(R.id.new_nope);
        emailBaru=findViewById(R.id.new_email);
        update = findViewById(R.id.update);
        kategoribaru = findViewById(R.id.new_kategori);
        new_radioPria = findViewById(R.id.new_radioPria);
        new_radioWanita = findViewById(R.id.new_radioWanita);
        Sosial1 = findViewById(R.id.new_gol1);
        Keagamaan1 = findViewById(R.id.new_gol2);
        rgjeniskelaminBaru=findViewById(R.id.new_rgjeniskelamin);
        updateImg = findViewById(R.id.updateImg);
        btn_updateImg = findViewById(R.id.btn_updateImg);
        progressbar = findViewById(R.id.progressBar);
        progressbar.setVisibility(View.GONE);

        storageReference = FirebaseStorage.getInstance().getReference();

        final String getJk = getIntent().getExtras().getString("dataJeniskelamin");
        final String getKategori = getIntent().getExtras().getString("dataKategori");
        /*Toast.makeText(this,getGoldar,Toast.LENGTH_SHORT).show();
        Toast.makeText(this,getJk,Toast.LENGTH_SHORT).show();*/
        if (getKategori.equals("Sosial")){
            Sosial1.setChecked(true);
        }else if (getKategori.equals("Keagamaan")){
            Keagamaan1.setChecked(true);
        }

        if (getJk.equals("Pria")){
            new_radioPria.setChecked(true);
        }else if (getJk.equals("Wanita")){
            new_radioWanita.setChecked(true);
        }

        database= FirebaseDatabase.getInstance().getReference();
        getData();

        btn_updateImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getImage();
            }
        });



        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int pilihjkBaru = rgjeniskelaminBaru.getCheckedRadioButtonId();
                jkBaru = findViewById(pilihjkBaru);

                cekNama = namaBaru.getText().toString();
                cekBank = bankBaru.getSelectedItem().toString();
                cekNope=nopeBaru.getText().toString();
                cekEmail=emailBaru.getText().toString();
                if (Sosial1.isChecked()) {
                    cekKategori = Sosial1.getText().toString();
                    kategoribaru.setText(cekKategori);
                    cekKategori = kategoribaru.getText().toString();
                } else {

                }
                if (Keagamaan1.isChecked()) {
                    cekKategori = Keagamaan1.getText().toString();
                    kategoribaru.setText(cekKategori);
                    cekKategori = kategoribaru.getText().toString();
                } else {

                }


//mengubah data databse menggunakan data yang baru di inputkan
                if(isEmpty(cekNama)|| isEmpty(cekNope)|| isEmpty(cekEmail)) {
                      Toast.makeText(ActivityUpdate.this, "Data tidak boleh ada yang kosong", Toast.LENGTH_SHORT).show();
                }else{
                    progressbar.setVisibility(View.VISIBLE);

                    //Get Image from imageView
                    updateImg.setDrawingCacheEnabled(true);
                    updateImg.buildDrawingCache();
                    Bitmap bitmap = ((BitmapDrawable) updateImg.getDrawable()).getBitmap();

                    ByteArrayOutputStream stream = new ByteArrayOutputStream();

                    //Proses bitmap -> JPEG
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                    byte[] bytes = stream.toByteArray();

                    //Where the Image saved
                    String namafile = UUID.randomUUID()+".jpg";
                    final String pathImage = "foto/"+namafile;
                    UploadTask uploadTask = storageReference.child(pathImage).putBytes(bytes);
                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    /*Processing Update Data
                                    Reload setter recomended to get new data from user*/

                                    data_penyumbang setPenyumbang = new data_penyumbang();
                                    setPenyumbang.setNama(namaBaru.getText().toString());
                                    setPenyumbang.setBank(bankBaru.getSelectedItem().toString());
                                    setPenyumbang.setNope(nopeBaru.getText().toString());
                                    setPenyumbang.setEmail(emailBaru.getText().toString());
                                    setPenyumbang.setJeniskelamin(jkBaru.getText().toString());
                                    setPenyumbang.setKategori(kategoribaru.getText().toString());
                                    setPenyumbang.setGambar(uri.toString().trim());
                                    updatePenyumbang(setPenyumbang);

                                }

                            });

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ActivityUpdate.this, "Update Gagal",Toast.LENGTH_SHORT).show();
                        }
                    }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                            progressbar.setVisibility(View.VISIBLE);
                            double progress = (100.0 * snapshot.getBytesTransferred())/snapshot.getTotalByteCount();
                            progressbar.setProgress((int) progress);
                        }
                    });


                }

            }
        });

        Sosial1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    Keagamaan1.setChecked(false);
                }
            }
        });

        Keagamaan1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    Sosial1.setChecked(false);
                }
            }
        });

    }

    private void getImage() {
        Intent ImageIntentGallery = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(ImageIntentGallery, REQUEST_CODE_GALLERY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case REQUEST_CODE_CAMERA:
                break;
            case REQUEST_CODE_GALLERY:
                if (resultCode == RESULT_OK){
                    try {
                        updateImg.setVisibility(View.VISIBLE);
                        Uri uri = data.getData();
                        Bitmap bitmap= MediaStore.Images.Media.getBitmap(this.getContentResolver(),uri);
                        updateImg.setImageBitmap(bitmap);

                    }catch (IOException e){
                        e.printStackTrace();
                    }
                }
                break;
        }
    }

    private boolean isEmpty(String s){
        return TextUtils.isEmpty(s);
    }

    //mengambil data dari database ke form update
    private void getData() {

        final String getNama = getIntent().getExtras().getString("dataNama");
        final String getBank = getIntent().getExtras().getString("dataBank");
        final String getNope = getIntent().getExtras().getString("dataNope");
        final String getKategori = getIntent().getExtras().getString("dataKategori");
        final String getEmail = getIntent().getExtras().getString("dataEmail");
        final String getJk = getIntent().getExtras().getString("dataJeniskelamin");
        final String getGambar = getIntent().getExtras().getString("dataGambar");

        //set Image View
        if (isEmpty(getGambar)){
            updateImg.setImageResource(R.drawable.ic_person);
        }else{
            Glide.with(ActivityUpdate.this)
                    .load(getGambar)
                    .into(updateImg);
        }



        namaBaru.setText(getNama);
      //fakultasBaru.setSelected(getFakultas);
      //prodiBaru.setText(getProdi);
      //  kategoribaru.setText(getKategori);
        nopeBaru.setText(getNope);
        emailBaru.setText(getEmail);


    }
    private void updatePenyumbang(data_penyumbang penyumbang) {

        String getKey = getIntent().getExtras().getString("getPrimaryKey");
        database.child("Admin")
                .child("Penyumbang")
                .child(getKey)
                .setValue(penyumbang)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        namaBaru.setText("");
                        nopeBaru.setText("");
                        emailBaru.setText("");

                        Toast.makeText(ActivityUpdate.this, "Data Berhasil diubah", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
    }

}