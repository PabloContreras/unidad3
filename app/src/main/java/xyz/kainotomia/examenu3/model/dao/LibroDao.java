package xyz.kainotomia.examenu3.model.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import xyz.kainotomia.examenu3.model.Libro;

@Dao
public interface LibroDao {

    @Query("SELECT COUNT(*) FROM LIBROS")
    int count();

    @Query("SELECT * FROM LIBROS")
    List<Libro> getAll();

    @Query("SELECT * FROM LIBROS WHERE UID = :id")
    Libro findById(int id);

    @Insert
    void insert(Libro libro);

    @Delete
    void delete(Libro libro);

    @Update
    void update(Libro libro);

}
