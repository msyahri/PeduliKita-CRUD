package pmo.ananda_20SA1182.pedulikita;

import static android.text.TextUtils.isEmpty;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.UUID;

public class form_data extends AppCompatActivity {

    private ProgressBar progressBar;
    private EditText Nama, Nope, Email;
    private Button Simpan, getFoto;
    private String getNama, getEmail, getNope, getKategori, getJeniskelamin, getGambar;
    private ImageView profilPic;
    private RadioButton radioPria, radioWanita, jk;
    private RadioGroup rgJenisKelamin;
    private CheckBox gol1, gol2;
    public Uri imageurl;
    data_penyumbang data_penyumbang;


    FirebaseStorage storage;
    StorageReference storageReference;

    private static final int REQUEST_CODE_CAMERA = 1;
    private static final int REQUEST_CODE_GALLERY = 2;

    String getBank;
    Spinner Bank;


    DatabaseReference getReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_data);

        data_penyumbang = new data_penyumbang();
        //inisiasi checkbox
        gol1 = findViewById(R.id.gol1);
        gol2 = findViewById(R.id.gol2);

        radioPria = findViewById(R.id.radioPria);
        radioWanita = findViewById(R.id.radioWanita);
        rgJenisKelamin = findViewById(R.id.rgjeniskelamin);


        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        //oinisiasi profil pic
        profilPic = findViewById(R.id.profilPic);

        //Init fto
        getFoto = findViewById(R.id.getfoto);


        //inisialisasi ID (progressbar)
        progressBar = findViewById(R.id.progress);
        progressBar.setVisibility(View.GONE);


        //inisiasi id (button)
        Simpan = findViewById(R.id.save);

        //inisiasi id(spinner)
        Bank = findViewById(R.id.Bank);


        //inisisasi id (edit text)
        Nama = findViewById(R.id.nama);
        Email = findViewById(R.id.Email);
        Nope = findViewById(R.id.nope);


        //mendapatkan instance dari database
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        //mendapatkan referensi dari database
        getReference = database.getReference();

        //fungsi upload foto
        profilPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, 2);


            }
        });


        //membuat fungsi tombol simpan

        Simpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                int pilihjk = rgJenisKelamin.getCheckedRadioButtonId();
                jk = findViewById(pilihjk);
                getNama = Nama.getText().toString();
                getBank = Bank.getSelectedItem().toString();
                getEmail = Email.getText().toString();
                getNope = Nope.getText().toString();

              //  getJeniskelamin = jk.getText().toString();
                if(radioPria.isChecked()){
                    getJeniskelamin = jk.getText().toString();
                }else{

                }
                if(radioWanita.isChecked()){
                    getJeniskelamin = jk.getText().toString();
                }else{

                }

                if (gol1.isChecked()) {
                    getKategori = gol1.getText().toString();
                } else {

                }
                if (gol2.isChecked()) {
                    getKategori = gol2.getText().toString();
                }


                checkUser();
                progressBar.setVisibility(View.VISIBLE);
            }
        });


        getFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getImage();
            }
        });

        gol1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    gol2.setChecked(false);
                }
            }
        });

        gol2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    gol1.setChecked(false);
                }
            }
        });
    }

    private void getImage() {
        Intent imageIntenGallery = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(imageIntenGallery, 2);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_CAMERA:
                if (resultCode == RESULT_OK) {
                    profilPic.setVisibility(View.VISIBLE);
                    Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                    profilPic.setImageBitmap(bitmap);
                }
                break;

            case REQUEST_CODE_GALLERY:
                if (resultCode == RESULT_OK) {
                    profilPic.setVisibility(View.VISIBLE);
                    Uri uri = data.getData();
                    profilPic.setImageURI(uri);
                }
                break;
        }
    }

    private void uploadToFirebase(Uri uri) {

        StorageReference fileRef = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(uri));
        fileRef.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                         Toast.makeText(form_data.this, "Berhasil Upload", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                 Toast.makeText(form_data.this, "Gagal Upload", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String getFileExtension(Uri mUri) {

        ContentResolver cr = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(mUri));
    }


    private void checkUser() {

        if (isEmpty(getNama) || isEmpty(getEmail) || isEmpty(getNope))   {
             Toast.makeText(form_data.this, "Data tidak boleh ada yang kosong", Toast.LENGTH_SHORT).show();
        } else {
            profilPic.setDrawingCacheEnabled(true);
            profilPic.buildDrawingCache();
            Bitmap bitmap = ((BitmapDrawable) profilPic.getDrawable()).getBitmap();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();

            //mengkompres Bitmap menjadi JPG dengan kualitas gambar 100%
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            byte[] bytes = stream.toByteArray();

            //Lokasi lengkap dimana gambar akan disimpan
            String namaFile = UUID.randomUUID() + ".jpg";
            final String pathImage = "Gambar/" + namaFile;
            UploadTask uploadTask = storageReference.child(pathImage).putBytes(bytes);
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {


                            getReference.child("Admin").child("Penyumbang").push()
                                    .setValue(new data_penyumbang(getNama, getBank, getNope, getEmail, getKategori, getJeniskelamin, uri.toString().trim()))
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            Toast.makeText(form_data.this, "Data Tersimpan", Toast.LENGTH_SHORT).show();
                                            progressBar.setVisibility(View.GONE);
                                            finish();

                                        }
                                    });
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressBar.setVisibility(View.GONE);
                   Toast.makeText(form_data.this, "Gagal Upload", Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                    progressBar.setVisibility(View.VISIBLE);
                    double progress = (100.0 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
                    progressBar.setProgress((int) progress);
                }
            });
        }
    }
}