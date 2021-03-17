package unicaen.tp.programmationandroid.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import unicaen.tp.programmationandroid.BuildConfig;
import unicaen.tp.programmationandroid.R;
import unicaen.tp.programmationandroid.activities.affichage.FlowLayout;
import unicaen.tp.programmationandroid.classes.Propriete;
import unicaen.tp.programmationandroid.classes.Vendeur;
import unicaen.tp.programmationandroid.db.Db;
import unicaen.tp.programmationandroid.db.DbContract;
import unicaen.tp.programmationandroid.db.DbOpener;

public class DeposerBienActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    // Request image capture
    static final int REQUEST_IMAGE_CAPTURE = 2;
    static final int THUMBNAIL_SIZE = 200;

    public static final int TEXT_NUMBER = 2;
    public static final int TEXT_VILLE = 8305;
    public static final int TEXT_TITLE = 16385;


    String currentPhotoPath;
    List<String> allPhotosPath = new ArrayList<>();
    Uri currentURI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deposer_bien);

        assert getSupportActionBar() != null;   //null check
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.textview_deposer_bien_titre));

        // Add picture button
        FloatingActionButton addPicture = findViewById(R.id.button_deposer_bien_camera);
        addPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("dd_MM_yyyy_hhmmss").format(new Date());
        String imageFileName = timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        allPhotosPath.add(image.getAbsolutePath());
        return image;
    }


    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // takePictureIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            String appId = BuildConfig.APPLICATION_ID;
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(
                        this,
                        appId,
                        photoFile
                );
                currentURI = photoURI;
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            // File root = Environment.getExternalStorageDirectory();
            // ImageView IV = (ImageView) findViewById(R.id."image view");
            // Bitmap bMap = BitmapFactory.decodeFile(root+"/images/01.jpg");
            // IV.setImageBitmap(bMap);

            // ~~~~~~~~~
            Uri anotherURI = currentURI;
            Bitmap thumbnailBitmap = null;
            try {
                thumbnailBitmap = getThumbnail(anotherURI);
            } catch (IOException e) {
                e.printStackTrace();
            }
            // ~~~~~~~~~

            // Bundle extras = data.getExtras();
            // Bitmap imageBitmap = (Bitmap) extras.get("data");
            ImageView imageView = new ImageView(this);
            FlowLayout ll = findViewById(R.id.flowLayout_in_scrollView);
            imageView.setImageBitmap(thumbnailBitmap);
            ll.addView(imageView);
        }
    }
    //solution trouve sur stackoverflow : https://stackoverflow.com/a/6228188
    public Bitmap getThumbnail(Uri uri) throws FileNotFoundException, IOException{
        InputStream input = this.getContentResolver().openInputStream(uri);

        BitmapFactory.Options onlyBoundsOptions = new BitmapFactory.Options();
        onlyBoundsOptions.inJustDecodeBounds = true;
        onlyBoundsOptions.inPreferredConfig=Bitmap.Config.ARGB_8888;//optional
        BitmapFactory.decodeStream(input, null, onlyBoundsOptions);
        input.close();

        if ((onlyBoundsOptions.outWidth == -1) || (onlyBoundsOptions.outHeight == -1)) {
            return null;
        }

        int originalSize = (onlyBoundsOptions.outHeight > onlyBoundsOptions.outWidth) ? onlyBoundsOptions.outHeight : onlyBoundsOptions.outWidth;

        double ratio = (originalSize > THUMBNAIL_SIZE) ? (originalSize / THUMBNAIL_SIZE) : 1.0;

        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        bitmapOptions.inSampleSize = getPowerOfTwoForSampleRatio(ratio);
        bitmapOptions.inPreferredConfig=Bitmap.Config.ARGB_8888;//
        input = this.getContentResolver().openInputStream(uri);
        Bitmap bitmap = BitmapFactory.decodeStream(input, null, bitmapOptions);
        input.close();
        return bitmap;
    }

    private static int getPowerOfTwoForSampleRatio(double ratio){
        int k = Integer.highestOneBit((int)Math.floor(ratio));
        if(k==0) return 1;
        else return k;
    }


    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }


    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    public void lancerActiviteDeposer(View v) {
        Intent intent = new Intent(this, VisualisationActivity.class);

        // Parsing
        // False unique id
        Random rand = new Random();
        int idInt = (rand.nextInt(100) + 1) * (rand.nextInt(50) + 1);
        String id = String.valueOf(idInt);
        // Title
        EditText title = ((EditText) findViewById(R.id.edittext_deposer_bien_title));
        // Description
        EditText description = ((EditText) findViewById(R.id.edittext_deposer_bien_desc));
        // Number of rooms
        EditText nbPieceString = ((EditText) findViewById(R.id.edittext_deposer_bien_select_nb_piece));

        // Caracteristics
        EditText editTextCaract = ((EditText) findViewById(R.id.edittext_deposer_bien_carac));
        List<String> caracteristiques = new ArrayList<>();
        if(!editTextCaract.getText().toString().isEmpty()) {
            String caract = editTextCaract.getText().toString().replaceAll(", ", ",");

            String[] listCarac = caract.split(",");
            caracteristiques = Arrays.asList(listCarac);
        }
        // Price
        EditText priceString = ((EditText) findViewById(R.id.edittext_deposer_bien_select_price));

        // City
        EditText ville = ((EditText) findViewById(R.id.edittext_deposer_bien_ville));
        // Postcode
        EditText codePostal = ((EditText) findViewById(R.id.edittext_deposer_bien_code));


        // Date
        long date = new Date().getTime();

        boolean fieldsOK = validate(new EditText[] { title, nbPieceString,ville,codePostal,priceString });
        if(fieldsOK){
            DbOpener dbo = new DbOpener(getApplicationContext(), DbContract.BD_NAME,null, DbContract.BD_VERSION);
            Db db = new Db(dbo);
            Vendeur vendeur = db.obtenirUtilisateur();
            int nbPiece = Integer.parseInt(nbPieceString.getText().toString());
            int price = Integer.parseInt(priceString.getText().toString());
            Propriete propriete = new Propriete(id, title.getText().toString(), description.getText().toString(), nbPiece, price, ville.getText().toString(), codePostal.getText().toString(), vendeur, date, caracteristiques, allPhotosPath);
            intent.putExtra("Propriete", propriete);
            db.ajouterPropriete(propriete);
            startActivity(intent);
            dbo.close();
        }else{
            Toast.makeText(v.getContext(), getString(R.string.string_erreur_saisie_incomplete), Toast.LENGTH_SHORT).show();
        }

    }

    private boolean validate(EditText[] fields){
        boolean result = true;
        for(int i = 0; i < fields.length; i++){
            EditText currentField = fields[i];
            if(!hasContentEditText(currentField)){
                result = false;
            }
        }
        return result;
    }

    private boolean hasContentEditText(EditText et){
        String s = et.getText().toString();
        int inputTypeValue = et.getInputType();
        if(inputTypeValue == TEXT_NUMBER) {
            Pattern pattern = Pattern.compile(new String("-?\\d+(\\.\\d+)?"));
            Matcher matcher = pattern.matcher(s);
            if (!matcher.matches() || s.isEmpty()) {
                et.setError(getString(R.string.string_erreur_saisie));
                et.setFocusable(true);
                return false;
            }

        }
        if(inputTypeValue == TEXT_VILLE) {
            Pattern pattern = Pattern.compile("^[a-zA-Z ]+$");
            Matcher matcher = pattern.matcher(s);
            if (!matcher.matches() || s.isEmpty()) {
                et.setError(getString(R.string.string_champ_obligatoire));
                et.setFocusable(true);
                return false;
            }

        }
        if(inputTypeValue == TEXT_TITLE) {
            if (s.isEmpty()) {
                et.setError(getString(R.string.string_champ_obligatoire));
                et.setFocusable(true);
                return false;
            }

        }
       return s != null;
    }

}
