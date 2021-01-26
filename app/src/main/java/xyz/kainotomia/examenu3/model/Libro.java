package xyz.kainotomia.examenu3.model;

import android.content.Context;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import xyz.kainotomia.examenu3.database.AppDatabase;

@Entity(tableName = "libros")
public class Libro {

    @PrimaryKey(autoGenerate = true)
    private int uid;
    @ColumnInfo(name = "uuid")
    private String uuid;


    @ColumnInfo(name = "nombre")
    private String nombre;

    @ColumnInfo(name = "autor")
    private String autor;

    @ColumnInfo(name = "genero")
    private String genero;

    @ColumnInfo(name = "fecha")
    private String fecha;

    @ColumnInfo(name = "photo_path")
    @Nullable
    private String photoPath;

    @ColumnInfo(name = "detalles")
    private String detalles;

    public Libro(String photoPath, String nombre, String genero, String autor, String fecha, String detalles) {
        this.setPhotoPath(photoPath);
        this.setNombre(nombre);
        this.setGenero(genero);
        this.setAutor(autor);
        this.setFecha(fecha);
        this.setDetalles(detalles);
    }

    public Libro() {
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getPhotoPath() {
        return photoPath;
    }

    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }


    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getDetalles() {
        return detalles;
    }

    public void setDetalles(String detalles) {
        this.detalles = detalles;
    }

    public boolean isValid() {
        return getNombre() != null && getGenero() != null && getAutor() != null  && getFecha() != null && getDetalles() != null;
    }


    public void insert(Context context) {
        this.setUuid(UUID.randomUUID().toString());
        //  SQLite
        AppDatabase.getInstance(context).pacienteDao().insert(this);
        //  Firebase
        AppDatabase.getFirebaseDbInstance().child("libros").child(this.getUuid()).setValue(this);
        //  Storage
        if (this.getPhotoPath() != null && !this.getPhotoPath().equals("")) {
            savePhoto();
        }
    }

    public void delete(Context context) {
        //  SQLite
        AppDatabase.getInstance(context).pacienteDao().delete(this);
        //  Firebase
        AppDatabase.getFirebaseDbInstance().child("libros").child(this.getUuid()).removeValue();
        //  Storage
        if (this.getPhotoPath() != null && !this.getPhotoPath().equals("")) {
            deletePhoto();
        }
    }

    public void update(Context context) {
        //  SQLite
        AppDatabase.getInstance(context).pacienteDao().update(this);
        //  Firebase
        AppDatabase.getFirebaseDbInstance().child("libros").child(this.getUuid()).setValue(this);
        //  Storage
        if (this.getPhotoPath() != null && !this.getPhotoPath().equals("")) {
            savePhoto();
        }
    }


    public static List<Libro> getAll(Context context) {
        AppDatabase db = AppDatabase.getInstance(context);

        if (db.pacienteDao().count() != 0) {
            return db.pacienteDao().getAll();
        }

        return new ArrayList<>();
    }


    public static Libro findById(Context context, int id) {
        return AppDatabase.getInstance(context).pacienteDao().findById(id);
    }

    private void savePhoto() {
        StorageReference ref = AppDatabase.getFirebaseStorageInstance();
        Uri file = Uri.fromFile(new File(getPhotoPath()));
        ref.child("images/" + file.getLastPathSegment()).putFile(file).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

            }
        });
    }

    private void deletePhoto() {
        StorageReference ref = AppDatabase.getFirebaseStorageInstance();
        Uri file = Uri.fromFile(new File(getPhotoPath()));
        ref.child("images/" + file.getLastPathSegment()).delete().addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        }).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

            }
        });
    }
}
