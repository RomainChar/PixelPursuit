package uqac.dim.pixelpursuit;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PackHelper {

    private final Context context;
    private final CustomPackDatabaseHelper dbHelper;
    private final XmlResourceParser parser;

    public PackHelper(Context context) {
        this.context = context;
        this.dbHelper = new CustomPackDatabaseHelper(context);
        this.parser = context.getResources().getXml(R.xml.packs);
    }

    public Pack getPack(int id, boolean isCustom) {
        if (isCustom) {
            // Accès à la base de données pour obtenir le pack personnalisé
            return dbHelper.getPackById(id);
        } else {
            // Accès au fichier XML pour obtenir le pack standard
            return getPackFromXmlById(id);
        }
    }

    private Pack getPackFromXmlById(int id) {
        XmlResourceParser parser = context.getResources().getXml(R.xml.packs);
        try {
            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG && "pack".equals(parser.getName())) {
                    Pack pack = new Pack(null, null, null, null, 0, false); // Crée un pack temporaire
                    while (!(eventType == XmlPullParser.END_TAG && "pack".equals(parser.getName()))) {
                        if (eventType == XmlPullParser.START_TAG) {
                            String tagName = parser.getName();
                            switch (tagName) {
                                case "packId":
                                    int currentId = Integer.parseInt(parser.nextText());
                                    pack.setPackId(currentId);
                                    break;
                                case "name":
                                    pack.setTitle(parser.nextText());
                                    break;
                                case "mainImage":
                                    pack.setMainImageFileName(parser.nextText());
                                    break;
                                case "description":
                                    pack.setDescription(parser.nextText());
                                    break;
                                case "secondaryImages":
                                    pack.setSecondaryImageFileNames(parseSecondaryImages(parser).toArray(new String[0]));
                                    break;
                            }
                        }
                        eventType = parser.next();
                    }
                    if (pack.getPackId() == id) {
                        return pack; // Retourne le pack correspondant à l'ID
                    }
                }
                eventType = parser.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            parser.close();
        }
        return null; // Aucun pack trouvé avec l'ID spécifié
    }


    public List<Pack> parsePacks(XmlResourceParser parser, CustomPackDatabaseHelper dbHelper) {
        List<Pack> packs = new ArrayList<>();

        try {
            int eventType = parser.getEventType();
            Pack pack = null;

            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG) {
                    String tagName = parser.getName();

                    if (tagName.equals("pack")) {
                        pack = new Pack(null, null, null, null, 0, false);
                    } else if (pack != null) {
                        switch (tagName) {
                            case "name":
                                pack.setTitle(parser.nextText());
                                break;
                            case "packId":
                                try {
                                    pack.setPackId(Integer.parseInt(parser.nextText()));
                                } catch (NumberFormatException e) {
                                    Log.e("Parse Error", "packId invalide", e);
                                }
                                break;
                            case "mainImage":
                                pack.setMainImageFileName(parser.nextText());
                                break;
                            case "secondaryImages":
                                List<String> secondaryImages = parseSecondaryImages(parser);
                                if (secondaryImages != null) {
                                    pack.setSecondaryImageFileNames(secondaryImages.toArray(new String[0]));
                                }
                                break;
                            case "description":
                                pack.setDescription(parser.nextText());
                                break;
                        }
                    }
                } else if (eventType == XmlPullParser.END_TAG && parser.getName().equals("pack") && pack != null) {
                    packs.add(pack);
                }

                eventType = parser.next();
            }
        } catch (XmlPullParserException | IOException e) {
            e.printStackTrace();
        } finally {
            if (parser != null) parser.close();
        }
        List<Pack> customPacks = dbHelper.getAllPacks();

        packs.addAll(customPacks);
        return packs;
    }

    private List<String> parseSecondaryImages(XmlResourceParser parser) throws IOException, XmlPullParserException {
        List<String> secondaryImages = new ArrayList<>();
        int eventType = parser.next();
        while (eventType != XmlPullParser.END_TAG || !parser.getName().equals("secondaryImages")) {
            if (eventType == XmlPullParser.START_TAG && parser.getName().equals("image")) {
                secondaryImages.add(parser.nextText());
            }
            eventType = parser.next();
        }
        return secondaryImages;
    }

    public Pack getDefaultPack() {
        return getPackFromXmlById(0);
    }

    public Pack getChosenPack() {

        SharedPreferences sharedPreferences = context.getSharedPreferences("MyPrefs", MODE_PRIVATE);

        int selectedPackId = sharedPreferences.getInt("selectedPackId", 0);
        Boolean selectedPackIsCustom = sharedPreferences.getBoolean("selectedPackIsCustom", false);

        return getPack(selectedPackId, selectedPackIsCustom);
    }

    public Boolean isChosenPack(Pack pack){
        Pack chosenPack = getChosenPack();
        if ((chosenPack.getIsCustom() == pack.getIsCustom()) && (chosenPack.getPackId()==pack.getPackId()))
            return true;
        return false;
    }

    public void savePack(Pack pack) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("selectedPackId", (pack.getPackId()));
        editor.putBoolean("selectedPackIsCustom", pack.getIsCustom());
        editor.apply();
    }

    public List<Bitmap> getSecondaryImageBitmaps(Pack pack) {
        String[] imageNames = pack.getSecondaryImageFileNames();
        List<Bitmap> bitmaps = new ArrayList<>();

        if (pack.getIsCustom()) {
            // Charge les images depuis le stockage pour les packs custom
            for (String path : imageNames) {
                Bitmap bitmap = BitmapFactory.decodeFile(path);
                if (bitmap != null) {
                    bitmaps.add(bitmap);
                } else {
                    Log.e("PackHelper", "Impossible de décoder le bitmal à cet URL : " + path);
                }
            }
        } else {
            // Charge les images depuis les ressources pour les packs non custom
            Resources resources = context.getResources();
            for (String name : imageNames) {
                int resId = resources.getIdentifier(name, "drawable", context.getPackageName());
                if (resId != 0) {
                    Bitmap bitmap = BitmapFactory.decodeResource(resources, resId);
                    if (bitmap != null) {
                        bitmaps.add(bitmap);
                    } else {
                        Log.e("PackHelper", "Impossible de décoder le bitmap de cette ressource : " + resId);
                    }
                } else {
                    Log.e("PackHelper", "Aucune ressource trouvée à ce nom : " + name);
                }
            }
        }

        return bitmaps;
    }

    public Bitmap getMainImageBitmap(Pack pack) {
        String imageName = pack.getMainImage();

        if (pack.getIsCustom()) {
            // Charge l'image principale depuis le stockage pour les packs custom
            Bitmap bitmap = BitmapFactory.decodeFile(imageName);
            if (bitmap != null) {
                return bitmap;
            } else {
                Log.e("PackHelper", "Impossible de décoder le bitmap à cet URL : " + imageName);
            }
        } else {
            // Charge les images depuis les ressources pour les packs non custom
            Resources resources = context.getResources();

            int resId = resources.getIdentifier(imageName, "drawable", context.getPackageName());
            if (resId != 0) {
                Bitmap bitmap = BitmapFactory.decodeResource(resources, resId);
                if (bitmap != null) {
                    return bitmap;
                } else {
                    Log.e("PackHelper", "Impossible de décoder le bitmap de cette ressource : " + resId);
                }
            } else {
                Log.e("PackHelper", "Aucune ressource trouvée à ce nom : " + imageName);
            }
        }
        return null;
    }

    public boolean isPackNameTaken(String packName) {
        List<Pack> packs = parsePacks(parser, dbHelper);

        for (Pack pack : packs) {
            if (pack.getTitle().equalsIgnoreCase(packName)) {
                // Le nom du pack est déjà pris
                return true;
            }
        }
        // Le nom du pack n'est pas pris
        return false;
    }

}

