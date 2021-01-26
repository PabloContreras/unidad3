package xyz.kainotomia.examenu3.ui.listar;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.Navigation;

import java.util.List;

import xyz.kainotomia.examenu3.R;
import xyz.kainotomia.examenu3.databinding.ListaItemPacientesBinding;
import xyz.kainotomia.examenu3.model.Libro;

public class AdaptadorDeLista extends ArrayAdapter<Libro> {

    public AdaptadorDeLista(Context context, List<Libro> libros) {
        super(context, 0, libros);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        Libro libro = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.lista_item_pacientes, parent, false);
        }

        ListaItemPacientesBinding lip = ListaItemPacientesBinding.bind(convertView);

        lip.nombre.setText(libro.getNombre());

        lip.edad.setText(lip.edad.getText().toString().replace(":autor", libro.getAutor() + ""));
        lip.resena.setText(lip.resena.getText().toString().replace(":detalles", libro.getDetalles() + ""));
        lip.genero.setText(lip.genero.getText().toString().replace(":genero", libro.getGenero()));
        lip.fecha.setText(lip.fecha.getText().toString().replace(":fecha", libro.getFecha()));


        if (libro.getPhotoPath() != null && !libro.getPhotoPath().equals("")) {
            lip.imagen.setImageBitmap(BitmapFactory.decodeFile(libro.getPhotoPath()));
        }


        lip.btnEliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                libro.delete(getContext());
                AdaptadorDeLista.this.remove(libro);

                Toast.makeText(getContext(), "Libro eliminado", Toast.LENGTH_LONG).show();

            }
        });

        lip.btnEditar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Bundle bundle = new Bundle();
                bundle.putString("pacienteID", libro.getUid() + "");
                Navigation.findNavController(parent).navigate(R.id.action_nav_listar_to_nav_crear, bundle);
            }
        });


        return convertView;


    }
}
