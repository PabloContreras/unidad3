package xyz.kainotomia.examenu3.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import xyz.kainotomia.examenu3.model.Libro;
import xyz.kainotomia.examenu3.model.dao.LibroDao;

@Database(entities = {Libro.class}, version = 3)
public abstract class AppDatabase extends RoomDatabase {

    public abstract LibroDao pacienteDao();


    public static AppDatabase getInstance(Context context) {
        return Room.databaseBuilder(context, AppDatabase.class, "examenU3").allowMainThreadQueries().fallbackToDestructiveMigration().build();
    }

    public static DatabaseReference getFirebaseDbInstance() {
        return FirebaseDatabase.getInstance().getReference();
    }

    public static StorageReference getFirebaseStorageInstance() {
        return FirebaseStorage.getInstance().getReference();
    }

}
