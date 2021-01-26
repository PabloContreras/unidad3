package xyz.kainotomia.examenu3.ui.listar;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import java.util.List;

import xyz.kainotomia.examenu3.databinding.FragmentListarBinding;
import xyz.kainotomia.examenu3.model.Libro;

public class ListarFragment extends Fragment {

    FragmentListarBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentListarBinding.inflate(inflater, container, false);

        List<Libro> libros = Libro.getAll(this.getContext());

        binding.list.setAdapter(new AdaptadorDeLista(ListarFragment.this.getContext(), libros));

        return binding.getRoot();
    }
}