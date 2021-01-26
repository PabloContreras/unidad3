package xyz.kainotomia.examenu3.ui.crear;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import xyz.kainotomia.examenu3.R;
import xyz.kainotomia.examenu3.databinding.FragmentCrearBinding;
import xyz.kainotomia.examenu3.model.Libro;

/**
 * CREATE PACIENTE FRAGMENT
 */
public class CrearFragment extends Fragment {

    /**
     * Spinners
     */
    private final String[] generos = new String[]{"Acción", "Suspenso", "Romance", "Terror", "Ficción"};

    private FragmentCrearBinding binding;
    private Libro libro;

    /**
     * Request Code For Photo
     */
    private static final int REQUEST_TAKE_PHOTO = 1;


    private String currentPhotoPath;

    /**
     * Current Paciente edit
     */
    private boolean editMode;

    private String userid;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCrearBinding.inflate(inflater, container, false);

        userid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myref = database.getReference();
        libro = new Libro();

        binding.imagenPaciente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String[] permisos = new String[]{
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                };

                int use_camera_permission = ActivityCompat.checkSelfPermission(CrearFragment.this.getContext(), permisos[0]);
                int write_external_permission = ActivityCompat.checkSelfPermission(CrearFragment.this.getContext(), permisos[1]);

                if (use_camera_permission == PackageManager.PERMISSION_GRANTED || write_external_permission == PackageManager.PERMISSION_GRANTED) {
                    abrirCamara();
                } else {
                    requestPermissions(permisos, 105);
                }

            }
        });


        /**
         * Spinner genero
         */
        binding.generoPaciente.setAdapter(new ArrayAdapter<String>(this.getContext(), android.R.layout.simple_spinner_dropdown_item, generos));
        binding.generoPaciente.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                libro.setGenero(adapterView.getItemAtPosition(position).toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        /**
         * Fecha ingreso boton
         */
        binding.fechaIngresoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final Calendar c = Calendar.getInstance();
                final int mes = c.get(Calendar.MONTH);
                final int dia = c.get(Calendar.DAY_OF_MONTH);
                final int anio = c.get(Calendar.YEAR);

                new DatePickerDialog(CrearFragment.this.getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int anio, int mes, int dia) {
                        final String fecha = dia + "/" + (mes+1) + "/" + anio;
                        binding.fechaIngreso.setText(fecha);
                        libro.setFecha(fecha);
                    }
                }, anio, mes, dia).show();

            }
        });

        /**
         * Guardar paciente
         */
        binding.guardarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String nombre = binding.nombrePaciente.getText().toString();
                final String autor = binding.edadPaciente.getText().toString();
                final String detalles = binding.resena.getText().toString();
                final String uid = UUID.randomUUID().toString();
                libro.setNombre(nombre);
                libro.setAutor(autor);
                libro.setDetalles(detalles);
                libro.setUuid(uid);

                if (libro.isValid()) {
                    String msj;

                    if (!editMode) {
                        libro.insert(CrearFragment.this.getContext());
                        //myref.child("libros").child(uid).setValue(libro);
                        msj = "Libro insertado correctamente";
                    } else {
                        libro.update(CrearFragment.this.getContext());
                        msj = "Libro actualizado correctamente";
                    }

                    limpiar();
                    Toast.makeText(CrearFragment.this.getContext(), msj, Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(CrearFragment.this.getContext(), "Verifique sus datos", Toast.LENGTH_LONG).show();
                }
            }
        });

        /**
         * Limpiar btn
         */
        binding.limpiarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                limpiar();
            }
        });

        /**
         * Si el fragment se crea en modo edicion recuperar datos del paciente a editar
         */
        editMode = isEditMode(getArguments());
        if (editMode) {
            recoverData();
            binding.guardarBtn.setText("Actualizar");
        }

        return binding.getRoot();
    }

    public void abrirCamara() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            File photoFile = null;

            try {
                photoFile = createImageFile();
            } catch (Exception e) {

            }

            if (photoFile != null) {
                Uri photoUri = FileProvider.getUriForFile(this.getContext(), "com.example.android.fileprovider", photoFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(intent, 100);
            }

        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = this.getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        abrirCamara();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 100) {
            binding.imagenPaciente.setImageBitmap(BitmapFactory.decodeFile(currentPhotoPath));
            libro.setPhotoPath(currentPhotoPath);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }

    private void recoverData() {

        binding.nombrePaciente.setText(libro.getNombre());
        binding.edadPaciente.setText(libro.getAutor() + "");
        binding.fechaIngreso.setText(libro.getFecha());
        binding.resena.setText(libro.getDetalles());

        if (libro.getPhotoPath() != null && !libro.getPhotoPath().equals("")) {
            binding.imagenPaciente.setImageBitmap(BitmapFactory.decodeFile(libro.getPhotoPath()));
        }


        for (int i = 0; i < generos.length; i++) {
            if (libro.getGenero().equals(generos[i])) {
                binding.generoPaciente.setSelection(i);
                break;
            }
        }


    }

    public boolean isEditMode(Bundle arguments) {
        if (arguments != null) {
            String pid = arguments.getString("pacienteID");
            if (pid != null && !pid.equals("")) {
                libro = Libro.findById(this.getContext(), Integer.parseInt(pid));
                return true;
            }
        }
        return false;
    }

    public void limpiar() {

        binding.nombrePaciente.setText("");
        binding.fechaIngreso.setText("");
        binding.edadPaciente.setText("");
        binding.resena.setText("");

        binding.imagenPaciente.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_person_24));

        libro = new Libro();

    }


}